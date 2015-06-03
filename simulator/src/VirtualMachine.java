import interfaces.ResourceManager;

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


    public VirtualMachine() {
        initializeMemory();
        initializeRegister();
    }

    @Override
    public void initializeMemory() {
        registers = new int[16];
        memory = new byte[2 << 15];
        lastMemoryAddress = 0;
    }

    @Override
    public void initializeRegister() {
        for (int i = 0; i < registers.length; i++) {
            registers[i] = 0;
        }
    }

    @Override
    public void initialDevice(String devName) {

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
}
