package argus.index;

import argus.query.Query;
import argus.query.QueryResult;
import argus.query.Search;
import argus.query.vectormodel.DocumentVector;
import argus.query.vectormodel.MergedAxe;
import argus.query.vectormodel.MergedAxeGroup;
import argus.query.vectormodel.QueryVector;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.LinkedHashMultimap;
import it.unimi.dsi.lang.MutableString;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static argus.util.Util.difference;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

/**
 * A DocumentCollection represents the widest information unit, and has direct
 * access to every collected document and terms.
 * <p/>
 * This class was named Corpora in the previous assignment.
 *
 * @author Eduardo Duarte (<a href="mailto:eduardo.miguel.duarte@gmail.com">eduardo.miguel.duarte@gmail.com</a>)
 * @version 1.0
 */
public final class Collection {

    private static final Logger logger = LoggerFactory.getLogger(Collection.class);


    /**
     * Local and cached map of document IDs (integers) to document objects.
     */
    private final LoadingCache<Integer, Document> documents;


    /**
     * Cached map of token texts to token indexes. This map loads terms from
     * local index files when requested for retrieval, and stores these in cache
     * for 20 seconds (counting from its last usage).
     */
    private final LoadingCache<String, Term> index;

    /**
     * The total number of documents in the collection.
     */
    private final int N;


    Collection(final LoadingCache<Integer, Document> documents,
               final LoadingCache<String, Term> index,
               final int N) {
        this.documents = documents;
        this.index = index;
        this.N = N;
    }


    /**
     * Immediately commands this index to clear the terms stored in memory cache.
     * Every retrieval of terms performed after this will require reading the
     * local index file again.
     */
    public void clearTokensCache() {
        index.invalidateAll();
    }


    /**
     * Returns the estimated size of documents that match the specified search.
     * This method required less computational effort to obtain the total number of results
     * The estimated result set size is equals to N · df(a)/N · df(b)/N · ...
     */
    public int getEstimatedResultSetSize(Set<Term> terms) {
        if (terms.isEmpty()) {
            return 0;
        }
        double N = getN();
        return (int) (N * terms.stream()
                .mapToDouble(t -> ((double) t.getDocumentFrequency()) / N)
                .reduce((x, y) -> x * y) // multiply all document frequencies
                .getAsDouble()
        );
    }


    /**
     * Converts the specified document id into a document object, by reading it
     * from its local file / cache.
     */
    public Document getDocumentForId(Integer documentId) {
        try {
            // the 'get' method will look for any document in the local files or
            // temporary cache that is equal to the specified id
            return documents.get(documentId);
        } catch (ExecutionException | CacheLoader.InvalidCacheLoadException ex) {
            // if this exception occurs, then no occurrences of the specified
            // document were found in this collection
            return null;
        }
    }


    /**
     * Converts the specified input text into a index term, by reading it from its
     * local file / cache.
     */
    public Term getTermForText(MutableString text) {
        try {
            // the 'get' method will look for any term in the local files or
            // temporary cache that is equal to the specified text, which, at this
            // point, should already be stopped and stemmed (if enabled)
            return index.get(text.toString());
        } catch (ExecutionException | CacheLoader.InvalidCacheLoadException ex) {
            // if this exception occurs, then no occurrences of the specified term
            // were found in this collection
            return null;
        }
    }


