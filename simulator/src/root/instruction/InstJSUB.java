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
    public void Execute(VirtualMachine virtualMachine) {
        int currPC = virtualMachine.getRegister(Constants.REGISTER_PC);
        virtualMachine.setRegister(Constants.REGISTER_L, currPC);
        virtualMachine.setRegister(Constants.REGISTER_PC, getDestAddress(virtualMachine));
    }
}
