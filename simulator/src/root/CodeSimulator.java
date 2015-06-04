package root;

import instruction.InstCLEAR;
import root.instruction.*;
import root.interfaces.SicSimulator;

/**
 * Created by loki on 15. 5. 29..
 */
public class CodeSimulator implements SicSimulator {

    private VirtualMachine virtualMachine;
    private GUISimulator guiSimulator;
    private InstructionTable instructionTable;


    public CodeSimulator() {
        instructionTable = new InstructionTable();
        instructionTable.LoadInstructionData("inst.data");
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void setGuiSimulator(GUISimulator guiSimulator) {
        this.guiSimulator = guiSimulator;
    }

    public void initialize() {
    }

    public int calculateInstructionSize(int location) {
        byte secondByte = virtualMachine.getMemory(location + 1, 1)[0];
        boolean isExtended = isExtendedInstruction(location);

        int opcode = getOpCode(location);
        InstructionData inst = instructionTable.FindInstructionDataByOpCode(opcode);
        if (inst.IsValidFormat(2)) {
            return 2;
        } else {
            if (isExtended) {
                return 4;
            }
            return 3;
        }
    }

    public int getOpCode(int location) {
        byte firstByte = virtualMachine.getMemory(location, 1)[0];
        int opcode = firstByte & 0x000000FC;
        return opcode;
    }

    public boolean isExtendedInstruction(int location) {
        byte secondByte = virtualMachine.getMemory(virtualMachine.getRegisterPC() + 1, 1)[0];
        boolean isExtended = (secondByte & 0x10) > 0;
        return isExtended;
    }

    @Override
    public void oneStep() {
        int opcode = getOpCode(virtualMachine.getRegisterPC());
        boolean isExtended = isExtendedInstruction(virtualMachine.getRegisterPC());
        int instructionSize = calculateInstructionSize(virtualMachine.getRegisterPC());
        virtualMachine.setCurrInstructionSize(instructionSize);

        byte[] wholeBytes = virtualMachine.getMemory(virtualMachine.getRegisterPC(), virtualMachine.getCurrInstructionSize());

        SICXEInstruction instruction = null;
        switch (opcode) {
            case 0x14:
                instruction = new InstSTL(wholeBytes, isExtended);
                break;
            case 0x48:
                instruction = new InstJSUB(wholeBytes, isExtended);
                break;
            case 0xB4:
                instruction = new InstCLEAR(wholeBytes, isExtended);
                break;
        }

        if (instruction != null) {
            virtualMachine.moveToNextPC();
            instruction.Execute(virtualMachine);
        }

        virtualMachine.affectVisualSimulator();
    }

    @Override
    public void allStep() {

    }

    @Override
    public void addLog() {

    }
}