package root.instruction;

import root.Constants;
import root.Util;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public abstract class SICXEInstruction {
    protected byte[] bytes;
    protected boolean isExtended;

    public SICXEInstruction(byte[] bytes, boolean e) {
        this.bytes = bytes;
        isExtended = e;
    }

    public boolean isIndirect() {
        return (bytes[0] & 0x2) > 0 && (bytes[0] & 0x1) == 0;
    }

    public boolean isImmediate() {
        return (bytes[0] & 0x2) == 0 && (bytes[0] & 0x1) > 0;
    }

    public boolean isSimple() {
        return !isIndirect() && !isImmediate();
    }

    public int getDisplacement() {
        char c1 = (char) (bytes[1] & 0xF);
        char c2 = (char) ((bytes[2] & 0xF0) >> 4);
        char c3 = (char) (bytes[2] & 0x0F);

        c1 = Util.digitToHex(c1);
        c2 = Util.digitToHex(c2);
        c3 = Util.digitToHex(c3);
        String str = "" + c1 + c2 + c3;

        return Util.twosComp(str, 12);
    }

    public byte[] getByteFromRegisterValue(int regValue) {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) (regValue & 0x000000FF);
        bytes[1] = (byte) ((regValue & 0x0000FF00) >> 8);
        bytes[2] = (byte) ((regValue & 0x00FF0000) >> 16);

        return bytes;
    }

    public int bytesToInteger(byte[] input) {
        int v = 0;
        for(int i = 0; i < input.length; i ++) {
            v += input[i] << (8 * i);
        }

        return v;
    }

    public int getRegisterNumber1() {
        return (bytes[1] & 0x000000F0) >> 4;
    }

    public int getRegisterNumber2() {
        return (bytes[1] & 0x0000000F);
    }

    public int getRegisterValue1(VirtualMachine virtualMachine) {
        return virtualMachine.getRegister(getRegisterNumber1());
    }

    public int getRegisterValue2(VirtualMachine virtualMachine) {
        return virtualMachine.getRegister(getRegisterNumber2());
    }

    public int getDestAddress(VirtualMachine virtualMachine) {
        if( isExtended ) {
            int address = 0;
            address += (bytes[1] & 0xF) << 16;
            address += bytes[2] << 8;
            address += bytes[3];

            return address;
        }

        boolean isRelativePC = (bytes[1] & 0x20) > 0;
        boolean isRelativeBase = (bytes[1] & 0x40) > 0;

        int address = 0;
        int diff = getDisplacement();

        if( isRelativePC ) {
            address = virtualMachine.getRegister(Constants.REGISTER_PC) + diff;
        } else if( isRelativeBase ) {
            address = virtualMachine.getRegister(Constants.REGISTER_B) + diff;
        }

        return address;
    }

    public int getValue(VirtualMachine virtualMachine) {
        if( isImmediate() ) {
            return getDisplacement();
        } else if( isIndirect() ) {
            int address = getDestAddress(virtualMachine);
            byte[] memory = virtualMachine.getMemory(address, 3);

            int indirectAddress = bytesToInteger(memory);
            return bytesToInteger(virtualMachine.getMemory(indirectAddress, 3));
        } else {
            int address = getDestAddress(virtualMachine);
            byte[] memory = virtualMachine.getMemory(address, 3);
            return bytesToInteger(memory);
        }
    }

    public int getSize() {
        return bytes.length;
    }

    public abstract String execute(VirtualMachine virtualMachine);
}
