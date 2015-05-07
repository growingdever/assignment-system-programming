package loki.cse.ssu;

import javax.jnlp.IntegrationService;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by loki on 15. 5. 6..
 */
public class MyAssembler {

    private final String _instructionDataPath;
    private final String _inputSourcePath;

    InstructionTable _instructionTable;
    ArrayList<String> _inputSourceLines;
    ArrayList<SourceToken> _tokens;



    public MyAssembler(String instructionDataPath, String inputSourcePath) {
        _instructionDataPath = instructionDataPath;
        _inputSourcePath = inputSourcePath;

        _instructionTable = new InstructionTable();
        _inputSourceLines = new ArrayList<>();
        _tokens = new ArrayList<>();
    }

    public static void Start() {
        MyAssembler myAssembler = new MyAssembler("inst.data", "program_in.txt");
        myAssembler.Run();
    }

    void Run() {
        if( ! Initialize() ) {
            System.err.println("ERROR! - Initialize");
        }
    }

    boolean Initialize() {
        if( ! _instructionTable.LoadInstructionData(_instructionDataPath) ) {
            System.err.println("error! - LoadInstructionData");
            return false;
        }

        if( ! LoadInputSourceLines(_inputSourcePath) ) {
            System.err.println("error! - LoadInputSourceLines");
            return false;
        }

        if( ! Pass1() ) {
            System.err.println("error! - Pass1");
            return false;
        }

        if( ! Pass2() ) {
            System.err.println("error! - Pass2");
            return false;
        }

        return true;
    }

    boolean LoadInputSourceLines(String inputSourcePath) {
        File file = new File(inputSourcePath);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            _inputSourceLines.add(line);
        }

        return true;
    }

    private boolean Pass1() {
        for( int i = 0; i < _inputSourceLines.size(); i ++ ) {
            String line = _inputSourceLines.get(i);
            if( ! ParsingToken(line) ) {
                return false;
            }
        }

        for(SourceToken token : _tokens) {
            System.out.println( token.GetOperator() );
        }

        return true;
    }

    private boolean ParsingToken(String line) {
        // 완전히 comment 라인
        if( line.charAt(0) == '.' ) {
            // token unit에 추가하지 않고 종료
            return true;
        }

        String label = null, operator = null, operandLine = null, comment = null;

        StringTokenizer stringTokenizer = new StringTokenizer(line);

        // label 존재하는 경우
        if( line.charAt(0) != ' ' ) {
            label = stringTokenizer.nextToken();
        }

        operator = stringTokenizer.nextToken();

        if( stringTokenizer.hasMoreTokens() ) {
            operandLine = stringTokenizer.nextToken();
        }

        if( stringTokenizer.hasMoreTokens() ) {
            comment = stringTokenizer.nextToken();
        }

        SourceToken token = new SourceToken(operator);
        token.SetLabel(label);
        token.SetComment(comment);

        // operand tokenizing
        if( operandLine != null ) {
            StringTokenizer operandTokenizer = new StringTokenizer(operandLine, ",");
            while(operandTokenizer.hasMoreTokens()) {
                token.AddOperand( operandTokenizer.nextToken() );
            }
        }

        _tokens.add(token);

        return true;
    }

    private boolean Pass2() {
        return true;
    }

}
