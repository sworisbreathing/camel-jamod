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
import java.util.Arrays;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.util.BitVector;
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
     * Only send messages when the polled value changes.
     */
    private boolean changesOnly;
    
    /**
     * The last polled value.
     */
    private BodyType lastPolledValue;

    /**
     * Creates a new ModbusPollingConsumer.
     *
     * @param endpoint the endpoint
     * @param processor the processor
     */
    public ModbusPollingConsumer(JamodEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.lastPolledValue = null;
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

    /**
     * Determines whether to send messages every time or only when the value
     * changes.
     * @return {@code true} if messages will only be sent when the value
     * changes, {@code false} if messages will be sent every polling interval.
     */
    public boolean isChangesOnly() {
        return changesOnly;
    }

    /**
     * Sets whether to send messages every time or only when the value changes.
     * @param changesOnly {@code true} if messages will only be sent when the
     * value changes, {@code false} if messages will be sent every polling
     * interval.
     */
    public void setChangesOnly(boolean changesOnly) {
        this.changesOnly = changesOnly;
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
        BodyType currentValue = getBodyFromResponse(response);
        BodyType tmp = lastPolledValue;
        lastPolledValue = currentValue;
        if (!isChangesOnly() || valueHasChanged(tmp, currentValue)) {
            Message message = exchange.getIn();
            message.setBody(currentValue);
            getProcessor().process(exchange);
            return 1;
        }else{
            return 0;
        }
    }
    
    /**
     * Determines if a polled value has changed.
     * @param oldValue the old value.
     * @param newValue the new value.
     * @return whether or not the polled value has changed
     */
    protected abstract boolean valueHasChanged(BodyType oldValue, BodyType newValue);

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
    
    protected boolean valueHasChanged(BitVector oldValue, BitVector newValue) {
        if (oldValue==null) {
            if (newValue!=null) {
                return true;
            }
        }else{
            if (newValue==null) {
                return true;
            }else{
                if (oldValue.size()!=newValue.size()) {
                    return true;
                }
                return !Arrays.equals(oldValue.getBytes(), newValue.getBytes());
            }
        }
        return false;
    }
    
    protected boolean valueHasChanged(InputRegister[] oldValue, InputRegister[] newValue) {
        if (oldValue == null) {
            if (newValue != null) {
                // old was null, new is non-null
                return true;
            }
        } else {
            if (newValue==null) {
                // old was non-null, new is null
                return true;
            }else if (oldValue.length!=newValue.length) {
                // different lengths
                return true;
            }else{
                // Check for changes, register-by-register and byte-by-byte
                for (int i=0; i < oldValue.length; i++) {
                    if (!Arrays.equals(oldValue[i].toBytes(), newValue[i].toBytes())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected boolean valueHasChanged(final Register[] oldValue, final Register[] newValue) {
        return valueHasChanged((InputRegister[])oldValue, (InputRegister[])newValue);
    }
}
