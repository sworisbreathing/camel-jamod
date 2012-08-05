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
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.util.BitVector;
import org.apache.camel.Processor;

/**
 * A Camel consumer which polls a Modbus device for its discrete inputs.
 *
 * @author Steven Swor
 */
public class DiscreteInputsPollingConsumer extends ModbusPollingConsumer<ReadInputDiscretesRequest, ReadInputDiscretesResponse, BitVector> {

    /**
     * Creates a new DiscreteInputsPollingConsumer.
     *
     * @param endpoint the endpoint
     * @param processor the processor
     */
    public DiscreteInputsPollingConsumer(final JamodEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected ReadInputDiscretesRequest createRequest() {
        return new ReadInputDiscretesRequest(getReferenceAddress(), getCount());
    }

    @Override
    protected BitVector getBodyFromResponse(ReadInputDiscretesResponse response) {
        return response.getDiscretes();
    }

    @Override
    protected boolean valueHasChanged(BitVector oldValue, BitVector newValue) {
        if (oldValue==null) {
            if (newValue!=null) {
                return true;
            }
        }else{
            if (newValue==null) {
                return true;
            }else{
                return !Arrays.equals(oldValue.getBytes(), newValue.getBytes());
            }
        }
        return false;
    }
    
    
}
