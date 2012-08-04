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

import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests for {@link JamodComponent}.
 * @author Steven Swor
 */
@Ignore
public class JamodComponentTest extends CamelTestSupport {

    private ModbusTCPListener listener;
    private SimpleProcessImage spi;
    private volatile JamodComponent instance = null;
    private static volatile String port = null;

    /**
     * Gets the port.
     * @return the port
     */
    protected static String getPort() {
        if (port == null) {
            synchronized (JamodComponentTest.class) {
                if (port == null) {
                    //port = TestUtilities.getTestProperty("tcp.port", String.valueOf(Modbus.DEFAULT_PORT));
                    //port = String.valueOf(Modbus.DEFAULT_PORT);
                    port = "1024";
                }
            }
        }
        return port;
    }

    public JamodComponentTest() {
    }

    @Override
    protected void setUp() throws Exception {
//        spi = new SimpleProcessImage();
//        spi.addDigitalIn(new SimpleDigitalIn(true));
//        ModbusCoupler.getReference().setProcessImage(spi);
//        ModbusCoupler.getReference().setMaster(false);
//        listener = new ModbusTCPListener(1);
//        listener.setPort(Integer.parseInt(getPort()));
//        listener.start();
//        Thread.sleep(1000);
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
//        listener.stop();
    }

    /**
     * Lazy-initializes the Jamod component.
     * @return the Jamod component
     */
    protected JamodComponent getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = new JamodComponent();
                }
            }
        }
        return instance;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext result = super.createCamelContext();
        JamodComponent c = getInstance();
        result.addComponent("jamod", c);
        c.setCamelContext(result);
        return result;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {

            public void configure() {
                from("jamod:tcp://localhost:" + getPort() + "/discreteInputs/0?count=8&delay=100&initialDelay=100").to("log:cameljamod.impl.JamodComponent");
            }
        };
    }
    
    /**
     * Tests the component.
     * @throws Exception if the test fails
     */
    @Test
    public void testCamelComponent() throws Exception {
        Thread.sleep(5000);
    }
}
