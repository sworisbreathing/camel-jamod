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
package cameljamod;

import cameljamod.DiscreteInputsPollingConsumer;
import cameljamod.JamodComponent;
import cameljamod.JamodEndpoint;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.util.BitVector;
import org.apache.camel.Processor;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link DiscreteInputsPollingConsumer}.
 * @author Steven Swor
 */
public class DiscreteInputsPollingConsumerTest {
    
    /**
     * The test instance.
     */
    private static DiscreteInputsPollingConsumer instance = null;
    
    /**
     * Sets up the test instance.
     * @throws Exception if the test instance cannot be set up
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        JamodComponent component = new JamodComponent();
        URI modbusUri = new URI("tcp://localhost:1024/discreteInputs/2");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("count", Integer.valueOf(8));
        JamodEndpoint endpoint = new JamodEndpoint(component, modbusUri, parameters);
        Processor processor = new NoopProcessor();
        instance = new DiscreteInputsPollingConsumer(endpoint, processor);
        instance.setCount(8);
        instance.setReferenceAddress(2);
        
    }
    
    /**
     * Destroys the test instance.
     * @throws Exception 
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
        instance = null;
    }

    /**
     * Tests {@link DiscreteInputsPollingConsumer#createRequest()}.
     */
    @Test
    public void testCreateRequest() throws Exception {
        ReadInputDiscretesRequest request = instance.createRequest();
        assertNotNull(request);
        assertEquals(8, request.getBitCount());
        assertEquals(2, request.getReference());
    }

    /**
     * Tests {@link DiscreteInputsPollingConsumer#getBodyFromResponse(ReadInputDiscretesResponse)}.
     */
    @Test
    public void testGetBodyFromResponse() {
        BitVector expected = new BitVector(8);
        ReadInputDiscretesResponse response = new ReadInputDiscretesResponse(8);
        assertArrayEquals(expected.getBytes(), instance.getBodyFromResponse(response).getBytes());
    }
}
