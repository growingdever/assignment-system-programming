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
        String deviceName = String.format("%02X", val & 0x000000FF);
        VirtualMachine.VirtualDevice device = virtualMachine.getDevice(deviceName);

        if(virtualMachine.getRegister(Constants.REGISTER_SW) == '<') {
            FileInputStream fileInputStream = device.getFileInputStream();
            try {
                int b = fileInputStream.read();
                if( b == -1 ){
                    virtualMachine.setRegister(Constants.REGISTER_A, 0);
                } else {
                    virtualMachine.setRegister(Constants.REGISTER_A, b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
