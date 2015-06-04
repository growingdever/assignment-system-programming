package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstLDCH extends SICXEInstruction {
    public InstLDCH(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int value = virtualMachine.getMemory(address, 1)[0];
        virtualMachine.setRegister(Constants.REGISTER_A, value);
    }
}
