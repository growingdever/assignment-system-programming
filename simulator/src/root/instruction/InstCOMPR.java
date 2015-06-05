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
    public String execute(VirtualMachine virtualMachine) {
        int r1 = getRegisterValue1(virtualMachine);
        int r2 = getRegisterValue2(virtualMachine);

        char c;
        if( r1 < r2 ) {
            c = '<';
        } else if( r1 > r2 ) {
            c = '>';
        } else {
            c = '=';
        }
        virtualMachine.setRegister(Constants.REGISTER_SW, c);

        return String.format("Register SW : %c", c);
    }
}
