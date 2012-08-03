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

package cameljamod.impl.net;

import java.net.InetAddress;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.io.ModbusTransport;

/**
 *
 * @author Steven Swor
 */
public abstract class AbstractMasterConnectionWrapper<T> {

    private final T masterConnection;
    
    public AbstractMasterConnectionWrapper(final T masterConnection) {
        this.masterConnection = masterConnection;
    }
    
    public T getMasterConnection() {
        return masterConnection;
    }
    
    public abstract ModbusTransport getModbusTransport();
    
    public abstract void close();
    
    public abstract void connect() throws Exception;
    
    public abstract InetAddress getAddress();
    
    public abstract int getPort();
    
    public abstract int getTimeout();
    
    public abstract void setAddress(final InetAddress address);
    
    public abstract void setPort(final int port);
    
    public abstract void setTimeout(final int timeout);
    
    public abstract boolean isConnected();
    
    public abstract ModbusTransaction createTransaction();
}
