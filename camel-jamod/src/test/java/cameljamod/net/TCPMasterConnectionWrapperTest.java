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
package cameljamod.impl.net;

import cameljamod.impl.test.TestUtilities;
import java.net.InetAddress;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.io.ModbusTCPTransport;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.io.ModbusTransport;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.net.TCPMasterConnection;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link TCPMasterConnectionWrapper}.
 *
 * @author Steven Swor
 */
public class TCPMasterConnectionWrapperTest {

    /**
     * The Modbus listener.
     */
    private static ModbusTCPListener listener = null;
    /**
     * The modbus port.
     */
    private static int port = Modbus.DEFAULT_PORT;

    public TCPMasterConnectionWrapperTest() {
    }

    /**
     * Sets up the Modbus listener.
     *
     * @throws Exception if the listener cannot be set up
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        listener = new ModbusTCPListener(1, InetAddress.getLocalHost());
        port = Integer.parseInt(TestUtilities.getTestProperty("tcp.port", String.valueOf(Modbus.DEFAULT_PORT)));
        listener.setPort(port);
        listener.start();
    }

    /**
     * Stops the modbus listener.
     *
     * @throws Exception if the listener cannot be stopped
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
        listener.stop();
        listener = null;
    }

    /**
     * Tests {@link TCPMasterConnection#connect()} and {@link TCPMasterConnection#close()}.
     */
    @Test
    public void testConnectAndClose() throws Exception {
        final TCPMasterConnection mockConnection = mock(TCPMasterConnection.class);
        doAnswer(new Answer() {

            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.when(mockConnection.isConnected()).thenReturn(Boolean.TRUE);
                return null;
            }
        }).when(mockConnection).connect();
        doAnswer(new Answer(){

            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.when(mockConnection.isConnected()).thenReturn(Boolean.FALSE);
                return null;
            }
        }).when(mockConnection).close();
        TCPMasterConnectionWrapper instance = new TCPMasterConnectionWrapper(mockConnection);
        assertFalse(instance.isConnected());
        instance.connect();
        assertTrue(instance.isConnected());
        instance.close();
        assertFalse(instance.isConnected());
    }

    @Test
    public void testIsConnected() {
        TCPMasterConnection mockConnection = mock(TCPMasterConnection.class);
        when(mockConnection.isConnected()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        TCPMasterConnectionWrapper instance = new TCPMasterConnectionWrapper(mockConnection);
        assertTrue(instance.isConnected());
        assertFalse(instance.isConnected());
    }

    @Test
    public void testGetAddressAndSetAddress() throws Exception {
        TCPMasterConnection conn = new TCPMasterConnection(null);
        TCPMasterConnectionWrapper instance = new TCPMasterConnectionWrapper(conn);
        assertNull(instance.getAddress());
        instance.setAddress(InetAddress.getLocalHost());
        assertEquals(InetAddress.getLocalHost(), instance.getAddress());
    }
    
    @Test
    public void testCreateTransaction() throws Exception {
        TCPMasterConnection conn = new TCPMasterConnection(null);
        TCPMasterConnectionWrapper instance = new TCPMasterConnectionWrapper(conn);
        ModbusTransaction transaction = instance.createTransaction();
        assertNotNull(transaction);
        assertTrue(transaction instanceof ModbusTCPTransaction);
    }
    
    @Test
    public void testGetModbusTransport() {
        TCPMasterConnection conn = new TCPMasterConnection(null);
        TCPMasterConnectionWrapper instance = new TCPMasterConnectionWrapper(conn);
        ModbusTransport transport = instance.getModbusTransport();
        /*
         * TODO jamod's API will return null until a successful connection is
         * established, so eventually we need to run a simulator during the test
         * phase so we can test this properly.
         */
        assertNull(transport);
        
    }
    
    @Test
    public void testGetPort() {
        TCPMasterConnection conn = new TCPMasterConnection(null);
        conn.setPort(1234);
        TCPMasterConnectionWrapper instance = new TCPMasterConnectionWrapper(conn);
        assertEquals(1234, instance.getPort());
    }
    
    @Test
    public void testGetTimeout() {
        TCPMasterConnection conn = new TCPMasterConnection(null);
        conn.setTimeout(5678);
        TCPMasterConnectionWrapper instance = new TCPMasterConnectionWrapper(conn);
        assertEquals(5678, instance.getTimeout());
    }
}
