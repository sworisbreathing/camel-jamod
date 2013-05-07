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

import cameljamod.net.AbstractMasterConnectionWrapper;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 *
 * @author Steven Swor
 */
public abstract class ModbusProducer<RequestType extends ModbusRequest, ResponseType extends ModbusResponse, DataType> extends DefaultProducer {

    private final JamodEndpoint endpoint;
    /**
     * The reference address of the first value to write.
     */
    private int referenceAddress;
    
    /**
     * The Modbus Slave ID
     */
    private int slaveId = 0;

    public ModbusProducer(JamodEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
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
     * Gets the Modbus Slave ID.
     *
     * @return the Slave ID
     */
    public int getSlaveId() {
		return slaveId;
	}

    /**
     * Sets the Modbus Slave ID.
     *
     * @param slaveId the Slave ID
     */
	public void setSlaveId(int slaveId) {
		this.slaveId = slaveId;
	}    

    @Override
    public void process(Exchange exchange) throws Exception {
        DataType data = exchange.getIn().getBody(getDataTypeClass());
        RequestType request = createRequest(data);
        request.setUnitID(slaveId);
        AbstractMasterConnectionWrapper connectionWrapper = endpoint.getConnection();
        ModbusTransaction transaction = connectionWrapper.createTransaction();
        transaction.setRequest(request);
        transaction.execute();
        ResponseType response = (ResponseType) transaction.getResponse();
        exchange.getOut().setBody(response);
    }

    protected abstract Class<DataType> getDataTypeClass();

    protected abstract RequestType createRequest(DataType data);
}
