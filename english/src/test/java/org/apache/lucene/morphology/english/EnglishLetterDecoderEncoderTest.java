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
package org.apache.lucene.morphology.english;

import org.junit.Before;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class EnglishLetterDecoderEncoderTest {
    private EnglishLetterDecoderEncoder decoderEncoder;

    @Before
    public void setUp() {
        decoderEncoder = new EnglishLetterDecoderEncoder();
    }

    @org.junit.Test
    public void testDecodeEncodeToArray() {
        assertThat(decoderEncoder.decodeArray(decoderEncoder.encodeToArray("abcdefghijklmnopqrstuvwxyz")), equalTo("abcdefghijklmnopqrstuvwxyz"));
        assertThat(decoderEncoder.decodeArray(decoderEncoder.encodeToArray("xyz")), equalTo("xyz"));
        assertThat(decoderEncoder.decodeArray(decoderEncoder.encodeToArray("ytrrty")), equalTo("ytrrty"));
        assertThat(decoderEncoder.decodeArray(decoderEncoder.encodeToArray("ytrrtyz")), equalTo("ytrrtyz"));
        assertThat(decoderEncoder.decodeArray(decoderEncoder.encodeToArray("ytrrtyzqwqwe")), equalTo("ytrrtyzqwqwe"));

    }
}
