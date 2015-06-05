package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstJLT extends SICXEInstruction {
    public InstJLT(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        if(virtualMachine.getRegister(Constants.REGISTER_SW) == '<') {
            virtualMachine.setRegister(Constants.REGISTER_PC, getDestAddress(virtualMachine));
            return String.format("Jump to %08X", getDestAddress(virtualMachine));
        }

        return "SW != \'<\'";
    }
}
