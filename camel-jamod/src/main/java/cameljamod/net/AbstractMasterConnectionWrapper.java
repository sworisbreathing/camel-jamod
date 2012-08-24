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

package cameljamod.net;

import java.net.InetAddress;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.io.ModbusTransport;

/**
 * Parent class for Modbus master connections.  This exists because the master
 * connection classes provided by Jamod have identical methods, but do not
 * extend a common parent class or implement a common interface.
 * 
 * @author Steven Swor
 */
public abstract class AbstractMasterConnectionWrapper<T> {
    
    /**
     * The underlying connection.
     */
    private final T masterConnection;
    
    /**
     * Creates a new AbstractMasterConnectionWrapper.
     * @param masterConnection the underlying modbus connection
     */
    public AbstractMasterConnectionWrapper(final T masterConnection) {
        this.masterConnection = masterConnection;
    }
    
    /**
     * Gets the underlying modbus connection.
     * @return the underlying modbus connection
     */
    public T getMasterConnection() {
        return masterConnection;
    }
    
    /**
     * Gets the connection's modbus transport.
     * @return the connection's modbus transport
     */
    public abstract ModbusTransport getModbusTransport();
    
    /**
     * Closes the connection.
     */
    public abstract void close();
    
    /**
     * Connects to the modbus device.
     * @throws Exception if a connection canot be established
     */
    public abstract void connect() throws Exception;
    
    /**
     * Gets the connection's address.
     * @return the connection's address
     */
    public abstract InetAddress getAddress();
    
    /**
     * Gets the connection's port.
     * @return the connection's port
     */
    public abstract int getPort();
    
    /**
     * Gets the conection's timeout.
     * @return the connection's timeout
     */
    public abstract int getTimeout();
    
    /**
     * Sets the connection's address.
     * @param address the connection's address
     */
    public abstract void setAddress(final InetAddress address);
    
    /**
     * Sets the connection's port.
     * @param port the connection's port
     */
    public abstract void setPort(final int port);
    
    /**
     * Sets the connection's timeout.
     * @param timeout the connection's timeout
     */
    public abstract void setTimeout(final int timeout);
    
    /**
     * Determines if the connection is connected.
     * @return wheter or not the connection is connected.
     */
    public abstract boolean isConnected();
    
    /**
     * Creates a new transaction.
     * @return a new transaction
     */
    public abstract ModbusTransaction createTransaction();
}
