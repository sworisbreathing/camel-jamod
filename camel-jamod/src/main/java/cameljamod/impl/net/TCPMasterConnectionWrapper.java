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
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.io.ModbusTransport;
import net.wimpi.modbus.net.TCPMasterConnection;

/**
 * Wraps a {@link net.wimpi.modbus.net.TCPMasterConnection}.
 * 
 * @author Steven Swor
 */
public class TCPMasterConnectionWrapper extends AbstractMasterConnectionWrapper<TCPMasterConnection> {
    
    public TCPMasterConnectionWrapper(TCPMasterConnection masterConnection) {
        super(masterConnection);
    }

    @Override
    public void close() {
        getMasterConnection().close();
    }

    @Override
    public void connect() throws Exception {
        getMasterConnection().connect();
    }

    @Override
    public InetAddress getAddress() {
        return getMasterConnection().getAddress();
    }

    @Override
    public ModbusTransport getModbusTransport() {
        return getMasterConnection().getModbusTransport();
    }

    @Override
    public int getPort() {
        return getMasterConnection().getPort();
    }

    @Override
    public int getTimeout() {
        return getMasterConnection().getTimeout();
    }

    @Override
    public boolean isConnected() {
        return getMasterConnection().isConnected();
    }

    @Override
    public void setAddress(InetAddress address) {
        getMasterConnection().setAddress(address);
    }

    @Override
    public void setPort(int port) {
        getMasterConnection().setPort(port);
    }

    @Override
    public void setTimeout(int timeout) {
        getMasterConnection().setTimeout(timeout);
    }

    @Override
    public ModbusTransaction createTransaction() {
        return new ModbusTCPTransaction(getMasterConnection());
    }

    
}
