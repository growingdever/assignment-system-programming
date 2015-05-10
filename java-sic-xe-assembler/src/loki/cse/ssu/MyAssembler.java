package loki.cse.ssu;

import javafx.util.Pair;

import javax.xml.transform.Source;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
    ArrayList<Symbol> _symbols;
    HashMap<Integer, ArrayList<ObjectCode>> _objectCodes;



    public MyAssembler(String instructionDataPath, String inputSourcePath) {
        _instructionDataPath = instructionDataPath;
        _inputSourcePath = inputSourcePath;

        _instructionTable = new InstructionTable();
        _inputSourceLines = new ArrayList<>();
        _tokens = new ArrayList<>();
        _symbols = new ArrayList<>();
        _objectCodes = new HashMap<>();
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
        if( ! ParsingAllTokens() ) {
            return false;
        }

        if( ! GenerateLiterals() ) {
            return false;
        }

        if( ! AddAllSymbols() ) {
            return false;
        }

        for(SourceToken token : _tokens) {
            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_CSECT_STRING) ) {
                System.out.println();
            }

            if( token.GetOperands().size() == 0 ) {
                System.out.println( token.GetLabel() + " " + token.GetOperator() );
            } else {
                System.out.println( token.GetLabel() + " " + token.GetOperator() + " " + token.GetOperands().get(0) );
            }
        }

        System.out.println();

        System.out.println("Symbols:");
        for(Symbol symbol : _symbols) {
            String formatted = String.format("%8s %2d %08X",
                    symbol.GetSymbol(),
                    symbol.GetControlSectionNumber(),
                    symbol.GetAddress());
            System.out.println(formatted);
        }

        return true;
    }

    private boolean ParsingAllTokens() {
        for( int i = 0; i < _inputSourceLines.size(); i ++ ) {
            String line = _inputSourceLines.get(i);
            if( ! ParsingToken(line) ) {
                return false;
            }
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
            StringTokenizer operandTokenizer = new StringTokenizer(operandLine, "[,\\+\\-]");
            while(operandTokenizer.hasMoreTokens()) {
                String str = operandTokenizer.nextToken();
                int i = operandLine.indexOf(str);
                if( i > 0 ) {
                    char c = operandLine.charAt(i - 1);
                    if( c == '-' ) {
                        str = "-" + str;
                    }
                }
                token.AddOperand( str );
            }
        }

        _tokens.add(token);

        return true;
    }

    private boolean GenerateLiterals() {
        ArrayList<Pair<String, Integer>> literals = new ArrayList<>();
        ArrayList<Integer> generateTargetPosition = new ArrayList<>(); // start from 1 because START operator

        int csectNum = 0;
        for( int i = 0; i < _tokens.size(); i ++ ) {
            SourceToken token = _tokens.get(i);

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_START_STRING)
                    || token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_CSECT_STRING) ) {
                csectNum++;
                generateTargetPosition.add(i);
                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_LTORG_STRING) ) {
                generateTargetPosition.add(i);
                _tokens.remove(i);
                continue;
            }

            ArrayList<String> operands = token.GetOperands();
            if( operands.size() == 0 ) {
                continue;
            }

            String operand1 = operands.get(0);
            if( operand1.charAt(0) == '=' ) {
                boolean isExist = false;
                for( int j = 0; j < literals.size(); j ++ ) {
                    if( literals.get(j).getKey().equals(operand1) ) {
                        isExist = true;
                        break;
                    }
                }

                if( ! isExist ) {
                    literals.add(new Pair<>(operand1, i) );
                }
            }
        }

        generateTargetPosition.add(_tokens.size());

        int generated = 0;
        int last = 0;
        for(Integer targetIndex : generateTargetPosition) {
            ArrayList<SourceToken> newTokens = new ArrayList<>();
            for( int i = last; i < literals.size(); i ++ ) {
                Pair<String, Integer> pair = literals.get(i);
                if( pair.getValue() < targetIndex ) {
                    String literal = pair.getKey();
                    SourceToken newToken;
                    if( literal.charAt(1) == 'X' || literal.charAt(1) == 'C' ) {
                        newToken = new SourceToken(Constants.ASSEMBLY_DIRECTIVE_BYTE_STRING);
                    } else {
                        newToken = new SourceToken(Constants.ASSEMBLY_DIRECTIVE_WORD_STRING);
                    }

                    newToken.SetLabel(literal);
                    newToken.AddOperand(literal.substring(1));
                    newTokens.add(newToken);

                    last = i + 1;
                    generated++;
                }
            }

            _tokens.addAll(targetIndex + generated - newTokens.size(), newTokens);
        }

        return true;
    }

    private boolean AddAllSymbols() {
        int csectNum = 0;
        int locationCounter = 0;

        for( int i = 0; i < _tokens.size(); i ++ ) {
            SourceToken token = _tokens.get(i);
            String operator = token.GetOperator();

            if( operator.equals(Constants.ASSEMBLY_DIRECTIVE_START_STRING)
                    || operator.equals(Constants.ASSEMBLY_DIRECTIVE_CSECT_STRING) ) {
                csectNum++;
                if( token.GetOperands().size() > 0 ) {
                    locationCounter = Integer.parseInt( token.GetOperands().get(0) );
                } else {
                    locationCounter = 0;
                }
            }

            if( operator.equals(Constants.ASSEMBLY_DIRECTIVE_EXTREF_STRING) ) {
                ArrayList<String> operands = token.GetOperands();
                for(String operand : operands) {
                    AddSymbol(operand, csectNum, Constants.ADDRESS_EXTREF);
                }
                continue;
            }

            if( token.GetLabel() != null ) {
                AddSymbol(token.GetLabel(), csectNum, locationCounter);
            }

            locationCounter += IncreaseLocationCounterByToken(token);
        }

        return true;
    }

    private int IncreaseLocationCounterByToken(SourceToken token) {
        if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_RESB_STRING) ) {
            return Integer.parseInt( token.GetOperands().get(0) );
        } else if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_RESW_STRING) ) {
            return Integer.parseInt( token.GetOperands().get(0) ) * Constants.SIZE_OF_WORD;
        } else if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_BYTE_STRING) ) {
            String operand = token.GetOperands().get(0);
            if( operand.charAt(0) == 'C' ) {
                return operand.length() - 3;
            } else {
                return (operand.length() - 3) / 2;
            }
        } else if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_WORD_STRING) ) {
            return 3;
        }

        if( token.GetOperator().charAt(0) == '+' ) {
            return 4;
        }

        InstructionData data = _instructionTable.GetInstructionData(token.GetOperator());
        if( data == null ) {
            return 0;
        }

        for( int i = 1; i <= 3; i ++ ) {
            if( data.IsValidFormat(i) ) {
                return i;
            }
        }

        return 0;
    }

    private void AddSymbol(String strSymbol, int csectNum, int address) {
        if( GetAddressOfRegister(strSymbol) != -1 ) {
            return;
        }

        if( strSymbol.charAt(0) == '#' || strSymbol.charAt(0) == '*' ) {
            return;
        }

        for( int i = 0; i < _symbols.size(); i ++ ) {
            Symbol symbol = _symbols.get(i);
            if( symbol.IsSameSymbol(strSymbol, csectNum) ) {
                return;
            }
        }

        _symbols.add( new Symbol(strSymbol, address, csectNum) );
    }

    int GetAddressOfRegister(String str) {
        if( str.equals("A") ) {
            return 0;
        } else if( str.equals("X") ) {
            return 1;
        } else if( str.equals("L") ) {
            return 2;
        } else if( str.equals("PC") ) {
            return 8;
        } else if( str.equals("SW") ) {
            return 9;
        } else if( str.equals("B") ) {
            return 3;
        } else if( str.equals("S") ) {
            return 4;
        } else if( str.equals("T") ) {
            return 5;
        } else if( str.equals("F") ) {
            return 6;
        }

        return -1;
    }

    int GetAddressOfSymbol(String strSymbol, int controlSectionNumber) {
        for(Symbol symbol : _symbols) {
            if( symbol.IsSameSymbol(strSymbol, controlSectionNumber) ) {
                return symbol.GetAddress();
            }
        }

        return -1;
    }


    private boolean Pass2() {
        int csectNum = 0;
        int locationCounter = 0;

        System.out.println();
        System.out.println();

        for(SourceToken token : _tokens) {
            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_START_STRING)
                    || token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_CSECT_STRING) ) {
                csectNum++;

                if( token.GetOperands().size() > 0 ) {
                    locationCounter = Integer.parseInt( token.GetOperands().get(0) );
                } else {
                    locationCounter = 0;
                }

                _objectCodes.put(csectNum, new ArrayList<>());
                ObjectCode objectCode = new ObjectCode('H', 0, locationCounter);
                objectCode.SetSymbol(token.GetLabel());
                _objectCodes.get(csectNum).add(objectCode);
                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_EXTDEF_STRING) ) {
                for(String symbol : token.GetOperands()) {
                    ObjectCode objectCode = new ObjectCode('D', 0, 0);
                    objectCode.SetSymbol(symbol);
                    _objectCodes.get(csectNum).add( objectCode );
                }
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_EXTREF_STRING) ) {
                for(String symbol : token.GetOperands()) {
                    ObjectCode objectCode = new ObjectCode('R', 0, 0);
                    objectCode.SetSymbol(symbol);
                    _objectCodes.get(csectNum).add( objectCode );
                }

                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_END_STRING) ) {
                _objectCodes.get(csectNum).add(new ObjectCode('E', 0, locationCounter));
                continue;
            }

            int objectCode = CalculateObjectCode(token, locationCounter, csectNum);
            if( objectCode == -1 ) {
                String formatted = String.format("%8s %8s", token.GetLabel(), token.GetOperator());
                System.out.println(formatted);
            } else {
                String formatted = String.format("%8s %8s %08X", token.GetLabel(), token.GetOperator(), objectCode);
                System.out.println(formatted);

                _objectCodes.get(csectNum).add(new ObjectCode('T', objectCode, locationCounter));
            }

            //
            // generate modification row
            //
            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_BYTE_STRING)
                    || token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_WORD_STRING) ) {
                continue;
            }

            if( token.GetOperands().size() > 0 && token.GetOperands().get(0).equals("*") ) {
                continue;
            }

            for(String operand : token.GetOperands()) {
                if( operand.charAt(0) == '@'
                        || operand.charAt(0) == '#'
                        || operand.charAt(0) == '+'
                        || operand.charAt(0) == '-' ) {
                    operand = operand.substring(1);
                }

                if( TransformableToInteger(operand) ) {
                    continue;
                }

                if( GetAddressOfRegister(operand) == -1
                        && GetAddressOfSymbol(operand, csectNum) == Constants.ADDRESS_EXTREF ) {
                    System.out.println("M : " + operand);

                    ObjectCode objectCodeUnit = new ObjectCode('M', objectCode, locationCounter);
                    objectCodeUnit.SetSymbol(operand);
                    _objectCodes.get(csectNum).add(objectCodeUnit);
                }
            }

            locationCounter += IncreaseLocationCounterByToken(token);
        }


        System.out.println();
        System.out.println();

        for(Integer controlSectionNumber : _objectCodes.keySet()) {
            for(ObjectCode objectCode : _objectCodes.get(controlSectionNumber)) {
                System.out.println( String.format("%c %08X %08X %8s", objectCode.GetType(), objectCode.GetAddress(), objectCode.GetCode(), objectCode.GetSymbol()) );
            }
        }

        return true;
    }

    private int CalculateObjectCode(SourceToken token, int locationCounter, int controlSectionNumber) {
        if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_BYTE_STRING) ) {
            return CalculateObjectCodeBYTE(token);
        } else if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_WORD_STRING) ) {
            return CalculateObjectCodeWORD(token, controlSectionNumber);
        }

        int n = 1, i = 1, x = 0, b = 0, p = 0, e = 0, disp = 0;
        int code = 0;

        String operator = token.GetOperator();

        InstructionData instructionData = _instructionTable.GetInstructionData(token.GetOperator());
        if( instructionData == null ) {
            return -1;
        }

        if( operator.charAt(0) == '+' ) {
            // format 4

            if( token.GetOperands().size() >= 2 && token.GetOperands().get(1).charAt(0) == 'X' ) {
                x = 1;
            }

            n = 1;
            i = 1;
            e = 1;

            code += (instructionData.GetOpCode() + n * 2 + i) << 24;
            code += x << 23;
            code += e << 20;
            return code;
        } else {
            if( instructionData.IsValidFormat(2) ) {
                // format 2
                code += instructionData.GetOpCode() << 8;

                code += GetAddressOfRegister( token.GetOperands().get(0) ) << 4;
                if( token.GetOperands().size() > 1 ) {
                    code += GetAddressOfRegister( token.GetOperands().get(1) );
                }

                return code;
            } else {
                // format 3
                locationCounter += Constants.SIZE_OF_FORMAT3;

                p = 1;

                if( token.GetOperands().size() == 0 ) {
                    n = 1;
                    i = 1;
                    x = b = p = e = 0;
                } else {
                    String operand = token.GetOperands().get(0);
                    if( operand.charAt(0) == '#' ) {
                        n = 0;
                        i = 1;
                        p = 0;
                        disp = Integer.parseInt( operand.substring(1) );
                    } else if( operand.charAt(0) == '@' ) {
                        n = 1;
                        i = 0;
                        disp = GetAddressOfSymbol(operand.substring(1), controlSectionNumber) - locationCounter;

                    } else {
                        n = 1;
                        i = 1;
                        disp = GetAddressOfSymbol(operand, controlSectionNumber) - locationCounter;
                    }
                }

                if( token.GetOperands().size() >= 2 && token.GetOperands().get(1).charAt(0) == 'X' ) {
                    x = 1;
                }

                code += (instructionData.GetOpCode() + n * 2 + i) << 16;
                code += x << 15;
                code += b << 14;
                code += p << 13;
                code += e << 12;
                code += (0x00000FFF & disp);

                return code;
            }
        }
    }

    private int CalculateObjectCodeBYTE(SourceToken token) {
        String operand = token.GetOperands().get(0);
        int code = 0;

        if( operand.charAt(0) == 'C' ) {
            int left = 2;
            int right = operand.length() - 2;
            for( int i = left; i <= right; i ++ ) {
                char c = operand.charAt(i);
                code += c << (8 * Math.abs(i - right));
            }
        } else if( operand.charAt(0) == 'X' ) {
            int left = 2;
            int right = 3;
            for( int i = left; i <= right; i ++ ) {
                char c = operand.charAt(i);
                int tmp = c > '9' ? c - 'A' + 10 : c - '0';
                code += tmp << (4 * Math.abs(i - right));
            }
        }

        return code;
    }

    private int CalculateObjectCodeWORD(SourceToken token, int controlSectionNumber) {
        if( token.GetOperands().size() == 1 ) {
            String operand = token.GetOperands().get(0);
            int address = GetAddressOfSymbol(operand, controlSectionNumber);
            if( address != 0 ) {
                return Integer.parseInt( token.GetOperands().get(0) );
            }
        }

        return 0;
    }

    private boolean TransformableToInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

}
