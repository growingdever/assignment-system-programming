package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstSTCH extends SICXEInstruction {
    public InstSTCH(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine) + virtualMachine.getRegister(Constants.REGISTER_X);
        int regValue = virtualMachine.getRegister(Constants.REGISTER_A);
        virtualMachine.setMemory(address, getByteFromRegisterValue(regValue), 1);

        return String.format("Store registerA value %08X to M[%08X]", regValue, address);
    }
}
