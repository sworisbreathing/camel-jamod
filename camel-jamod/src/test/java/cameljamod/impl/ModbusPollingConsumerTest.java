/*
 *  Copyright 2012 Steven Swor.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cameljamod.impl;

import java.util.Random;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.BitVector;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Steven Swor
 */
public class ModbusPollingConsumerTest {

    private static Random random = null;

    public ModbusPollingConsumerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        random = new Random();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        random = null;
    }

    /**
     * Tests {@link ModbusPollingConsumer#getCount()} and {@link
     * ModbusPollingConsumer#setCount(int)}.
     */
    @Test
    public void testGetCountAndSetCount() {
        ModbusPollingConsumer consumer = new ModbusPollingConsumerImpl();
        int expected;
        do {
            expected = random.nextInt();
        } while (expected == 0);
        consumer.setCount(expected);
        assertEquals(expected, consumer.getCount());
    }

    /**
     * Tests {@link ModbusPollingConsumer#getReferenceAddress()} and {@link
     * ModbusPollingConsumer#setReferenceAddress(int)}.
     */
    @Test
    public void testGetReferenceAddressAndSetReferenceAddress() {
        ModbusPollingConsumer consumer = new ModbusPollingConsumerImpl();
        int expected;
        do {
            expected = random.nextInt();
        } while (expected == 0);
        consumer.setReferenceAddress(expected);
        assertEquals(expected, consumer.getReferenceAddress());
    }

    /**
     * Tests {@link ModbusPollingConsumer#isChangesOnly()} and {@link
     * ModbusPollingConsumer#setChangesOnly(boolean)}.
     */
    @Test
    public void testIsChangesOnlyAndSetChangesOnly() {
        ModbusPollingConsumer consumer = new ModbusPollingConsumerImpl();
        boolean expected = !consumer.isChangesOnly();
        consumer.setChangesOnly(expected);
        assertEquals(expected, consumer.isChangesOnly());
    }


    /**
     * Tests {@link ModbusPollingConsumer#valueHasChanged(BitVector,
     * BitVector)}.
     */
    @Test
    public void testValueHasChanged_BitVector_BitVector() {
        ModbusPollingConsumer consumer = new ModbusPollingConsumerImpl();
        BitVector firstBits = new BitVector(1);
        BitVector secondBits = new BitVector(2);
        BitVector thirdBits = new BitVector(1);
        firstBits.setBit(0, random.nextBoolean());
        secondBits.setBit(0, random.nextBoolean());
        secondBits.setBit(1, random.nextBoolean());
        thirdBits.setBit(0, !firstBits.getBit(0));
        assertFalse(consumer.valueHasChanged((BitVector)null, (BitVector)null));
        assertTrue(consumer.valueHasChanged(firstBits, null));
        assertTrue(consumer.valueHasChanged(null, secondBits));
        assertTrue(consumer.valueHasChanged(firstBits, secondBits));
        assertTrue(consumer.valueHasChanged(firstBits, thirdBits));
        assertFalse(consumer.valueHasChanged(firstBits, firstBits));
    }

    /**
     * Test of valueHasChanged method, of class ModbusPollingConsumer.
     */
    @Test
    public void testValueHasChanged_InputRegisterArr_InputRegisterArr() {
        ModbusPollingConsumer consumer = new ModbusPollingConsumerImpl();
        byte[] firstBytes = new byte[2];
        byte[] secondBytes = new byte[2];
        byte[] thirdBytes = new byte[2];
        byte[] fourthBytes = new byte[2];
        random.nextBytes(firstBytes);
        random.nextBytes(secondBytes);
        random.nextBytes(thirdBytes);
        random.nextBytes(fourthBytes);
        InputRegister[] firstRegisters = new InputRegister[]{new SimpleInputRegister(firstBytes[0], firstBytes[1]), new SimpleInputRegister(secondBytes[0], secondBytes[1])};
        InputRegister[] secondRegisters = new InputRegister[]{new SimpleInputRegister(thirdBytes[0], thirdBytes[1]), new SimpleInputRegister(fourthBytes[0], fourthBytes[1])};
        assertFalse(consumer.valueHasChanged((InputRegister[])null, (InputRegister[]) null));
        assertTrue(consumer.valueHasChanged(null, secondRegisters));
        assertTrue(consumer.valueHasChanged(firstRegisters, null));
        assertFalse(consumer.valueHasChanged(firstRegisters, firstRegisters));
        assertTrue(consumer.valueHasChanged(firstRegisters, secondRegisters));
    }

    /**
     * Test of valueHasChanged method, of class ModbusPollingConsumer.
     */
    @Test
    public void testValueHasChanged_RegisterArr_RegisterArr() {
        ModbusPollingConsumer consumer = new ModbusPollingConsumerImpl();
        byte[] firstBytes = new byte[2];
        byte[] secondBytes = new byte[2];
        byte[] thirdBytes = new byte[2];
        byte[] fourthBytes = new byte[2];
        random.nextBytes(firstBytes);
        random.nextBytes(secondBytes);
        random.nextBytes(thirdBytes);
        random.nextBytes(fourthBytes);
        Register[] firstRegisters = new Register[]{new SimpleRegister(firstBytes[0], firstBytes[1]), new SimpleRegister(secondBytes[0], secondBytes[1])};
        Register[] secondRegisters = new Register[]{new SimpleRegister(thirdBytes[0], thirdBytes[1]), new SimpleRegister(fourthBytes[0], fourthBytes[1])};
        assertFalse(consumer.valueHasChanged((Register[])null, (Register[]) null));
        assertTrue(consumer.valueHasChanged(null, secondRegisters));
        assertTrue(consumer.valueHasChanged(firstRegisters, null));
        assertFalse(consumer.valueHasChanged(firstRegisters, firstRegisters));
        assertTrue(consumer.valueHasChanged(firstRegisters, secondRegisters));
    }

    public class ModbusPollingConsumerImpl extends ModbusPollingConsumer<ModbusRequest, ModbusResponse, Object> {

        public ModbusPollingConsumerImpl() {
            super(null, null);
        }

        public boolean valueHasChanged(Object oldValue, Object newValue) {
            return false;
        }

        public ModbusRequest createRequest() {
            return null;
        }

        public Object getBodyFromResponse(ModbusResponse response) {
            return null;
        }
    }
}
