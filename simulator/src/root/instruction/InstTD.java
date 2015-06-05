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
    public String execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int size = 1;
        int val = bytesToInteger(virtualMachine.getMemory(address, size));
        String deviceName = String.format("%02X", val & 0x000000FF);

        File file = virtualMachine.getDevice(deviceName).getFile();
        if( file.canWrite() && file.canRead() ) {
            virtualMachine.setRegister(Constants.REGISTER_SW, '<');
            return "Test Device - Valid";
        } else {
            virtualMachine.setRegister(Constants.REGISTER_SW, '=');
            return "Test Device - Invalid";
        }
    }
}
