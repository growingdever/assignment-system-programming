package root;

import root.interfaces.ResourceManager;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by loki on 15. 6. 3..
 */
public class VirtualMachine implements ResourceManager {

    private GUISimulator guiSimulator;
    private CodeSimulator codeSimulator;
    private ObjectCodeLoader objectCodeLoader;

    private int[] registers;
    private byte[] memory;
    private int lastMemoryAddress;
    private String programName;
    private int currInstructionSize;
    private HashMap<String, VirtualDevice> devices;


    public VirtualMachine() {
        initializeMemory();
        initializeRegister();
        initialDevices();
    }

    @Override
    public void initializeMemory() {
        registers = new int[16];
        memory = new byte[2 << 15];
        lastMemoryAddress = 0;
        devices = new HashMap<>();
    }

    @Override
    public void initializeRegister() {
        for (int i = 0; i < registers.length; i++) {
            registers[i] = 0;
        }
    }

    public void initialDevices() {
        devices = new HashMap<>();
    }

    @Override
    public void initialDevice(String devName) {
        File dir = new File("./dev");
        dir.mkdir();

        File file = new File("./dev/" + devName);
        if( ! file.exists() ) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        VirtualDevice virtualDevice = new VirtualDevice(file);
        devices.put(devName, virtualDevice);
    }

    public VirtualDevice getDevice(String deviceName) {
        return devices.get(deviceName);
    }

    @Override
    public void setMemory(int locate, byte[] data, int size) {
        for (int i = 0; i < size; i++) {
            memory[locate + i] = data[i];
        }
    }

    @Override
    public void setRegister(int regNum, int value) {
        registers[regNum] = value;
    }

    @Override
    public byte[] getMemory(int locate, int size) {
        byte[] ret = new byte[size];
        for (int i = 0; i < size; i++) {
            ret[i] = memory[locate + i];
        }

        return ret;
    }

    public byte[] getMemory() {
        return memory;
    }

    @Override
    public int getRegister(int regNum) {
        return registers[regNum];
    }

    @Override
    public void affectVisualSimulator() {
        guiSimulator.updateRegisters(this.registers);
        guiSimulator.updateProgramInformation(programName, lastMemoryAddress);
        guiSimulator.updateMemoryDump();
        guiSimulator.updateLogs();
    }

    public int reserveMemory(int size) {
        int prev = lastMemoryAddress;
        lastMemoryAddress += size;

        return prev;
    }

    public void setGuiSimulator(GUISimulator guiSimulator) {
        this.guiSimulator = guiSimulator;
    }

    public void setCodeSimulator(CodeSimulator codeSimulator) {
        this.codeSimulator = codeSimulator;
    }

    public void setObjectCodeLoader(ObjectCodeLoader objectCodeLoader) {
        this.objectCodeLoader = objectCodeLoader;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getRegisterPC() {
        return registers[Constants.REGISTER_PC];
    }

    public int getCurrMemoryIndex() {
        return registers[Constants.REGISTER_PC] - currInstructionSize;
    }

    public void moveToNextPC() {
        registers[Constants.REGISTER_PC] += currInstructionSize;
    }

    public int getCurrInstructionSize() {
        return currInstructionSize;
    }

    public void setCurrInstructionSize(int currInstructionSize) {
        this.currInstructionSize = currInstructionSize;
        registers[Constants.REGISTER_PC] = getCurrMemoryIndex() + currInstructionSize;
    }

    public void printMemoryDump() {
        for(int i = 0; i < memory.length; i ++) {
            if( i % 4 == 0 && i > 0 ) {
                System.out.print(" ");
            }
            if( i % 16 == 0 && i > 0 ) {
                System.out.println();
            }

            char c1 = (char) ((memory[i] & 0x000000F0) >> 4);
            char c2 = (char) (memory[i] & 0x0000000F);

            System.out.print(Util.digitToHex(c1));
            System.out.print(Util.digitToHex(c2));
        }
    }

    public class VirtualDevice {
        private File file;
        private FileInputStream fileInputStream;
        private FileOutputStream fileOutputStream;

        public VirtualDevice(File file) {
            this.file = file;
            try {
                this.fileInputStream = new FileInputStream(file);
                this.fileOutputStream = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public File getFile() {
            return file;
        }

        public FileInputStream getFileInputStream() {
            return fileInputStream;
        }

        public FileOutputStream getFileOutputStream() {
            return fileOutputStream;
        }
    }
}
