package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstCOMP extends SICXEInstruction {
    public InstCOMP(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int value = getValue(virtualMachine);
        int reg = virtualMachine.getRegister(Constants.REGISTER_A);
        if( reg < value ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '<');
        } else if( reg > value ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '>');
        } else {
            virtualMachine.setRegister(Constants.REGISTER_SW, '=');
        }
    }
}
