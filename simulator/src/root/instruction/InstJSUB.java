package root.instruction;

import root.Constants;
import root.SICXEInstruction;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstJSUB extends SICXEInstruction {
    public InstJSUB(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int currPC = virtualMachine.getRegister(Constants.REGISTER_PC);
        virtualMachine.setRegister(Constants.REGISTER_L, currPC);

        if(isExtended) {
            int address = 0;
            address += (bytes[1] & 0xF) << 16;
            address += bytes[2] << 8;
            address += bytes[3];

            virtualMachine.setRegister(Constants.REGISTER_PC, address);
        } else {
            boolean isRelativePC = (bytes[1] & 0x20) > 0;
            boolean isRelativeBase = (bytes[1] & 0x40) > 0;

            int diff = 0;
            diff += (bytes[1] & 0xF) << 8;
            diff += (bytes[2] & 0xFF);

            int regNum = Constants.REGISTER_PC;
            if( isRelativePC ) {
                regNum = Constants.REGISTER_PC;
            } else if( isRelativeBase ) {
                regNum = Constants.REGISTER_B;
            }

            int address = virtualMachine.getRegister(regNum) + diff;
            virtualMachine.setRegister(Constants.REGISTER_PC, address);
        }
    }
}
