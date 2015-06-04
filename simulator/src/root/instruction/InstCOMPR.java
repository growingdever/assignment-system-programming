package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstCOMPR extends SICXEInstruction {
    public InstCOMPR(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int r1 = getRegisterValue1(virtualMachine);
        int r2 = getRegisterValue2(virtualMachine);

        if( r1 < r2 ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '<');
        } else if( r1 > r2 ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '>');
        } else {
            virtualMachine.setRegister(Constants.REGISTER_SW, '=');
        }
    }
}
