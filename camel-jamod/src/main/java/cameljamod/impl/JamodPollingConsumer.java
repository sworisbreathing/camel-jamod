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
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultScheduledPollConsumer;

/**
 *
 * @author Steven Swor
 */
public class JamodPollingConsumer extends DefaultScheduledPollConsumer {

    private final JamodEndpoint endpoint;
    
    private int referenceAddress;
    
    private int discreteInputCount;

    public JamodPollingConsumer(final JamodEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.referenceAddress = endpoint.getReferenceAddress();
        this.discreteInputCount = endpoint.getDiscreteInputCount();
        setDelay(endpoint.getDelay());
        setInitialDelay(endpoint.getInitialDelay());
    }

    public int getDiscreteInputCount() {
        return discreteInputCount;
    }

    public void setDiscreteInputCount(int discreteInputCount) {
        this.discreteInputCount = discreteInputCount;
    }

    public int getReferenceAddress() {
        return referenceAddress;
    }

    public void setReferenceAddress(int referenceAddress) {
        this.referenceAddress = referenceAddress;
    }
    
    protected Exchange doReceive(int timeout) {
        Exchange exchange = endpoint.createExchange();
        try {
            AbstractMasterConnectionWrapper connectionWrapper = endpoint.getConnection();
            if (timeout > 0) {
                connectionWrapper.setTimeout(timeout);
            }
            //create a transaction and execute
            ReadInputDiscretesRequest request = new ReadInputDiscretesRequest(getReferenceAddress(), getDiscreteInputCount());
            ModbusTransaction transaction = connectionWrapper.createTransaction();
            transaction.setRequest(request);
            transaction.execute();
            ReadInputDiscretesResponse response = (ReadInputDiscretesResponse) transaction.getResponse();
            Message message = exchange.getIn();
            message.setBody(response.getDiscretes());
            return exchange;
        } catch (ModbusException ex) {
            throw new RuntimeCamelException(ex);
        }
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = doReceive(1000);
        if (exchange==null) {
            return 0;
        }else{
            getProcessor().process(exchange);
            return 1;
        }
    }
}
