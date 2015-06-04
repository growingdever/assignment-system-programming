package root.instruction;

import root.Constants;
import root.VirtualMachine;
import root.instruction.SICXEInstruction;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstLDT extends SICXEInstruction {
    public InstLDT(byte[] wholeBytes, boolean isExtended) {
        super(wholeBytes, isExtended);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int size = isExtended ? 4 : 3;
        int val = bytesToInteger(virtualMachine.getMemory(address, size));
        virtualMachine.setRegister(Constants.REGISTER_T, val);
    }
}
