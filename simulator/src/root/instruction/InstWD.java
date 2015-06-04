package root.instruction;

import root.Constants;
import root.VirtualMachine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstWD extends SICXEInstruction {
    public InstWD(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int size = 1;
        int val = bytesToInteger(virtualMachine.getMemory(address, size));
        String deviceName = String.format("%02X", val & 0x000000FF);
        VirtualMachine.VirtualDevice device = virtualMachine.getDevice(deviceName);

        if(virtualMachine.getRegister(Constants.REGISTER_SW) == '<') {
            FileOutputStream fileOutputStream = device.getFileOutputStream();
            try {
                int b = virtualMachine.getRegister(Constants.REGISTER_A);
                fileOutputStream.write(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
