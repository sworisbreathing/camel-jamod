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
package cameljamod.demo.device;

import java.text.ParseException;
import java.util.Formatter;
import javax.swing.text.DefaultFormatter;

/**
 *
 * @author Steven Swor
 */
public class HexWordFormatter extends DefaultFormatter {
    
    private static HexWordFormatter instance = null;
    
    public static HexWordFormatter getInstance() {
        if (instance==null) {
            instance = new HexWordFormatter();
        }
        return instance;
    }

    @Override
    public String valueToString(final Object value) throws ParseException {
        StringBuilder sb = new StringBuilder(6);
        sb.append("0x");
        Formatter f = new Formatter(sb);
        f.format("%04X", (Integer) value);
        return sb.toString();
    }

    @Override
    public Object stringToValue(String string) throws ParseException {
        try {
            return Integer.valueOf(string.substring(2), 16);
        } catch (NumberFormatException nfe) {
            throw new ParseException(string, 0);
        }
    }
}
