/*
 * Copyright 2012 Steven Swor.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cameljamod;

import java.util.Arrays;
import net.wimpi.modbus.util.BitVector;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;

/**
 *
 * @author Steven Swor
 */
public class JamodConverter {

    private JamodConverter() {
    }

    @Converter
    public static byte[] toByteArray(final BitVector bitVector) {
        byte[] source = bitVector.getBytes();
        return Arrays.copyOf(source, source.length);
    }

    @Converter
    public static BitVector toBitVector(final byte[] byteArray) {
        BitVector bitVector = new BitVector(8 * byteArray.length);
        bitVector.setBytes(byteArray);
        return bitVector;
    }

    @Converter
    public static String toString(final BitVector bitVector) {
        return bitVector.toString().trim();
    }

    @Converter
    public static BitVector toBitVector(final String str) {
        int strLen = str.length();
        if (strLen == 0) {
            return new BitVector(0);
        }
        int numSpaces = strLen / 9;
        BitVector bitVector = new BitVector(strLen - numSpaces);
        int offset = 0;
        for (int i = 0; i < str.length(); i++) {
            if (' ' == str.charAt(i)) {
                offset++;
            } else {
                bitVector.setBit(i - offset, '1' == str.charAt(i));
            }
        }
        return bitVector;
    }
}
