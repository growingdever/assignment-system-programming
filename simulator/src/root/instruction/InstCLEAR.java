package root.instruction;

import root.VirtualMachine;
import root.instruction.SICXEInstruction;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstCLEAR extends SICXEInstruction {
    public InstCLEAR(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        int r = getRegisterNumber1();
        virtualMachine.setRegister(r, 0);

        return String.format("register %d is now 0", r);
    }
}
