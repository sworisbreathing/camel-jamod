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

package cameljamod.impl;

import cameljamod.impl.net.AbstractMasterConnectionWrapper;
import cameljamod.impl.net.TCPMasterConnectionWrapper;
import cameljamod.impl.net.UDPMasterConnectionWrapper;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Map;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.net.UDPMasterConnection;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultPollingEndpoint;

/**
 * Endpoint for polling discrete inputs.
 * 
 * @author Steven Swor
 */
public class JamodEndpoint extends DefaultPollingEndpoint {
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
    
    private final JamodComponent component;
    
    /**
     * Creates a new JamodEndpoint.
     * @param modbusURI the URI of the modbus device.
     */
    public JamodEndpoint(final JamodComponent component, final URI modbusURI, final Map<String, Object> parameters) {
        this.modbusURI = modbusURI;
        this.parameters = parameters;
        this.component = component;
    }
    
    /**
     * Returns {@code null}.
     * @return {@code null}
     * @throws Exception never
     */
    public Producer createProducer() throws Exception {
        return null;
    }

    @Override
    public Consumer createConsumer(final Processor processor) throws Exception {
        String dataType = JamodUriResolver.getDataTypeFromUri(modbusURI);
        if ("discreteInputs".equalsIgnoreCase(dataType)) {
            DiscreteInputsPollingConsumer consumer = new DiscreteInputsPollingConsumer(this, processor);
            consumer.setReferenceAddress(JamodUriResolver.getReferenceFromUri(modbusURI));
            int delay = component.getAndRemoveParameter(parameters, "delay", Integer.class, Integer.valueOf(500));
            consumer.setDelay(delay);
            int initialDelay = component.getAndRemoveParameter(parameters, "initialDelay", Integer.class, Integer.valueOf(500));
            consumer.setInitialDelay(initialDelay);
            int count = component.getAndRemoveParameter(parameters, "count", Integer.class, Integer.valueOf(1));
            consumer.setDiscreteInputCount(count);
            return consumer;
        }else{
            throw new IllegalArgumentException(MessageFormat.format("Unsupported data type: {0}", dataType));
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
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
     * @return the connection
     */
    public AbstractMasterConnectionWrapper getConnection() {
        if (conn == null) {
            synchronized(this) {
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
     * @return the connection
     */
    protected AbstractMasterConnectionWrapper createConnection() {
        AbstractMasterConnectionWrapper result = null;
        InetAddress addr = resolveHostAddress(modbusURI);
        if (isTCP(modbusURI)) {
            result = new TCPMasterConnectionWrapper(createTCPMasterConnection(addr));
        }else if (isUDP(modbusURI)) {
            result = new UDPMasterConnectionWrapper(createUDPMasterConnection(addr));
        }else{
            throw new ResolveEndpointFailedException(modbusURI.toString());
        }
        int port = modbusURI.getPort();
        if (port==-1) {
            port = Modbus.DEFAULT_PORT;
        }
        result.setPort(port);
        return result;
    }
    
    /**
     * Creates a new TCP master connection.
     * @param addr the address of the modbus device
     * @return a new TCP master connection
     */
    protected TCPMasterConnection createTCPMasterConnection(final InetAddress addr) {
        return new TCPMasterConnection(addr);
    }
    
    /**
     * Creates a new UDP master connection.
     * @param addr the address of the modbus device
     * @return a new UDP master connection
     */
    protected UDPMasterConnection createUDPMasterConnection(final InetAddress addr) {
        return new UDPMasterConnection(addr);
    }
    
    /**
     * Determines if a URI represents a TCP connection.
     * @param uri the URI to check
     * @return {@code true} if the URI scheme is {@code tcp}, otherwise
     * {@code false}.
     */
    private static boolean isTCP(final URI uri) {
        return "tcp".equalsIgnoreCase(uri.getScheme());
    }
    
    /**
     * Determines if a URI represents a UDP connection.
     * @param uri the URI to check
     * @return {@code true} if the URI scheme is {@code udp}, otherwise
     * {@code false}.
     */
    private static boolean isUDP(final URI uri) {
        return "udp".equalsIgnoreCase(uri.getScheme());
    }
    
    /**
     * Resolves a modbus device's address from a URI.
     * @param uri the URI
     * @return an {@link java.net.InetAddress} for the URI's host
     */
    private static InetAddress resolveHostAddress(final URI uri) {
        String host = uri.getHost();
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            throw new RuntimeCamelException(ex);
        }
    }
}
