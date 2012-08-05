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

import java.util.Arrays;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.procimg.InputRegister;
import org.apache.camel.Processor;

/**
 * A Camel consumer which polls a Modbus device for its input registers.
 *
 * @author Steven Swor
 */
public class InputRegistersPollingConsumer extends ModbusPollingConsumer<ReadInputRegistersRequest, ReadInputRegistersResponse, InputRegister[]> {

    /**
     * Creates a new DiscreteInputsPollingConsumer.
     *
     * @param endpoint the endpoint
     * @param processor the processor
     */
    public InputRegistersPollingConsumer(final JamodEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected ReadInputRegistersRequest createRequest() {
        return new ReadInputRegistersRequest(getReferenceAddress(), getCount());
    }

    @Override
    protected InputRegister[] getBodyFromResponse(ReadInputRegistersResponse response) {
        return response.getRegisters();
    }

    @Override
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
}
