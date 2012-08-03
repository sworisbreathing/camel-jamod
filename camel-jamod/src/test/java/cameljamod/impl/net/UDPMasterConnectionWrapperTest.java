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

import cameljamod.impl.TestUtilities;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.ModbusUDPListener;
import net.wimpi.modbus.net.UDPMasterConnection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests for {@link UDPMasterConnectionWrapper}.
 * 
 * @author Steven Swor
 */
@Ignore("Jamod UDP listener does not clean up threads when stopping")
public class UDPMasterConnectionWrapperTest {

    /**
     * The modbus listener.
     */
    private static ModbusUDPListener listener = null;
    
    /**
     * The port.
     */
    private static int port = Modbus.DEFAULT_PORT;

    public UDPMasterConnectionWrapperTest() {
    }

    /**
     * Sets up the modbus listener.
     * @throws Exception if the modbus listener cannot be set up
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        listener = new ModbusUDPListener(InetAddress.getLocalHost());
        port = Integer.parseInt(TestUtilities.getTestProperty("udp.port", String.valueOf(Modbus.DEFAULT_PORT)));
        listener.setPort(port);
        listener.start();
    }

    /**
     * Stops the modbus listener.
     * @throws Exception if the modbus listener cannot be stopped
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
        listener.stop();
        listener = null;
    }

    /**
     * Creates a new modbus connection.
     * @return a new modbus connection
     */
    protected static UDPMasterConnection createMockConnection() {
        try {
            UDPMasterConnection result = new UDPMasterConnection(InetAddress.getLocalHost());
            result.setPort(port);
            return result;
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Tests {@link UDPMasterConnectionWrapper#connect()} and
     * {@link UDPMasterConnectionWrapper#close()}.
     */
    @Test
    public void testConnectAndClose() throws Exception {
        UDPMasterConnection connection = createMockConnection();
        connection.connect();
        connection.close();
    }

}
