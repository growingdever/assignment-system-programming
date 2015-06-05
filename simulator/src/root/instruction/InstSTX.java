package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstSTX extends SICXEInstruction {
    public InstSTX(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int regValue = virtualMachine.getRegister(Constants.REGISTER_X);
        virtualMachine.setMemory(address, getByteFromRegisterValue(regValue), 3);

        return String.format("Store registerX value %08X to M[%08X]", regValue, address);
    }
}
