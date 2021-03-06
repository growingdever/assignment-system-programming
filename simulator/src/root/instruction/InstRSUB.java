package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstRSUB extends SICXEInstruction {
    public InstRSUB(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        int next = virtualMachine.getRegister(Constants.REGISTER_L);
        virtualMachine.setRegister(Constants.REGISTER_PC, next);

        return String.format("RSUB : Jump to %08X", next);
    }
}
