package root.instruction;

import root.Constants;
import root.VirtualMachine;
import root.instruction.SICXEInstruction;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstLDT extends SICXEInstruction {
    public InstLDT(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int val = getValue(virtualMachine);
        virtualMachine.setRegister(Constants.REGISTER_T, val);
    }
}
