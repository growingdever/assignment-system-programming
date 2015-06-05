package root.instruction;

import root.Constants;
import root.VirtualMachine;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstJ extends SICXEInstruction {
    public InstJ(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public String execute(VirtualMachine virtualMachine) {
        if( isIndirect() ) {
            int address = getDestAddress(virtualMachine);
            byte[] memory = virtualMachine.getMemory(address, 3);

            int indirectAddress = bytesToInteger(memory);
            virtualMachine.setRegister(Constants.REGISTER_PC, indirectAddress);
            return String.format("Jump to %08X", indirectAddress);
        }

        virtualMachine.setRegister(Constants.REGISTER_PC, getDestAddress(virtualMachine));

        return String.format("Jump to %08X", getDestAddress(virtualMachine));
    }
}
