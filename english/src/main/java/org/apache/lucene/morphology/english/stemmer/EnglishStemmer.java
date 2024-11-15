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
package org.apache.lucene.morphology.english.stemmer;


import org.apache.lucene.morphology.english.EnglishLuceneMorphology;

import java.util.List;

public class EnglishStemmer {
    private final EnglishLuceneMorphology englishLuceneMorphology;

    public EnglishStemmer(EnglishLuceneMorphology englishLuceneMorphology) {
        this.englishLuceneMorphology = englishLuceneMorphology;
    }

    public String getStemmedWord(String word){
        if(!englishLuceneMorphology.checkString(word)){
            return word;
        }
        List<String> normalForms = englishLuceneMorphology.getNormalForms(word);
        if(normalForms.size() == 1){
            return normalForms.get(0);
        }
        normalForms.remove(word);
        if(normalForms.size() == 1){
            return normalForms.get(0);
        }
        return word;
    }

}
