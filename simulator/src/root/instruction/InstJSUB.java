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

        int address = 0;
        if(isExtended) {
            address += (bytes[1] & 0xF) << 16;
            address += bytes[2] << 8;
            address += bytes[3];

            virtualMachine.setRegister(Constants.REGISTER_PC, address);
        } else {
            address += (bytes[1] & 0xF) << 16;
            address += bytes[2] << 8;
            address += bytes[3];

            virtualMachine.setRegister(Constants.REGISTER_PC, address);
        }
    }
}
