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
        int value = 0;

        if( isSimple() ) {
            value = virtualMachine.getMemory(address, 1)[0];
        } else if( isImmediate() ) {
            value = getDisplacement();
        } else if( isIndirect() ) {
            byte[] memory = virtualMachine.getMemory(address, 3);
            int indirectAddress = bytesToInteger(memory);
            value = bytesToInteger(virtualMachine.getMemory(indirectAddress, 3));
        }

        virtualMachine.setRegister(Constants.REGISTER_A, value);
    }
}
