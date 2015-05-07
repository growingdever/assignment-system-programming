package loki.cse.ssu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by loki on 15. 5. 7..
 */
public class InstructionTable {

    ArrayList<InstructionData> _instructions = new ArrayList<InstructionData>();


    public InstructionTable() {

    }

    public boolean LoadInstructionData(String instructionDataPath) {
        File file = new File(instructionDataPath);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();

            StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
            String mnemonic = stringTokenizer.nextToken();
            String format = stringTokenizer.nextToken();
            String opcode = stringTokenizer.nextToken();
            String numOfOperand = stringTokenizer.nextToken();

            _instructions.add(new InstructionData(mnemonic, format, opcode, numOfOperand));
        }

        return true;
    }

    public InstructionData GetInstructionData(int i) {
        return _instructions.get(i);
    }

    public InstructionData GetInstructionData(String mnemonic) {
        for( int i = 0; i < _instructions.size(); i ++ ) {
            if( _instructions.get(i).IsSameMnemonic(mnemonic) ) {
                return _instructions.get(i);
            }
        }

        return null;
    }
}
