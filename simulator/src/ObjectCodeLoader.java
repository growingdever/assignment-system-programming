import interfaces.ResourceManager;
import interfaces.SicLoader;
import interfaces.SicSimulator;
import interfaces.VisualSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Created by loki on 15. 6. 3..
 */
public class ObjectCodeLoader implements SicLoader {

    private VirtualMachine virtualMachine;
    private CodeSimulator codeSimulator;

    private String currSectionName;
    private int currSectionStartAddress;
    private int next;

    HashMap<String, ArrayList<LinkingOperation>> linkingOperations;
    HashMap<String, Integer> symbolTable;

    public ObjectCodeLoader() {
        linkingOperations = new HashMap<>();
        symbolTable = new HashMap<>();
    }

    @Override
    public void load(File objFile) {
        Scanner scanner;
        try {
            scanner = new Scanner(objFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        currSectionName = "";

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            readLine(line);
        }

        linking();

        byte[] bytes = virtualMachine.getMemory(0, 8192);
        for(int i = 0; i < bytes.length; i ++) {
            if( i % 4 == 0 && i > 0 ) {
                System.out.print(" ");
            }
            if( i % 16 == 0 && i > 0 ) {
                System.out.println();
            }

            char c1 = (char) ((bytes[i] & 0x000000F0) >> 4);
            char c2 = (char) (bytes[i] & 0x0000000F);

            System.out.print(Util.digitToHex(c1));
            System.out.print(Util.digitToHex(c2));
        }

        virtualMachine.affectVisualSimulator();
    }

    @Override
    public void readLine(String line) {
        if( line.length() == 0 || line.equals("\n") ) {
            return;
        }

        if( line.charAt(0) == 'H' ) {
            handleHeader(line.substring(1));
        } else if( line.charAt(0) == 'D' ) {
            handleExternalDefine(line.substring(1));
        } else if( line.charAt(0) == 'R' ) {
            handleExternalReference(line.substring(1));
        } else if( line.charAt(0) == 'T' ) {
            handleText(line.substring(1));
        } else if( line.charAt(0) == 'M' ) {
            handleModification(line.substring(1));
        } else if( line.charAt(0) == 'E' ) {
            handleEnd(line.substring(1));
        }
    }

    void handleHeader(String line) {
        boolean isProgramName = currSectionName.equals("");
        currSectionName = line.substring(0, 6).replace(" ", "");
        int sectionSize = Integer.parseInt(line.substring(13), 16);

        currSectionStartAddress = virtualMachine.reserveMemory(sectionSize);
        linkingOperations.put(currSectionName, new ArrayList<>());

        symbolTable.put(currSectionName, currSectionStartAddress);

        if( isProgramName ) {
            virtualMachine.setProgramName(currSectionName);
        }
    }

    void handleExternalDefine(String line) {
        for( int i = 0; i < line.length(); i += 12 ) {
            String symbol = line.substring(i, i + 6).replace(" ", "");
            int addressDiff = Integer.parseInt(line.substring(i + 6, i + 12), 16);
            symbolTable.put(symbol, currSectionStartAddress + addressDiff);
        }
    }

    void handleExternalReference(String line) {

    }

    void handleText(String line) {
        int startAddress = Integer.parseInt(line.substring(0, 6), 16);
        String hexes = line.substring(8);
        byte[] bytes = new byte[hexes.length() / 2];
        for( int i = 0; i < hexes.length(); i += 2 ) {
            byte b = 0;
            char c1 = Util.hexToDigit(hexes.charAt(i));
            char c2 = Util.hexToDigit(hexes.charAt(i + 1));

            b += c1 << 4;
            b += c2;
            bytes[i / 2] = b;
        }

        virtualMachine.setMemory(currSectionStartAddress + startAddress, bytes, bytes.length);
    }

    void handleModification(String line) {
        int startAddress = Integer.parseInt(line.substring(0, 6), 16);
        int size = Integer.parseInt(line.substring(6, 6 + 2), 16);
        int operation = 0;
        if( line.charAt(8) == '+' ) {
            operation = 0;
        } else if( line.charAt(8) == '-' ){
            operation = 1;
        }
        String symbol = line.substring(9).replace(" ", "");

        ArrayList<LinkingOperation> list = linkingOperations.get(currSectionName);
        list.add(new LinkingOperation(currSectionStartAddress + startAddress, size, operation, symbol));
    }

    void handleEnd(String line) {

    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void setCodeSimulator(CodeSimulator codeSimulator) {
        this.codeSimulator = codeSimulator;
    }

    private void linking() {
        for(String symbol : new TreeSet<>(symbolTable.keySet())) {
            System.out.println(symbol + " " + symbolTable.get(symbol));
        }

        for( String key : linkingOperations.keySet() ) {
            for(LinkingOperation linkingOperation : linkingOperations.get(key)) {
                int symbol_address = symbolTable.get(linkingOperation.symbol);

                int size = linkingOperation.modifyingSize;
                if( size % 2 == 0 ) {
                    size = size / 2;
                } else {
                    size = size / 2 + 1;
                }

                byte[] adder = new byte[size];
                adder[0] = (byte) ((symbol_address & 0x00FF0000) >> 16);
                adder[1] = (byte) ((symbol_address & 0x0000FF00) >> 8);
                adder[2] = (byte) ((symbol_address & 0x000000FF));

                if( linkingOperation.operation == 1 ) {
                    for( int i = 0; i < adder.length; i ++ ) {
                        adder[i] *= -1;
                    }
                }

                byte[] origin = virtualMachine.getMemory(linkingOperation.addressStart, size);
                for( int i = 0; i < origin.length; i ++ ) {
                    origin[i] += adder[i];
                }
                virtualMachine.setMemory(linkingOperation.addressStart, origin, size);
            }
        }
    }


    class LinkingOperation {
        public int addressStart;
        public int modifyingSize;
        public int operation;
        public String symbol;

        public LinkingOperation(int start, int size, int operation, String symbol) {
            this.addressStart = start;
            this.modifyingSize = size;
            this.operation = operation;
            this.symbol = symbol;
        }
    }
}
