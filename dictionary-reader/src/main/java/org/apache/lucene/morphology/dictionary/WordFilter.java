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

package org.apache.lucene.morphology.dictionary;

import java.io.IOException;
import java.util.List;


abstract public class WordFilter implements WordProcessor {
    private final WordProcessor wordProcessor;

    public WordFilter(WordProcessor wordProcessor) {
        this.wordProcessor = wordProcessor;
    }

    abstract public List<WordCard> transform(WordCard wordCard);

    public void process(WordCard wordCard) throws IOException {
        for (WordCard wc : transform(wordCard)) {
            wordProcessor.process(wc);
        }
    }
}
