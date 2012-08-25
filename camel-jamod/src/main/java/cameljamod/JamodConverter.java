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
import java.util.Formatter;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.BitVector;
import org.apache.camel.Converter;

/**
 *
 * @author Steven Swor
 */
@Converter
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

    @Converter
    public static InputRegister[] toInputRegisterArray(final byte[] bytes) {
        int size = bytes.length / 2;
        InputRegister[] results = new InputRegister[size];
        for (int i = 0; i < bytes.length; i += 2) {
            results[i / 2] = new SimpleInputRegister(bytes[i], bytes[i + 1]);
        }
        return results;
    }

    @Converter
    public static byte[] toByteArray(final InputRegister[] inputRegisters) {
        int size = inputRegisters.length * 2;
        byte[] results = new byte[size];
        for (int i = 0; i < inputRegisters.length; i++) {
            byte[] registerBytes = inputRegisters[i].toBytes();
            System.arraycopy(registerBytes, 0, results, i * 2, 2);
        }
        return results;
    }

    @Converter
    public static Register[] toRegisterArray(final byte[] bytes) {
        int size = bytes.length / 2;
        Register[] results = new Register[size];
        for (int i = 0; i < bytes.length; i += 2) {
            results[i / 2] = new SimpleRegister(bytes[i], bytes[i + 1]);
        }
        return results;
    }

    @Converter
    public static byte[] toByteArray(final Register[] registers) {
        int size = registers.length * 2;
        byte[] results = new byte[size];
        for (int i = 0; i < registers.length; i++) {
            byte[] registerBytes = registers[i].toBytes();
            System.arraycopy(registerBytes, 0, results, i * 2, 2);
        }
        return results;
    }

    @Converter
    public static String toString(final InputRegister[] registers) {
        StringBuilder sb = new StringBuilder(7*registers.length-1);
        boolean first = true;
        for (InputRegister register : registers) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            appendRegisterToString(register, sb);
        }
        return sb.toString();
    }
    
    @Converter
    public static String toString(final Register[] registers) {
        StringBuilder sb = new StringBuilder(7*registers.length-1);
        boolean first = true;
        for (InputRegister register : registers) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            appendRegisterToString(register, sb);
        }
        return sb.toString();
    }

    @Converter
    public static byte[] toByteArray(final Register register) {
        return register.toBytes();
    }
    
    @Converter
    public static byte[] toByteArray(final InputRegister register) {
        return register.toBytes();
    }
    
    @Converter
    public static String toString(final Register register) {
        StringBuilder sb = new StringBuilder(6);
        appendRegisterToString(register, sb);
        return sb.toString();
    }
    
    @Converter
    public static String toString(final InputRegister register) {
        StringBuilder sb = new StringBuilder(6);
        appendRegisterToString(register, sb);
        return sb.toString();
    }
    
    private static void appendRegisterToString(final InputRegister register, final StringBuilder sb) {
        sb.append("0x");
        Formatter formatter = new Formatter(sb);
        byte[] bytes = register.toBytes();
        formatter.format("%02X", bytes[0]);
        formatter.format("%02X", bytes[1]);
    }
}
