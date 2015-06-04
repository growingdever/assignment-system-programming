package root.instruction;

import root.Constants;
import root.VirtualMachine;

import java.io.File;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstTD extends SICXEInstruction {
    public InstTD(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int size = 1;
        int val = bytesToInteger(virtualMachine.getMemory(address, size));
        String deviceName = Integer.toString(val & 0xFF, 16).toUpperCase();

        File file = virtualMachine.getDevice(deviceName).getFile();
        if( file.canWrite() && file.canRead() ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '<');
        } else {
            virtualMachine.setRegister(Constants.REGISTER_SW, '=');
        }
    }
}
