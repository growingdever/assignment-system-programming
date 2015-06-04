package root.instruction;

import root.Constants;
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

    public int getDisplacement() {
        int disp = 0;
        disp += (bytes[1] & 0xF) << 8;
        disp += (bytes[2] & 0xFF);

        return disp;
    }

    public byte[] getByteFromRegisterValue(int regValue) {
        int size = isExtended ? 4 : 3;
        byte[] bytes = new byte[size];
        if( size == 3 ) {
            bytes[0] = (byte) (regValue & 0x000000FF);
            bytes[1] = (byte) (regValue & 0x0000FF00);
            bytes[2] = (byte) (regValue & 0x00FF0000);
        } else {
            bytes[0] = (byte) (regValue & 0x000000FF);
            bytes[1] = (byte) (regValue & 0x0000FF00);
            bytes[2] = (byte) (regValue & 0x00FF0000);
            bytes[3] = (byte) (regValue & 0xFF000000);
        }

        return bytes;
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

    public int getSize() {
        return bytes.length;
    }

    public abstract void Execute(VirtualMachine virtualMachine);
}