    /**
     * Processes the specified query in this index, finding results, sorting
     * them using a tf-idf weighting scheme and limiting them by proximity of
     * different terms according to the query's proximity slop. If slop is <= 0,
     * the results are not filtered and every match is returned.
     */
    public QueryResult search(Query query) {
//        Search search = new Search(this, query, resultConsumer);
//        search.run();

        Stopwatch sw = Stopwatch.createStarted();

        // Obtains a set of all terms that exist in this index and
        // that were inserted in the search, converting the query terms into
        // index terms that contain document occurrences.
        // This retrieval is performed in concurrent threads.
        Set<Term> queryTerms = query.textStream()
                .parallel()
                .map(this::getTermForText)
                .filter(term -> term != null)
                .collect(toSet());


        // Convert the query into a vector in the vector-space model
        QueryVector queryVector = new QueryVector(this, query, queryTerms);
        Map<Term, QueryVector.Axe> queryNormalizedAxes = queryVector
                .getWeightedAxes()
                .collect(toMap(QueryVector.Axe::getTerm, nt -> nt));


        // For each query term, obtain the occurring documents and convert the
        // document-to-term pairings to document vectors in the vector-space.
        // Since the Term t is contained in Query q, and since Document d contains t,
        // then, by transitive relation: (t ∈ q) ∩ (t ∈ d) = t ∈ (q ∩ d).
        // Hence, the document vectors below will only contain terms that exist in
        // the query as well, and we wont be needing to deal with an entire vector
        // space composed of every term (matching and non-matching) in the
        // collection.
        Stream<DocumentVector> documentVectorStream = queryTerms
                .stream()
                .flatMap(term -> term.getOccurringDocuments()
                        .stream()
                        .map(docId -> Pair.of(docId, term)))
                .collect(groupingByConcurrent(Pair::getKey, toSet()))
                .entrySet()
                .stream()
                .map((Map.Entry<Integer, Set<Pair<Integer, Term>>> entry) -> {
                    Document document = this.getDocumentForId(entry.getKey());
                    return document == null ? null : new DocumentVector(
                            this,
                            document,
                            entry.getValue()
                                    .stream()
                                    .map(Pair::getValue)
                                    .collect(toSet())
                    );
                })
                .filter(docVector -> docVector != null);


        // merges document vectors with the query vector, and groups these by
        // document. Vector merging consists in normalizing each axe (that is,
        // obtaining the nlize value for each term in both the query and the
        // matched documents) and by multiplying these in pairs (Wd(t1) * Wq(t1))
        Map<Document, List<MergedAxe>> groupedAxes = documentVectorStream
                .flatMap(DocumentVector::getWeightedAxes)
                .map((DocumentVector.Axe docAxe) -> {

                    // retrieves the query-vector axe whose term
                    // corresponds to the term from this document vector
                    Term commonTerm = docAxe.getTerm();
                    QueryVector.Axe queryAxe = queryNormalizedAxes.get(commonTerm);

                    // merges both axes by multiplying both nlize values and
                    // obtain a score for the document of this docAxe
                    return MergedAxe.mergeOf(docAxe, queryAxe);
                })
                .collect(groupingBy(MergedAxe::getDocument));


        // for each document d, calculates the ∑ score(d) and sorts the documents by
        // this value
        List<MergedAxeGroup> sortedDocuments = groupedAxes.entrySet()
                .stream()
                .map(mergedAxeEntry ->
                        // groups the merged axes by document, calculating a score
                        // sum for each document
                        MergedAxeGroup.group(
                                mergedAxeEntry.getKey(),
                                mergedAxeEntry.getValue()
                        ))
                .sorted((o1, o2) -> Double.compare(o2.getScoreSum(), o1.getScoreSum()))
                .collect(toList());

        groupedAxes.clear();
        groupedAxes = null;
        queryNormalizedAxes.clear();

//        // drop the final sorted results into a linked hash multimap, whose
//        // order is equivalent to the insertion order
        LinkedHashMultimap<Document, Term> finalResults = LinkedHashMultimap.create();
        int slop = query.getSlop();

        documentProximityFilter:
        for (MergedAxeGroup scoredDoc : sortedDocuments) {
            int docId = scoredDoc.getDocument().getId();

            if (slop <= 0 || queryTerms.size() == 1) {
                // slop 0 or single term queries mean that, by default, no
                // documents are filtered out of the query results
                finalResults.putAll(scoredDoc.getDocument(), scoredDoc.getTerms());

            } else {
                // should add this document to the results ONLY IF ALL of the
                // queried terms occur AT LEAST ONCE within the specified slop.
                // In other words, a document is only returned if BOTH of the
                // conditions below are met:
                // 1st) the document d has simultaneously all query terms (t1, t2 and
                //      t3, for example)
                // 2nd) for the term t1, there must exist at least one occurrence of
                //      t1 whose distance to a occurrence of t2 and of t3 is <= slop

                queryTerms.removeAll(scoredDoc.getTerms());
                if (!queryTerms.isEmpty()) {
                    // failed the 1st condition - this document does not contain all
                    // of the query terms!
                    continue documentProximityFilter;
                }

                for (Term t1 : scoredDoc.getTerms()) {
                    boolean isNearOccurrence = scoredDoc.getTerms()
                            .stream()
                            .filter(term -> !t1.equals(term))
                            .anyMatch((Term t2) -> {
                                Set<Occurrence> o1 = t1.getOccurencesInDocument(docId);
                                Set<Occurrence> o2 = t2.getOccurencesInDocument(docId);

                                // we need to check if there is AT LEAST ONE
                                // occurrence in o1 that is within the slop of
                                // AT LEAST ONE occurrence in o2.
                                return o1.stream().mapToInt(Occurrence::getPhrasePosition).anyMatch(position1 -> {
                                    return o2.stream().mapToInt(Occurrence::getPhrasePosition).anyMatch(position2 -> {
                                        return difference(position1, position2) <= slop;
                                    });
                                });
                            });

                    if (!isNearOccurrence) {
                        // failed the 2nd condition - one of the terms of the
                        // query did not occur near the other terms in the document!
                        continue documentProximityFilter;
                    }
                }

                // if this point is reached, then the document meets both of the
                // above conditions
                finalResults.putAll(scoredDoc.getDocument(), scoredDoc.getTerms());
            }
        }

        sw.stop();

        return new QueryResult(finalResults, sw.toString());
    }


    /**
     * Returns the number of documents in the collection.
     */
    public int getN() {
        return N;
    }

}
