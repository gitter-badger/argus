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

package com.edduarte.argus.reader;

import com.edduarte.argus.reader.PlainTextReader;
import it.unimi.dsi.lang.MutableString;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Ed Duarte (<a href="mailto:ed@edduarte.com">ed@edduarte.com</a>)
 * @version 2.0.0
 * @since 1.0.0
 */
public class PlainTextReaderTest {

    @Test
    public void testWikipediaPlain() throws Exception {
        InputStream input = getClass().getResourceAsStream("wikipedia.txt");

        Reader reader = new PlainTextReader();
        MutableString text = reader.readDocumentContents(input);

        assertEquals("Argus Panoptes\n" +
                        "From Wikipedia, the free encyclopedia\n" +
                        "\n" +
                        "Io (as cow) and Argus, black-figure amphora, 540–530 BC, Staatliche Antikensammlungen (Inv. 585).\n" +
                        "Argus Panoptes (or Argos) is the name of the 100-eyed giant in Greek mythology.\n" +
                        "\n" +
                        "Contents\n" +
                        "\n" +
                        "1 Mythology\n" +
                        "2 In popular culture\n" +
                        "3 Notes\n" +
                        "4 External links\n" +
                        "Mythology\n" +
                        "\n" +
                        "Argus Panoptes (Ἄργος Πανόπτης), guardian of the heifer-nymph Io and son of Arestor,[1] was a primordial giant whose epithet, \"Panoptes\", \"all-seeing\", led to his being described with multiple, often one hundred, eyes. The epithet Panoptes was applied to the Titan of the Sun, Helios, and was taken up as an epithet by Zeus, Zeus Panoptes. \"In a way,\" Walter Burkert observes, \"the power and order of Argos the city are embodied in Argos the neatherd, lord of the herd and lord of the land, whose name itself is the name of the land.\"[2]\n" +
                        "\n" +
                        "The epithet Panoptes, reflecting his mythic role, set by Hera as a very effective watchman of Io, was described in a fragment of a lost poem Aigimios, attributed to Hesiod:[3]\n" +
                        "\n" +
                        "And set a watcher upon her, great and strong Argos, who with four eyes looks every way. And the goddess stirred in him unwearying strength: sleep never fell upon his eyes; but he kept sure watch always.\n" +
                        "\n" +
                        "In the 5th century and later, Argus' wakeful alertness was explained for an increasingly literal culture as his having so many eyes that only a few of the eyes would sleep at a time: there were always eyes still awake. In the 2nd century AD Pausanias noted at Argos, in the temple of Zeus Larissaios, an archaic image of Zeus with a third eye in the center of his forehead, allegedly Priam's Zeus Herkeios purloined from Troy.[4] According to Ovid, to commemorate her faithful watchman, Hera had the hundred eyes of Argus preserved forever, in a peacock's tail.[5]\n" +
                        "\n" +
                        "\n" +
                        "Argus dozes off: Velázquez renders the theme of stealth and murder in modern dress, 1659 (Prado)\n" +
                        "Argus was Hera's servant. His great service to the Olympian pantheon was to slay the chthonic serpent-legged monster Echidna as she slept in her cave.[6] Hera's defining task for Argus was to guard the white heifer Io from Zeus, keeping her chained to the sacred olive tree at the Argive Heraion.[7] She charged him to \"Tether this cow safely to an olive-tree at Nemea\". Hera knew that the heifer was in reality Io, one of the many nymphs Zeus was coupling with to establish a new order. To free Io, Zeus had Argus slain by Hermes. Hermes, disguised as a shepherd, first put all of Argus's eyes asleep with spoken charms, then slew him by hitting him with a stone, the first stain of bloodshed among the new generation of gods.[8]\n" +
                        "\n" +
                        "The myth makes the closest connection of Argos, the neatherd, with the bull. In the Library of pseudo-Apollodorus, \"Argos killed the bull that ravaged Arcadia, then clothed himself in its skin.\"[9]\n" +
                        "\n" +
                        "The sacrifice of Argos liberated Io and allowed her to wander the earth, although tormented by a gadfly sent by Hera.\n" +
                        "\n" +
                        "In popular culture\n" +
                        "\n" +
                        "The Argus was a daily newspaper in Melbourne, Australia, that was published between 1846 and 1957.\n" +
                        "Alternative rock band Ween's eighth studio album Quebec has a song entitled \"The Argus\", which refers to the Argus' many eyes.\n" +
                        "Argus is the title of the Wishbone Ash's third album.\n" +
                        "Argus is featured in the Percy Jackson & the Olympians series of books as Camp Half-Blood's security guard.\n" +
                        "The Argus Array was a multi-aperture space telescope in Star Trek.\n" +
                        "Argus is mentioned in the Irish poet Antoine Ó Raifteiri's poem 'An Pótaire ag Moladh an Uisce Beatha'.\n" +
                        "J.K. Rowling, author of the Harry Potter novels, gave the name Argus Filch to the caretaker of Hogwarts School of Witchcraft and Wizardry.[10]\n" +
                        "The fifteenth colossus from the video game Shadow of the Colossus is called Argus and nicknamed \"The Sentinel\" and \"Vigilant Guard\". The hundreds of eyes carved into the temple that he resides in refers to the omnividence (all-seeing ability) of Argus Panoptes and the watchful colossus himself.\n" +
                        "A once highly sought Notorious Monster from the video game Final Fantasy XI is called Argus. It has close to a dozen visible eyes and drops an accuracy enchanting necklace.\n" +
                        "One of the monsters from Kyōryū Sentai Zyuranger and its American counterpart Mighty Morphin Power Rangers is based on Argos. It is called \"Dora Argos\" in Japanese, in Power Rangers it is called \"Eye Guy\" and is a creature composed entirely of eyeballs.\n" +
                        "Similarly, Argus Panoptes served as the inspiration for one of the Kaijin from Kamen Rider Wizard, the Phantom Argos.\n" +
                        "Argus is the name of Jack's pet peacock on the NBC TV show 30 Rock. Jack believes Argus to be Don Giess' spirit watching over him.\n" +
                        "In the mobile video game God of War: Betrayal, Argos is featured as the giant pet of Hera.\n" +
                        "In the novel \"Luka and the Fire of Life\", by Salman Rushdie, Argus Panoptes is one of the five appointed guardians of the 'Fire of Life'.\n" +
                        "Argus is the name of a Macedonian heavy metal band, formed in 1987.\n" +
                        "Argus is the name of a fictional PMC in the video games Splinter Cell: Pandora Tomorrow and Splinter Cell: Chaos Theory.\n" +
                        "In the video game Skullgirls, a character named Peacock is equipped with the Argus System. It allows her to see as well as use her blockbuster, Argus Agony.\n" +
                        "In indie game La-Mulana, Argos appears as a blue giant that can only be defeated by weapon called Serpent Staff.\n" +
                        "Argus was the name of a character created for DC Comics \"Bloodlines\" event, appearing in Flash Annual #6 and later his own limited mini-series. He was depicted as a vigilante who turned completely invisible when not in direct light, and his eyes could see every spectrum of light, including X-ray and ultraviolet.\n" +
                        "Argus Panoptes was featured in Marvel Comics. He was revived by Hera to be in charge of the Panopticon (a computer surveillance system that was set up to help defend New Olympus).[11]\n" +
                        "A.R.G.U.S. (Advanced Research Group Uniting Superhumans) is the name of a government organization in the fictional DC Universe. The name stems from the secondary objective of the organization, which is to watch for threats should the Justice League ever fail.[12]\n" +
                        "Notes\n" +
                        "\n" +
                        "Jump up ^ Therefore called Arestorides (Pseudo-Apollodorus, Bibliotheca ii.1.3, Apollonius Rhodius i.112, Ovid Metamorphoses i.624). According to Pausanias (ii.16.3), Arestor was the consort of Mycene, the eponymous nymph of nearby Mycenae.\n" +
                        "Jump up ^ Walter Burkert, Homo Necans (1972) 1983:166-67.\n" +
                        "Jump up ^ Hesiodic Aigimios, fragment 294, reproduced in Merkelbach and West 1967 and noted in Burkert 1983:167 note 28.\n" +
                        "Jump up ^ Pausanias, 2.24.3. (noted by Burkert 1983:168 note 28).\n" +
                        "Jump up ^ Ovid I, 625. The peacock is an Eastern bird, unknown to Greeks before the time of Alexander.\n" +
                        "Jump up ^ Homer, Iliad ii.783; Hesiod, Theogony, 295ff; Pseudo-Apollodorus, Bibliotheca ii.i.2).\n" +
                        "Jump up ^ Pseudo-Apollodorus, Bibliotheke, 2.6.\n" +
                        "Jump up ^ Hermes was tried, exonerated, and earned the epithet Argeiphontes, \"killer of Argos\".\n" +
                        "Jump up ^ Pseudo-Apollodorus, Bibliotheke, 2.4.\n" +
                        "Jump up ^ Rowling, J.K. (1997). Harry Potter and the Philosopher's Stone.\n" +
                        "Jump up ^ Incredible Hercules #138\n" +
                        "Jump up ^ http://www.dccomics.com/comics/forever-evil-argus-2013/forever-evil-argus-1\n" +
                        "External links\n" +
                        "\n" +
                        "\tWikimedia Commons has media related to Argus Panoptes.\n" +
                        "Theoi Project - Gigante Argos Panoptes\n" +
                        "Warburg Institute Iconographic Database (ca 250 images of Io and Argus)\n" +
                        "Retrieved from \"http://en.wikipedia.org/w/index.php?title=Argus_Panoptes&oldid=637267009\"\n" +
                        "Categories: Ancient ArgosArcadian mythologyMythology of ArgosMonstersGreek giantsGreek legendary creatures\n" +
                        "This page was last modified on 9 December 2014 at 03:17.\n" +
                        "Text is available under the Creative Commons Attribution-ShareAlike License; additional terms may apply. By using this site, you agree to the Terms of Use and Privacy Policy. Wikipedia® is a registered trademark of the Wikimedia Foundation, Inc., a non-profit organization.",
                text.toString());
    }
}
