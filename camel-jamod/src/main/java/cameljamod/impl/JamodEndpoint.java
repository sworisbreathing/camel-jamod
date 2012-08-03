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
     * The reference address for the discrete inputs.
     */
    private int referenceAddress;
    /**
     * The number of discrete inputs to read.
     */
    private int discreteInputCount;
    /**
     * The initial delay before polling.
     */
    private int initialDelay;
    /**
     * The polling interval.
     */
    private int delay;
    /**
     * Creates a new JamodEndpoint.
     * @param modbusURI the URI of the modbus device.
     */
    public JamodEndpoint(final URI modbusURI) {
        this.modbusURI = modbusURI;
    }
    /**
     * Gets the number of discrete inputs to be read.
     * @return the number of discrete inputs to be read
     */
    public int getDiscreteInputCount() {
        return discreteInputCount;
    }
    /**
     * Sets the number of discrete inputs to be read.
     * @param discreteInputCount the number of discrete inputs to be read
     */
    public void setDiscreteInputCount(int discreteInputCount) {
        this.discreteInputCount = discreteInputCount;
    }
    /**
     * Gets the reference address of the first discrete input.
     * @return the reference address of the first discrete input
     */
    public int getReferenceAddress() {
        return referenceAddress;
    }
    /**
     * Sets the reference address of the first discrete input.
     * @param referenceAddress the reference address of the first discrete input
     */
    public void setReferenceAddress(int referenceAddress) {
        this.referenceAddress = referenceAddress;
    }
    /**
     * Gets the polling interval (in milliseconds).
     * @return the polling interval (in milliseconds)
     */
    public int getDelay() {
        return delay;
    }
    /**
     * Sets the polling interval (in milliseconds).
     * @param delay the polling interval (in milliseconds)
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }
    /**
     * Gets the delay before the first polling attempt.
     * @return the delay before the first polling attempt
     */
    public int getInitialDelay() {
        return initialDelay;
    }
    /**
     * Sets the delay before the first polling attempt.
     * @param initialDelay the delay before the first polling attempt
     */
    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
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
        return new JamodPollingConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    protected String createEndpointUri() {
        StringBuilder sb = new StringBuilder(modbusURI.toString()).append("?");
        sb.append("initialDelay=").append(getInitialDelay());
        sb.append("&");
        sb.append("delay=").append(getDelay());
        sb.append("&");
        sb.append("referenceAddress=").append(getReferenceAddress());
        sb.append("&");
        sb.append("discreteInputCount=").append(getDiscreteInputCount());
        
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
