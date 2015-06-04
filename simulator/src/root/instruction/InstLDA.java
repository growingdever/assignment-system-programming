package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstLDA extends SICXEInstruction {
    public InstLDA(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int val = getValue(virtualMachine);
        virtualMachine.setRegister(Constants.REGISTER_A, val);
    }
}
