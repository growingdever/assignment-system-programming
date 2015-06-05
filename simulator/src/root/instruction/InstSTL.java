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
    public String execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int regValueL = virtualMachine.getRegister(Constants.REGISTER_L);
        virtualMachine.setMemory(address, getByteFromRegisterValue(regValueL), 3);

        return String.format("Store registerL value %08X to M[%08X]", regValueL, address);
    }
}
