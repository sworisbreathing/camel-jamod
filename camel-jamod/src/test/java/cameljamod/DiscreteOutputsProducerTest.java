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

import java.util.Random;
import net.wimpi.modbus.msg.WriteMultipleCoilsRequest;
import net.wimpi.modbus.util.BitVector;
import org.apache.camel.impl.DefaultCamelContext;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Steven Swor
 */
public class DiscreteOutputsProducerTest {

    /**
     * Test of getRequestTypeClass method, of class DiscreteOutputsProducer.
     */
    @Test
    public void testGetDataTypeClass() throws Exception {
        JamodComponent c = new JamodComponent();
        c.setCamelContext(new DefaultCamelContext());
        assertEquals(BitVector.class, new DiscreteOutputsProducer((JamodEndpoint) c.createEndpoint("jamod:tcp://localhost/coils/0")).getDataTypeClass());
    }
    
    @Test
    public void testCreateRequest() throws Exception {
        JamodComponent c = new JamodComponent();
        c.setCamelContext(new DefaultCamelContext());
        DiscreteOutputsProducer producer = new DiscreteOutputsProducer((JamodEndpoint) c.createEndpoint("jamod:tcp://localhost/coils/0"));
        Random random = new Random();
        byte[] bytes = new byte[2];
        random.nextBytes(bytes);
        BitVector expected = new BitVector(16);
        expected.setBytes(bytes);
        WriteMultipleCoilsRequest request = producer.createRequest(expected);
        assertEquals(expected, request.getCoils());
    }
}
