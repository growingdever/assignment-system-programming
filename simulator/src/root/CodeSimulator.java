package root;

import root.instruction.InstCLEAR;
import root.instruction.InstLDT;
import root.instruction.*;
import root.interfaces.SicSimulator;

import java.util.logging.Handler;

/**
 * Created by loki on 15. 5. 29..
 */
public class CodeSimulator implements SicSimulator {

    private VirtualMachine virtualMachine;
    private GUISimulator guiSimulator;
    private InstructionTable instructionTable;
    private InstructionData currInstruction;
    private String lastLog = "";

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
        lastLog = "";
    }

    public int calculateInstructionSize(int location) {
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

    public InstructionData getCurrInstructionData() {
        int opcode = getOpCode(virtualMachine.getRegisterPC());
        return instructionTable.FindInstructionDataByOpCode(opcode);
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
            case 0x3C:
                instruction = new InstJ(wholeBytes, isExtended);
                break;
            case 0x30:
                instruction = new InstJEQ(wholeBytes, isExtended);
                break;
            case 0x38:
                instruction = new InstJLT(wholeBytes, isExtended);
                break;
            case 0x48:
                instruction = new InstJSUB(wholeBytes, isExtended);
                break;
            case 0x4C:
                instruction = new InstRSUB(wholeBytes, isExtended);
                break;
            case 0xB4:
                instruction = new InstCLEAR(wholeBytes, isExtended);
                break;
            case 0x28:
                instruction = new InstCOMP(wholeBytes, isExtended);
                break;
            case 0xA0:
                instruction = new InstCOMPR(wholeBytes, isExtended);
                break;
            case 0xB8:
                instruction = new InstTIXR(wholeBytes, isExtended);
                break;
            case 0x00:
                instruction = new InstLDA(wholeBytes, isExtended);
                break;
            case 0x74:
                instruction = new InstLDT(wholeBytes, isExtended);
                break;
            case 0x54:
                instruction = new InstSTCH(wholeBytes, isExtended);
                break;
            case 0x50:
                instruction = new InstLDCH(wholeBytes, isExtended);
                break;
            case 0x0C:
                instruction = new InstSTA(wholeBytes, isExtended);
                break;
            case 0x10:
                instruction = new InstSTX(wholeBytes, isExtended);
                break;
            case 0xE0:
                instruction = new InstTD(wholeBytes, isExtended);
                break;
            case 0xD8:
                instruction = new InstRD(wholeBytes, isExtended);
                break;
            case 0xDC:
                instruction = new InstWD(wholeBytes, isExtended);
                break;
        }

        if (instruction != null) {
            virtualMachine.moveToNextPC();
            lastLog = instruction.execute(virtualMachine);
            currInstruction = instructionTable.FindInstructionDataByOpCode(opcode);
        }

        virtualMachine.affectVisualSimulator();
        addLog();
    }

    @Override
    public void allStep() {
        Runnable runnable = () -> {
            while(true) {
                oneStep();

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if( virtualMachine.getRegisterPC() == 0 ) {
                    break;
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void addLog() {
        guiSimulator.updateLogs(lastLog);
    }

    public String getLastLog() {
        return lastLog;
    }
}
