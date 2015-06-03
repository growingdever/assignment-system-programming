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

    public CodeSimulator() {
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void setGuiSimulator(GUISimulator guiSimulator) {
        this.guiSimulator = guiSimulator;
    }

    private void loadProgram(String path) {
    }


    public void updateRegisterValue(HashMap<String, Integer> registerValueMap) {
        if( registerValueMap == null ) {
            return;
        }

//        registerValueMap.put("A", regA);
//        registerValueMap.put("X", regX);
//        registerValueMap.put("L", regL);
//        registerValueMap.put("PC", regPC);
//        registerValueMap.put("SW", regSW);
//        registerValueMap.put("B", regB);
//        registerValueMap.put("S", regS);
//        registerValueMap.put("T", regT);
//        registerValueMap.put("F", regF);
    }

    @Override
    public void oneStep() {

    }

    @Override
    public void allStep() {

    }

    @Override
    public void addLog() {

    }

}
