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

import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import org.apache.camel.impl.DefaultCamelContext;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Steven Swor
 */
public class RegistersProducerTest {

    /**
     * Test of getRequestTypeClass method, of class DiscreteOutputsProducer.
     */
    @Test
    public void testGetRequestTypeClass() throws Exception {
        JamodComponent c = new JamodComponent();
        c.setCamelContext(new DefaultCamelContext());
        assertEquals(WriteMultipleRegistersRequest.class, new RegistersProducer((JamodEndpoint) c.createEndpoint("jamod:tcp://localhost/discreteOutputs/0")).getRequestTypeClass());
    }
}
