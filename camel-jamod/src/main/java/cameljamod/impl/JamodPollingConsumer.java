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
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.PollingConsumerSupport;

/**
 *
 * @author Steven Swor
 */
public class JamodPollingConsumer extends PollingConsumerSupport {

    private final JamodEndpoint endpoint;

    public JamodPollingConsumer(final JamodEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        
    }

    @Override
    protected void doStop() throws Exception {
        
    }
    
    

    public Exchange receive() {
        return doReceive(-1);
    }

    public Exchange receive(long timeout) {
        return doReceive((int) timeout);
    }

    public Exchange receiveNoWait() {
        return doReceive(-1);
    }

    protected Exchange doReceive(int timeout) {
        Exchange exchange = endpoint.createExchange();
        try {
            AbstractMasterConnectionWrapper connectionWrapper = endpoint.getConnection();
            if (timeout > 0) {
                connectionWrapper.setTimeout(timeout);
            }
            //create a transaction and execute
            ReadInputDiscretesRequest request = new ReadInputDiscretesRequest();
            ModbusTransaction transaction = connectionWrapper.createTransaction();
            transaction.setRequest(request);
            transaction.execute();
            ReadInputDiscretesResponse response = (ReadInputDiscretesResponse) transaction.getResponse();
            Message message = exchange.getOut();
            message.setBody(response.getDiscretes());
            return exchange;
        } catch (ModbusException ex) {
            throw new RuntimeCamelException(ex);
        }
    }
}
