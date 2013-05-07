/*
 * Copyright 2012 Steven Swor.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cameljamod;

import cameljamod.net.AbstractMasterConnectionWrapper;
import cameljamod.net.TCPMasterConnectionWrapper;
import cameljamod.net.UDPMasterConnectionWrapper;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Map;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.net.UDPMasterConnection;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultPollingEndpoint;

/**
 * Endpoint for polling discrete inputs.
 *
 * @author Steven Swor
 */
public class JamodEndpoint extends DefaultPollingEndpoint {

    /**
	 * Modbus Slave ID constant
	 */
    private static final String SLAVE_ID = "slaveId";

    /**
     * The connection.
     */
    private volatile AbstractMasterConnectionWrapper conn;
    /**
     * The URI to the modbus device.
     */
    private URI modbusURI;
    /**
     * The parameters.
     */
    private Map<String, Object> parameters;
    /**
     * The component.
     */
    private final JamodComponent component;

    /**
     * Creates a new JamodEndpoint.
     *
     * @param modbusURI the URI of the modbus device.
     */
    public JamodEndpoint(final JamodComponent component, final URI modbusURI, final Map<String, Object> parameters) {
        this.modbusURI = modbusURI;
        this.parameters = parameters;
        this.component = component;
    }

    /**
     * Returns {@code null}.
     *
     * @return {@code null}
     * @throws Exception never
     */
    public ModbusProducer createProducer() throws Exception {
        String dataType = JamodUriResolver.getDataTypeFromUri(modbusURI);
        ModbusProducer producer;
        if ("coils".equalsIgnoreCase(dataType)) {
            producer = new DiscreteOutputsProducer(this);
        } else if ("registers".equalsIgnoreCase(dataType)) {
            producer = new RegistersProducer(this);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Unsupported data type: {0}", dataType));
        }
        producer.setReferenceAddress(JamodUriResolver.getReferenceFromUri(modbusURI));
        int slaveId = component.getAndRemoveParameter(parameters, SLAVE_ID, Integer.class, 0);
        producer.setSlaveId(slaveId);
        return producer;
    }

    @Override
    public Consumer createConsumer(final Processor processor) throws Exception {
        String dataType = JamodUriResolver.getDataTypeFromUri(modbusURI);
        ModbusPollingConsumer consumer;
        if ("discreteInputs".equalsIgnoreCase(dataType)) {
            consumer = new DiscreteInputsPollingConsumer(this, processor);
        } else if ("coils".equalsIgnoreCase(dataType)) {
            consumer = new DiscreteOutputsPollingConsumer(this, processor);
        } else if ("registers".equalsIgnoreCase(dataType)) {
            consumer = new RegistersPollingConsumer(this, processor);
        } else if ("register".equalsIgnoreCase(dataType)) {
            consumer = new RegisterPollingConsumer(this, processor);
        } else if ("inputRegisters".equalsIgnoreCase(dataType)) {
            consumer = new InputRegistersPollingConsumer(this, processor);
        } else if ("inputRegister".equalsIgnoreCase(dataType)) {
            consumer = new InputRegisterPollingConsumer(this, processor);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Unsupported data type: {0}", dataType));
        }
        consumer.setReferenceAddress(JamodUriResolver.getReferenceFromUri(modbusURI));
        int delay = component.getAndRemoveParameter(parameters, "delay", Integer.class, Integer.valueOf(500));
        consumer.setDelay(delay);
        int initialDelay = component.getAndRemoveParameter(parameters, "initialDelay", Integer.class, Integer.valueOf(500));
        consumer.setInitialDelay(initialDelay);
        int count = component.getAndRemoveParameter(parameters, "count", Integer.class, Integer.valueOf(1));
        consumer.setCount(count);
        boolean changesOnly = component.getAndRemoveParameter(parameters, "changesOnly", Boolean.class, Boolean.FALSE);
        consumer.setChangesOnly(changesOnly);
        int slaveId = component.getAndRemoveParameter(parameters, SLAVE_ID, Integer.class, 0);
        consumer.setSlaveId(slaveId);
        return consumer;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public boolean isLenientProperties() {
        return true;
    }

    @Override
    protected String createEndpointUri() {
        StringBuilder sb = new StringBuilder(modbusURI.toString());
        return sb.toString();
    }

    /**
     * Gets the Modbus connection.
     *
     * @return the connection
     */
    public AbstractMasterConnectionWrapper getConnection() {
        if (conn == null) {
            synchronized (this) {
                if (conn == null) {
                    conn = createConnection();
                }
            }
        }
        return conn;
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        getConnection().close();
    }

    /**
     * Creates the Modbus connection.
     *
     * @return the connection
     */
    protected AbstractMasterConnectionWrapper createConnection() {
        AbstractMasterConnectionWrapper result = null;
        InetAddress addr = resolveHostAddress(modbusURI);
        if (isTCP(modbusURI)) {
            result = new TCPMasterConnectionWrapper(createTCPMasterConnection(addr));
            result.setTimeout(1000);
        } else if (isUDP(modbusURI)) {
            result = new UDPMasterConnectionWrapper(createUDPMasterConnection(addr));
        } else {
            throw new ResolveEndpointFailedException(modbusURI.toString());
        }
        int port = modbusURI.getPort();
        if (port == -1) {
            port = Modbus.DEFAULT_PORT;
        }
        result.setPort(port);
        return result;
    }

    /**
     * Creates a new TCP master connection.
     *
     * @param addr the address of the modbus device
     * @return a new TCP master connection
     */
    protected TCPMasterConnection createTCPMasterConnection(final InetAddress addr) {
        return new TCPMasterConnection(addr);
    }

    /**
     * Creates a new UDP master connection.
     *
     * @param addr the address of the modbus device
     * @return a new UDP master connection
     */
    protected UDPMasterConnection createUDPMasterConnection(final InetAddress addr) {
        return new UDPMasterConnection(addr);
    }

    /**
     * Determines if a URI represents a TCP connection.
     *
     * @param uri the URI to check
     * @return {@code true} if the URI scheme is {@code tcp}, otherwise
     * {@code false}.
     */
    public static boolean isTCP(final URI uri) {
        return "tcp".equalsIgnoreCase(uri.getScheme());
    }

    /**
     * Determines if a URI represents a UDP connection.
     *
     * @param uri the URI to check
     * @return {@code true} if the URI scheme is {@code udp}, otherwise
     * {@code false}.
     */
    public static boolean isUDP(final URI uri) {
        return "udp".equalsIgnoreCase(uri.getScheme());
    }

    /**
     * Resolves a modbus device's address from a URI.
     *
     * @param uri the URI
     * @return an {@link java.net.InetAddress} for the URI's host
     */
    protected static InetAddress resolveHostAddress(final URI uri) {
        String host = uri.getHost();
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            throw new RuntimeCamelException(ex);
        }
    }
}
