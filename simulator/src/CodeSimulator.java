import interfaces.SicSimulator;
import interfaces.VisualSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

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

    @Override
    public void oneStep() {
        // instruction check
        byte firstByte = virtualMachine.getMemory(virtualMachine.getCurrMemoryIndex(), 1)[0];
        byte secondByte = virtualMachine.getMemory(virtualMachine.getCurrMemoryIndex() + 1, 1)[0];
        int opcode = firstByte & 0x000000FC;
        boolean isExtended = (secondByte & 0x10) > 0;

        InstructionData inst = instructionTable.FindInstructionDataByOpCode(opcode);
        if( inst.IsValidFormat(2) ) {
            // format 2
            virtualMachine.setCurrInstructionSize(2);
        } else {
            // format 3 or 4
            if( isExtended ) {
                virtualMachine.setCurrInstructionSize(4);
            } else {
                virtualMachine.setCurrInstructionSize(3);
            }
        }


        virtualMachine.affectVisualSimulator();
        virtualMachine.setCurrMemoryIndex(virtualMachine.getCurrMemoryIndex() + virtualMachine.getCurrInstructionSize());
    }

    @Override
    public void allStep() {

    }

    @Override
    public void addLog() {

    }
}
