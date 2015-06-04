package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstSTL extends SICXEInstruction {
    public InstSTL(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int regValueL = virtualMachine.getRegister(Constants.REGISTER_L);
        virtualMachine.setMemory(address, getByteFromRegisterValue(regValueL), 3);
    }
}
