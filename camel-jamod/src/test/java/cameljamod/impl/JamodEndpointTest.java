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

import cameljamod.impl.net.AbstractMasterConnectionWrapper;
import cameljamod.impl.net.TCPMasterConnectionWrapper;
import java.net.InetAddress;
import java.net.URI;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.net.UDPMasterConnection;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Steven Swor
 */
public class JamodEndpointTest {
    
    private static Logger testLogger = null;
    
    public JamodEndpointTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        testLogger = LoggerFactory.getLogger(JamodEndpointTest.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        testLogger = null;
    }
    
    private volatile JamodEndpoint instance = null;
    
    protected JamodEndpoint getInstance() throws Exception {
        if (instance==null) {
            synchronized(this) {
                if (instance == null) {
                    JamodComponent c = new JamodComponent();
                    c.setCamelContext(new DefaultCamelContext());
                    instance = (JamodEndpoint) c.createEndpoint("jamod:tcp://localhost:1024/discreteInputs/0");
                }
            }
        }
        return instance;
    }

    /**
     * Test of createProducer method, of class JamodEndpoint.
     */
    @Test
    public void testCreateProducer() throws Exception {
    }

    /**
     * Test of createConsumer method, of class JamodEndpoint.
     */
    @Test
    public void testCreateConsumer() throws Exception {
        JamodComponent c = new JamodComponent();
        c.setCamelContext(new DefaultCamelContext());
        JamodEndpoint discreInputsEndpoint = (JamodEndpoint) c.createEndpoint("tcp://localhost/discreteInputs/0");
        Processor p = new NoopProcessor();
        assertTrue(discreInputsEndpoint.createConsumer(p) instanceof DiscreteInputsPollingConsumer);
        JamodEndpoint discreteOutputsEndpoint = (JamodEndpoint) c.createEndpoint("tcp://localhost/coils/0");
        assertTrue(discreteOutputsEndpoint.createConsumer(p) instanceof DiscreteOutputsPollingConsumer);
        JamodEndpoint registersEndpoint = (JamodEndpoint) c.createEndpoint("tcp://localhost/registers/0");
        assertTrue(registersEndpoint.createConsumer(p) instanceof RegistersPollingConsumer);
        JamodEndpoint inputRegistersEndpoint = (JamodEndpoint) c.createEndpoint("tcp://localhost/inputRegisters/0");
        assertTrue(inputRegistersEndpoint.createConsumer(p) instanceof InputRegistersPollingConsumer);
        
    }

    /**
     * Test of isSingleton method, of class JamodEndpoint.
     */
    @Test
    public void testIsSingleton() throws Exception {
        assertFalse(getInstance().isSingleton());
    }

    /**
     * Test of isLenientProperties method, of class JamodEndpoint.
     */
    @Test
    public void testIsLenientProperties() throws Exception {
        assertTrue(getInstance().isLenientProperties());
    }

    /**
     * Test of createEndpointUri method, of class JamodEndpoint.
     */
    @Test
    public void testCreateEndpointUri() throws Exception {
        assertEquals("tcp://localhost:1024/discreteInputs/0",getInstance().createEndpointUri());
    }

    /**
     * Test of getConnection method, of class JamodEndpoint.
     */
    @Test
    public void testGetConnection() throws Exception {
        AbstractMasterConnectionWrapper connection = getInstance().getConnection();
        assertNotNull(connection);
        assertTrue(connection instanceof TCPMasterConnectionWrapper);
    }

    /**
     * Test of doStop method, of class JamodEndpoint.
     */
    @Test
    public void testDoStop() throws Exception {
    }

    /**
     * Test of createConnection method, of class JamodEndpoint.
     */
    @Test
    public void testCreateConnection() throws Exception {
    }

    /**
     * Test of createTCPMasterConnection method, of class JamodEndpoint.
     */
    @Test
    public void testCreateTCPMasterConnection() throws Exception {
        TCPMasterConnection c = getInstance().createTCPMasterConnection(InetAddress.getLocalHost());
        assertEquals(InetAddress.getLocalHost(), c.getAddress());
        assertEquals(Modbus.DEFAULT_PORT, c.getPort());
    }

    /**
     * Test of createUDPMasterConnection method, of class JamodEndpoint.
     */
    @Test
    public void testCreateUDPMasterConnection() throws Exception {
        UDPMasterConnection c = getInstance().createUDPMasterConnection(InetAddress.getLocalHost());
        assertEquals(InetAddress.getLocalHost(), c.getAddress());
        assertEquals(Modbus.DEFAULT_PORT, c.getPort());
    }
    
    @Test
    public void testIsTcp() throws Exception {
        assertTrue(JamodEndpoint.isTCP(new URI("tcp://localhost:512")));
    }
    
    @Test
    public void testIsUDP() throws Exception {
        assertTrue(JamodEndpoint.isUDP(new URI("udp://localhost:512")));
    }
    
}
