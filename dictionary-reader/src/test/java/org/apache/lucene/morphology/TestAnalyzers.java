/**
 * Copyright 2009 Alexander Kuznetsov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.morphology;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.morphology.analyzer.MorphologyAnalyzer;
import org.apache.lucene.morphology.analyzer.MorphologyFilter;
import org.apache.lucene.morphology.english.EnglishAnalyzer;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;

public class TestAnalyzers extends BaseTokenStreamTestCase {

    @Test
    public void shouldGiveCorrectWordsForEnglish() throws IOException {
        Analyzer morphologyAnalyzer = new EnglishAnalyzer();
        String answerPath = "/english/english-analyzer-answer.txt";
        String testPath = "/english/english-analyzer-data.txt";

        testAnalyzer(morphologyAnalyzer, answerPath, testPath);
    }

    @Test
    public void shouldGiveCorrectWordsForRussian() throws IOException {
        Analyzer morphologyAnalyzer = new RussianAnalyzer();
        String answerPath = "/russian/russian-analyzer-answer.txt";
        String testPath = "/russian/russian-analyzer-data.txt";

        testAnalyzer(morphologyAnalyzer, answerPath, testPath);
    }

    @Test
    public void emptyStringTest() throws IOException {
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();

        MorphologyAnalyzer russianAnalyzer = new MorphologyAnalyzer(russianLuceneMorphology);
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream("тест пм тест".getBytes()), StandardCharsets.UTF_8);
        TokenStream stream = russianAnalyzer.tokenStream(null, reader);
        MorphologyFilter englishFilter = new MorphologyFilter(stream, englishLuceneMorphology);

        englishFilter.reset();
        while (englishFilter.incrementToken()) {
            System.out.println(englishFilter);
        }
    }

    @Test
    public void shouldProvideCorrectIndentForWordWithMelitaForm() throws IOException {
        Analyzer morphologyAnalyzer = new RussianAnalyzer();
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream("принеси мне вина на новый год".getBytes()), StandardCharsets.UTF_8);

        TokenStream tokenStream = morphologyAnalyzer.tokenStream(null, reader);
        tokenStream.reset();
        Set<String> formsOfWine = new HashSet<>();
        formsOfWine.add("вина");
        formsOfWine.add("винo");
        boolean wordSeen = false;
        while (tokenStream.incrementToken()) {
            CharTermAttribute charTerm = tokenStream.getAttribute(CharTermAttribute.class);
            PositionIncrementAttribute position = tokenStream.getAttribute(PositionIncrementAttribute.class);
            if (formsOfWine.contains(charTerm.toString()) && wordSeen) {
                org.hamcrest.MatcherAssert.assertThat(position.getPositionIncrement(), equalTo(0));
            }
            if (formsOfWine.contains(charTerm.toString())) {
                wordSeen = true;
            }
        }
    }

    private void testAnalyzer(Analyzer morphologyAnalyzer, String answerPath, String testPath) throws IOException {
        InputStream stream = this.getClass().getResourceAsStream(answerPath);
        assertNotNull(stream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String[] strings = bufferedReader.readLine().replaceAll(" +", " ").trim().split(" ");
        HashSet<String> answer = new HashSet<>(Arrays.asList(strings));
        stream.close();

        stream = this.getClass().getResourceAsStream(testPath);
        assertNotNull(stream);

        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

        TokenStream tokenStream = morphologyAnalyzer.tokenStream(null, reader);
        tokenStream.reset();
        HashSet<String> result = new HashSet<>();
        while (tokenStream.incrementToken()) {
            CharTermAttribute attribute1 = tokenStream.getAttribute(CharTermAttribute.class);
            result.add(attribute1.toString());
        }

        stream.close();

        org.hamcrest.MatcherAssert.assertThat(result, equalTo(answer));
    }

    @Test
    public void testPositionIncrement() throws IOException {
        EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
        assertTokenStreamContents(
                englishAnalyzer.tokenStream("test", "There are tests!"),
                new String[]{"there", "are", "be", "test"},
                new int[]{0, 6, 6, 10},
                new int[]{5, 9, 9, 15},
                new String[]{"<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>", "<ALPHANUM>"},
                new int[]{1, 1, 0, 1}
        );
    }

    @Test
    public void testKeywordHandling() throws IOException {
        Analyzer analyzer = new EnglishKeywordAnalyzerTest();
        assertTokenStreamContents(
                analyzer.tokenStream("test", "Tests shouldn't be stemmed, but tests should!"),
                new String[]{"tests", "shouldn't", "be", "stem", "but", "test", "shall"}
        );
    }

    private static class EnglishKeywordAnalyzerTest extends Analyzer {
        @Override
        protected TokenStreamComponents createComponents(String s) {
            StandardTokenizer src = new StandardTokenizer();
            CharArraySet dontStem = new CharArraySet(1, false);
            dontStem.add("Tests");
            TokenFilter filter = new SetKeywordMarkerFilter(src, dontStem);
            filter = new LowerCaseFilter(filter);
            try {
                filter = new MorphologyFilter(filter, new EnglishLuceneMorphology());
            } catch (IOException ex) {
                throw new RuntimeException("cannot create EnglishLuceneMorphology", ex);
            }
            return new TokenStreamComponents(src, filter);
        }
    }
}
