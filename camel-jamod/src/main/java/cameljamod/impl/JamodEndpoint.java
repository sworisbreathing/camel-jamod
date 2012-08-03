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
 *
 * @author Steven Swor
 */
public class JamodEndpoint extends DefaultPollingEndpoint {
    
    private volatile AbstractMasterConnectionWrapper conn;
    
    private URI modbusURI;
    
    public JamodEndpoint(final URI modbusURI) {
        this.modbusURI = modbusURI;
    }

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
        return modbusURI.toString();
    }
    
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
    
    protected TCPMasterConnection createTCPMasterConnection(final InetAddress addr) {
        return new TCPMasterConnection(addr);
    }
    
    protected UDPMasterConnection createUDPMasterConnection(final InetAddress addr) {
        return new UDPMasterConnection(addr);
    }
    
    private static boolean isTCP(final URI uri) {
        return "tcp".equalsIgnoreCase(uri.getScheme());
    }
    
    private static boolean isUDP(final URI uri) {
        return "udp".equalsIgnoreCase(uri.getScheme());
    }
    
    private static InetAddress resolveHostAddress(final URI uri) {
        String host = uri.getHost();
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            throw new RuntimeCamelException(ex);
        }
    }
}
