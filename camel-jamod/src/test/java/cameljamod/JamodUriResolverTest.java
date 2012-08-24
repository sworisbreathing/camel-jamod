/*
 *  Copyright 2012 Steven Swor.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cameljamod.impl;

import java.net.URI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link JamodUriResolver}.
 * 
 * @author Steven Swor
 */
public class JamodUriResolverTest {
    
    /**
     * The sample URI.
     */
    private static URI sampleUri = null;
    
    /**
     * Sets up the sample URI
     * @throws Exception if the sample uri is invalid
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        sampleUri = new URI("tcp://localhost:1024/discreteInputs/3?delay=500&initialDelay=500&count=8");
    }
    
    /**
     * Destroys the sample uri
     */
    @AfterClass
    public static void tearDownClass() {
        sampleUri = null;
    }
    
    /**
     * Tests {@link JamodUriResolver#getProtocolFromUri(URI)}.
     */
    @Test
    public void testGetProtocolFromURI() {
        assertEquals("tcp", JamodUriResolver.getProtocolFromUri(sampleUri));
    }
    
    /**
     * Tests {@link JamodUriResolver#getHostFromUri(URI)}.
     */
    @Test
    public void testGetHostFromURI() {
        assertEquals("localhost", JamodUriResolver.getHostFromUri(sampleUri));
    }
    
    /**
     * Tests {@link JamodUriResolver#getPortFromUri(URI)};
     */
    @Test
    public void testGetPortFromURI() {
        assertEquals(1024, JamodUriResolver.getPortFromUri(sampleUri));
    }
    
    /**
     * Tests {@link JamodUriResolver#getDataTypeFromUri(URI)}.
     */
    @Test
    public void testGetDataTypeFromUri() {
        assertEquals("discreteInputs", JamodUriResolver.getDataTypeFromUri(sampleUri));
    }
    
    /**
     * Tests {@link JamodUriResolver#getReferenceFromUri(URI)}
     */
    @Test
    public void testGetReferenceFromUri() {
        assertEquals(3, JamodUriResolver.getReferenceFromUri(sampleUri));
    }
}
