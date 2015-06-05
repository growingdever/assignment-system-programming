package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstTIXR extends SICXEInstruction {
    public InstTIXR(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        int r1 = getRegisterValue1(virtualMachine);
        int x = virtualMachine.getRegister(Constants.REGISTER_X);
        x ++;
        virtualMachine.setRegister(Constants.REGISTER_X, x);


        if( x < r1 ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '<');
        } else if( x > r1 ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '>');
        } else {
            virtualMachine.setRegister(Constants.REGISTER_SW, '=');
        }

        return String.format("Increase registerX, set registerSW to %c", (int)virtualMachine.getRegister(Constants.REGISTER_SW));
    }
}
