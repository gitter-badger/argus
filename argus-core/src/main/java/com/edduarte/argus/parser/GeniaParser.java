/*
 * Copyright 2014 Ed Duarte
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edduarte.argus.parser;

import com.edduarte.argus.stemmer.Stemmer;
import com.edduarte.argus.stopper.Stopper;
import com.edduarte.argus.util.Constants;
import com.edduarte.argus.util.ProcessWrapper;
import com.aliasi.util.Pair;
import it.unimi.dsi.lang.MutableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * High performance dependency parser wrapper implementation, currently being used
 * only for tokenization purposes.
 *
 * @author Ed Duarte (<a href="mailto:ed@edduarte.com">ed@edduarte.com</a>)
 * @version 1.3.0
 * @since 1.0.0
 */
public class GeniaParser implements Parser {

    private static final Logger logger = LoggerFactory.getLogger(GeniaParser.class);

    private static final String BINARY_WIN = "win32.exe";

    private static final String BINARY_LIN64 = "./linux64";

    private static final String BINARY_LIN32 = "./linux32";

    private static final String BINARY_MAC = "./mac64";

    private PipedInputStream pis;

    private PipedOutputStream sink;

    private PipedOutputStream pos;

    private PipedInputStream source;

    private BufferedReader br;

    private BufferedWriter bw;

    private ProcessWrapper pc;

    private SentenceSplitter splitter;


    public GeniaParser() throws IOException {
        this.splitter = new SentenceSplitter();
        File dir = Constants.PARSER_DIR;

        String[] parserCommand = new String[]{
                new File(dir, getCompatibleBinary()).getAbsolutePath(),
                "-tok"
        };

        pis = new PipedInputStream();
        sink = new PipedOutputStream(pis);
        pos = new PipedOutputStream();
        source = new PipedInputStream(pos);
        br = new BufferedReader(new InputStreamReader(source));
        bw = new BufferedWriter(new OutputStreamWriter(sink));
        pc = new ProcessWrapper(pis, pos, new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        }));

        pc.create(dir, parserCommand);
    }


    private static String getCompatibleBinary() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            return BINARY_WIN;
        } else if (os.contains("mac")) {
            return BINARY_MAC;
        } else {
            if (System.getProperty("os.arch").contains("64"))
                return BINARY_LIN64;
            else
                return BINARY_LIN32;
        }
    }


    /**
     * Terminates this parser.
     */
    @Override
    public void close() {
        try {
            pis.close();
            pis = null;
            sink.close();
            sink = null;
            pos.close();
            pos = null;
            source.close();
            source = null;
            br.close();
            br = null;
            bw.close();
            bw = null;
            pc.destroy();
            System.gc();

        } catch (IOException ex) {
            logger.error("Error terminating Genia parser: " + ex.toString());
        }
    }


    @Override
    public List<Result> parse(final MutableString text,
                              final Stopper stopper,
                              final Stemmer stemmer,
                              final boolean ignoreCase) {
        List<Result> _results = new ArrayList<>();

        int[][] idx = splitter.split(text);
        List<Pair<Integer, Integer>> sentenceIndexList = new ArrayList<>();

        for (int[] split : idx) {
            int start = split[0];
            int end = split[1];
            sentenceIndexList.add(new Pair<>(start, end));
        }

        try {
            int start, end, tokenCounter = 0;
            for (Pair<Integer, Integer> pair : sentenceIndexList) {
                MutableString sentenceText = text.substring(pair.a(), pair.b());

                List<Object> parserOutput = new ArrayList<>();

                bw.write("tok|" + sentenceText.trim().toString() + "\n");
                bw.flush();

                while (!br.ready()) {
                    // wait for results
                    Thread.yield();
                }
                String line;

                while (!(line = br.readLine()).equals("")) {
                    parserOutput.add(line);
                }

                int offset = 0;
                for (Object result : parserOutput) {
                    String[] parts = result.toString().split("\t");

                    // Get parsing results
                    MutableString tokenText = new MutableString(parts[1]);

                    // Get start and end source with white spaces
                    start = sentenceText.indexOf(tokenText, offset);
                    end = start + tokenText.length();

                    if (ignoreCase) {
                        tokenText.toLowerCase();
                    }

                    // checks if the text is a stopword
                    // if true, do not stem it nor add it to the ParserResult list
                    boolean isStopword = false;
                    if (stopper != null) {
                        MutableString textToTest = tokenText;
                        if (!ignoreCase) {
                            textToTest = textToTest.copy().toLowerCase();
                        }
                        isStopword = stopper.isStopword(textToTest);
                    }
                    if (!isStopword) {

                        // stems the term text
                        if (stemmer != null) {
                            stemmer.stem(tokenText);
                        }

                        _results.add(new Result(tokenCounter++, start, end, tokenText));
                    }

                    // Offsets
                    offset = end + 1;
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return _results;
    }
}
