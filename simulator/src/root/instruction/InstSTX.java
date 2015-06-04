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
    public void Execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int regValue = virtualMachine.getRegister(Constants.REGISTER_X);
        int size = isExtended ? 4 : 3;
        virtualMachine.setMemory(address, getByteFromRegisterValue(regValue), size);
    }
}
