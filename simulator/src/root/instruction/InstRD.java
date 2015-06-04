package root.instruction;

import root.Constants;
import root.VirtualMachine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by loki on 15. 6. 4..
 */
public class InstRD extends SICXEInstruction {
    public InstRD(byte[] bytes, boolean e) {
        super(bytes, e);
    }

    @Override
    public void Execute(VirtualMachine virtualMachine) {
        int address = getDestAddress(virtualMachine);
        int size = 1;
        int val = bytesToInteger(virtualMachine.getMemory(address, size));
        String deviceName = Integer.toString(val & 0xFF, 16).toUpperCase();
        VirtualMachine.VirtualDevice device = virtualMachine.getDevice(deviceName);

        if(virtualMachine.getRegister(Constants.REGISTER_SW) == '<') {
            FileInputStream fileInputStream = device.getFileInputStream();
            try {
                int b = fileInputStream.read();
                virtualMachine.setRegister(Constants.REGISTER_A, b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
