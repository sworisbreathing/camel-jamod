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

import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.procimg.Register;
import org.apache.camel.Processor;

/**
 * A Camel consumer which polls a Modbus device for its registers.
 *
 * @author Steven Swor
 */
public class RegistersPollingConsumer extends ModbusPollingConsumer<ReadMultipleRegistersRequest, ReadMultipleRegistersResponse, Register[]> {

    /**
     * Creates a new DiscreteInputsPollingConsumer.
     *
     * @param endpoint the endpoint
     * @param processor the processor
     */
    public RegistersPollingConsumer(final JamodEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected ReadMultipleRegistersRequest createRequest() {
        return new ReadMultipleRegistersRequest(getReferenceAddress(), getCount());
    }

    @Override
    protected Register[] getBodyFromResponse(ReadMultipleRegistersResponse response) {
        return response.getRegisters();
    }
}
