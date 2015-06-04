package instruction;

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
    public void Execute(VirtualMachine virtualMachine) {
        int r = getRegisterValue1();
        int r2 = getRegisterValue2();
        virtualMachine.setRegister(r, 0);
    }
}
