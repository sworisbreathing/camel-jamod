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
package cameljamod;

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
}
