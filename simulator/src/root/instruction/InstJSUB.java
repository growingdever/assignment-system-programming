package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstJSUB extends SICXEInstruction {
    public InstJSUB(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        int currPC = virtualMachine.getRegister(Constants.REGISTER_PC);
        virtualMachine.setRegister(Constants.REGISTER_L, currPC);
        virtualMachine.setRegister(Constants.REGISTER_PC, getDestAddress(virtualMachine));

        return String.format("Register L : %08X, Jump to %08X", currPC, getDestAddress(virtualMachine));
    }
}
