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
package cameljamod.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility methods for testing.
 * 
 * @author Steven Swor
 */
public class TestUtilities {

    /**
     * Gets a test property from the test properties file.
     *
     * @param defaultResult the default value
     * @return the property from a test.properties file, or the default value
     */
    public static String getTestProperty(String propertyName, String defaultResult) {
        String results;
            FileInputStream fileStream = null;
            try {
                Properties props = new Properties();
                fileStream = new FileInputStream(new File("target/test.properties"));
                props.load(fileStream);
                results = props.getProperty(propertyName, defaultResult);
            } catch (IOException ex) {
                results = defaultResult;
            } finally {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    } catch (IOException ex) {
                        //trap
                    }
                }
            }
        return results;
    }
}
