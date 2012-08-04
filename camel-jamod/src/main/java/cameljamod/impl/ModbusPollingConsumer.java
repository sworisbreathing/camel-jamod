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
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultScheduledPollConsumer;

/**
 * Parent class for camel consumers which poll modbus devices.
 * 
 * @author Steven Swor
 */
public abstract class ModbusPollingConsumer<RequestType extends ModbusRequest, ResponseType extends ModbusResponse, BodyType> extends DefaultScheduledPollConsumer {

    /**
     * The endpoint.
     */
    private final JamodEndpoint endpoint;
    
    /**
     * The reference address of the first value to read.
     */
    private int referenceAddress;
    
    /**
     * The number of values to read.
     */
    private int count;

    /**
     * Creates a new ModbusPollingConsumer.
     *
     * @param endpoint the endpoint
     * @param processor the processor
     */
    public ModbusPollingConsumer(JamodEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    /**
     * Gets the number of values to read.
     *
     * @return the number of values to read
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the number of values to read.
     *
     * @param count the number of values to read
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Gets the reference address.
     *
     * @return the reference address
     */
    public int getReferenceAddress() {
        return referenceAddress;
    }

    /**
     * Sets the reference address.
     *
     * @param referenceAddress the reference address
     */
    public void setReferenceAddress(int referenceAddress) {
        this.referenceAddress = referenceAddress;
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = endpoint.createExchange();
        AbstractMasterConnectionWrapper connectionWrapper = endpoint.getConnection();
        //create a transaction and execute
        RequestType request = createRequest();
        ModbusTransaction transaction = connectionWrapper.createTransaction();
        transaction.setRequest(request);
        transaction.execute();
        ResponseType response = (ResponseType) transaction.getResponse();
        Message message = exchange.getIn();
        message.setBody(getBodyFromResponse(response));
        getProcessor().process(exchange);
        return 1;
    }

    /**
     * Creates a new request to send to the modbus device.
     *
     * @return a new request to send to the modbus device
     */
    protected abstract RequestType createRequest();

    /**
     * Gets the body from the response.
     *
     * @param response the modbus response
     * @return the body of the modbus response
     */
    protected abstract BodyType getBodyFromResponse(final ResponseType response);
}
