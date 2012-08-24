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
package cameljamod.net;

import cameljamod.net.UDPMasterConnectionWrapper;
import java.net.InetAddress;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.io.ModbusTransport;
import net.wimpi.modbus.io.ModbusUDPTransaction;
import net.wimpi.modbus.io.ModbusUDPTransport;
import net.wimpi.modbus.net.UDPMasterConnection;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Unit tests for {@link UDPMasterConnectionWrapper}.
 *
 * @author Steven Swor
 */
public class UDPMasterConnectionWrapperTest {

    public UDPMasterConnectionWrapperTest() {
    }

    /**
     * Tests {@link UDPMasterConnection#connect()} and {@link UDPMasterConnection#close()}.
     */
    @Test
    public void testConnectAndClose() throws Exception {
        final UDPMasterConnection mockConnection = mock(UDPMasterConnection.class);
        doAnswer(new Answer() {

            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.when(mockConnection.isConnected()).thenReturn(Boolean.TRUE);
                return null;
            }
        }).when(mockConnection).connect();
        doAnswer(new Answer() {

            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.when(mockConnection.isConnected()).thenReturn(Boolean.FALSE);
                return null;
            }
        }).when(mockConnection).close();
        UDPMasterConnectionWrapper instance = new UDPMasterConnectionWrapper(mockConnection);
        assertFalse(instance.isConnected());
        instance.connect();
        assertTrue(instance.isConnected());
        instance.close();
        assertFalse(instance.isConnected());
    }

    @Test
    public void testIsConnected() {
        UDPMasterConnection mockConnection = mock(UDPMasterConnection.class);
        when(mockConnection.isConnected()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        UDPMasterConnectionWrapper instance = new UDPMasterConnectionWrapper(mockConnection);
        assertTrue(instance.isConnected());
        assertFalse(instance.isConnected());
    }

    @Test
    public void testGetAddressAndSetAddress() throws Exception {
        UDPMasterConnection conn = new UDPMasterConnection(null);
        UDPMasterConnectionWrapper instance = new UDPMasterConnectionWrapper(conn);
        assertNull(instance.getAddress());
        instance.setAddress(InetAddress.getLocalHost());
        assertEquals(InetAddress.getLocalHost(), instance.getAddress());
    }

    @Test
    public void testCreateTransaction() throws Exception {
        UDPMasterConnection conn = new UDPMasterConnection(InetAddress.getLocalHost());
        conn.setPort(1024);
        conn.connect();
        try {
            UDPMasterConnectionWrapper instance = new UDPMasterConnectionWrapper(conn);
            ModbusTransaction transaction = instance.createTransaction();
            assertNotNull(transaction);
            assertTrue(transaction instanceof ModbusUDPTransaction);
        } finally {
            conn.close();
        }
    }

    @Test
    public void testGetModbusTransport() throws Exception {
        UDPMasterConnection conn = new UDPMasterConnection(InetAddress.getLocalHost());
        conn.setPort(1024);
        conn.connect();
        try {
            UDPMasterConnectionWrapper instance = new UDPMasterConnectionWrapper(conn);
            ModbusTransport transport = instance.getModbusTransport();
            assertNotNull(transport);
            assertTrue(transport instanceof ModbusUDPTransport);
        } finally {
            conn.close();
        }

    }

    @Test
    public void testGetPortAndSetPort() {
        UDPMasterConnection conn = new UDPMasterConnection(null);
        UDPMasterConnectionWrapper instance = new UDPMasterConnectionWrapper(conn);
        instance.setPort(1234);
        assertEquals(1234, instance.getPort());
    }

    @Test
    public void testGetTimeoutAndSetTimeout() throws Exception {
        UDPMasterConnection conn = new UDPMasterConnection(InetAddress.getLocalHost());
        conn.connect();
        try {
            UDPMasterConnectionWrapper instance = new UDPMasterConnectionWrapper(conn);
            instance.setTimeout(5678);
            assertEquals(5678, instance.getTimeout());
        } finally {
            conn.close();
        }
    }
}
