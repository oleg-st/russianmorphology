/**
 * Copyright 2009 Alexander Kuznetsov 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.morphology.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.morphology.LetterDecoderEncoder;
import org.apache.lucene.morphology.LuceneMorphology;

import java.io.IOException;
import java.io.InputStream;

public class MorphologyAnalyzer extends Analyzer {
    private final LuceneMorphology luceneMorph;

    public MorphologyAnalyzer(LuceneMorphology luceneMorph) {
        this.luceneMorph = luceneMorph;
    }

    public MorphologyAnalyzer(String pathToMorph, LetterDecoderEncoder letterDecoderEncoder) throws IOException {
        luceneMorph = new LuceneMorphology(pathToMorph, letterDecoderEncoder);
    }

    public MorphologyAnalyzer(InputStream inputStream, LetterDecoderEncoder letterDecoderEncoder) throws IOException {
        luceneMorph = new LuceneMorphology(inputStream, letterDecoderEncoder);
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        StandardTokenizer src = new StandardTokenizer();
        TokenFilter filter = new LowerCaseFilter(src);
        filter = new MorphologyFilter(filter, luceneMorph);
        return new TokenStreamComponents(src, filter);
    }
}
