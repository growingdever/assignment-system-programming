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
    public String execute(VirtualMachine virtualMachine) {
        int value = getValue(virtualMachine);
        int reg = virtualMachine.getRegister(Constants.REGISTER_A);

        char c;
        if( reg < value ) {
            c = '<';
        } else if( reg > value ) {
            c = '>';
        } else {
            c = '=';
        }

        virtualMachine.setRegister(Constants.REGISTER_SW, c);

        return String.format("Register SW : %c", c);
    }
}
