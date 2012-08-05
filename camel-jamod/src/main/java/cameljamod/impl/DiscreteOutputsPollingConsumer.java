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
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.util.BitVector;
import org.apache.camel.Processor;

/**
 * A Camel consumer which polls a Modbus device for its discrete outputs.
 *
 * @author Steven Swor
 */
public class DiscreteOutputsPollingConsumer extends ModbusPollingConsumer<ReadCoilsRequest, ReadCoilsResponse, BitVector> {

    /**
     * Creates a new DiscreteOutputsPollingConsumer.
     *
     * @param endpoint the endpoint
     * @param processor the processor
     */
    public DiscreteOutputsPollingConsumer(final JamodEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected ReadCoilsRequest createRequest() {
        return new ReadCoilsRequest(getReferenceAddress(), getCount());
    }

    @Override
    protected BitVector getBodyFromResponse(ReadCoilsResponse response) {
        return response.getCoils();
    }
}
