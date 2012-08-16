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
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.net.TCPMasterConnection;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.BDDMockito.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
     * Creates a new master connection.
     *
     * @return a new master connection
     */
    protected static TCPMasterConnection createMockConnection() {
        try {
            TCPMasterConnection result = new TCPMasterConnection(InetAddress.getLocalHost());
            result.setPort(port);
            result.setTimeout(1000);
            return result;
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
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
}
