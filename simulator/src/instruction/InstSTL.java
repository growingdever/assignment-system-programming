package instruction;

import root.SICXEInstruction;
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

    }
}
