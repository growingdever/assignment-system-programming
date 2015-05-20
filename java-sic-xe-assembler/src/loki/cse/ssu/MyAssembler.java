package loki.cse.ssu;

import com.sun.tools.internal.jxc.ap.Const;
import com.sun.tools.javac.code.Attribute;
import javafx.util.Pair;

import javax.xml.transform.Source;
import java.io.*;
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

        if( ! Pass1() ) {
            System.err.println("error! - Pass1");
            return;
        }

        if( ! Pass2() ) {
            System.err.println("error! - Pass2");
            return;
        }

        if( ! PrintObjectCodes("output") ) {
            System.err.println("error! - PrintObjectCoeds");
            return;
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

            String line = String.format("%7s %7s", token.GetLabel(), token.GetOperator());
            if( token.GetOperands().size() > 0 ) {
                line += "  ";
                for(String operand : token.GetOperands()) {
                    line += operand + ", ";
                }

                line = line.substring(0, line.length() - 2);
            }

            System.out.println(line);
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
        ArrayList<Pair<Integer, Boolean>> generateTargetPosition = new ArrayList<>(); // start from 1 because START operator

        int csectNum = 0;
        for( int i = 0; i < _tokens.size(); i ++ ) {
            SourceToken token = _tokens.get(i);

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_START_STRING)
                    || token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_CSECT_STRING) ) {
                csectNum++;
                generateTargetPosition.add(new Pair<>(i, false));
                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_LTORG_STRING) ) {
                generateTargetPosition.add(new Pair<>(i, true));
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

        generateTargetPosition.add(new Pair<>(_tokens.size() - 1, false));

        int generated = 0;
        int last = 0;
        for(Pair<Integer, Boolean> targetIndex : generateTargetPosition) {
            ArrayList<SourceToken> newTokens = new ArrayList<>();
            for( int i = last; i < literals.size(); i ++ ) {
                Pair<String, Integer> pair = literals.get(i);
                if( pair.getValue() < targetIndex.getKey() ) {
                    String literal = pair.getKey();
                    SourceToken newToken;
                    if( literal.charAt(1) == 'X' || literal.charAt(1) == 'C' ) {
                        newToken = new SourceToken(Constants.ASSEMBLY_DIRECTIVE_BYTE_STRING);
                    } else {
                        newToken = new SourceToken(Constants.ASSEMBLY_DIRECTIVE_WORD_STRING);
                    }

                    newToken.SetLabel(literal);
                    newToken.SetGeneratedByLTORG(targetIndex.getValue());
                    newToken.AddOperand(literal.substring(1));
                    newTokens.add(newToken);

                    last = i + 1;
                    generated++;
                }
            }

            _tokens.addAll(targetIndex.getKey() + generated - newTokens.size(), newTokens);
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
                if( csectNum >= 1 ) {
                    _objectCodes.get(csectNum).add(new ObjectCode(Constants.RECORD_PREFIX_END, 0, locationCounter));
                }

                csectNum++;

                if( token.GetOperands().size() > 0 ) {
                    locationCounter = Integer.parseInt( token.GetOperands().get(0) );
                } else {
                    locationCounter = 0;
                }

                _objectCodes.put(csectNum, new ArrayList<>());
                ObjectCode objectCode = new ObjectCode(Constants.RECORD_PREFIX_HEADER, 0, locationCounter);
                objectCode.SetSymbol(token.GetLabel());
                _objectCodes.get(csectNum).add(objectCode);
                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_EXTDEF_STRING) ) {
                for(String symbol : token.GetOperands()) {
                    ObjectCode objectCode = new ObjectCode(Constants.RECORD_PREFIX_EXTDEF, 0, GetAddressOfSymbol(symbol, csectNum));
                    objectCode.SetSymbol(symbol);
                    _objectCodes.get(csectNum).add( objectCode );
                }

                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_EXTREF_STRING) ) {
                for(String symbol : token.GetOperands()) {
                    ObjectCode objectCode = new ObjectCode(Constants.RECORD_PREFIX_EXTREF, 0, 0);
                    objectCode.SetSymbol(symbol);
                    _objectCodes.get(csectNum).add( objectCode );
                }

                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_END_STRING) ) {
                _objectCodes.get(csectNum).add(new ObjectCode(Constants.RECORD_PREFIX_END, 0, locationCounter));
                continue;
            }

            Pair<Integer, Integer> objectCode = CalculateObjectCode(token, locationCounter, csectNum);
            if( objectCode.getValue() == -1 ) {
                String formatted = String.format("%8s %8s", token.GetLabel(), token.GetOperator());
                System.out.println(formatted);
            } else {
                String formatted = String.format("%8s %8s %08X", token.GetLabel(), token.GetOperator(), objectCode.getValue());
                System.out.println(formatted);

                char type = Constants.RECORD_PREFIX_TEXT;
                if( token.IsGeneratedByLTORG() && token.GetLabel() != null && token.GetLabel().charAt(0) == '=' ) {
                    type = Constants.RECORD_PREFIX_LITERAL;
                }

                ObjectCode unit = new ObjectCode(type, objectCode.getValue(), locationCounter);
                unit.SetFormat( objectCode.getKey() );
                _objectCodes.get(csectNum).add(unit);
            }

            int locationCounterIncrease = IncreaseLocationCounterByToken(token);
            locationCounter += locationCounterIncrease;

            //
            // generate modification row
            //
            if( token.GetOperands().size() > 0 && token.GetOperands().get(0).equals("*") ) {
                continue;
            }

            if( token.GetOperator().equals(Constants.ASSEMBLY_DIRECTIVE_BYTE_STRING) ) {
                continue;
            }

            for(String operand : token.GetOperands()) {
                String onlySymbol = operand;
                if( operand.charAt(0) == '@'
                        || operand.charAt(0) == '#'
                        || operand.charAt(0) == '+'
                        || operand.charAt(0) == '-' ) {
                    onlySymbol = operand.substring(1);
                }

                if( TransformableToInteger(onlySymbol) ) {
                    continue;
                }

                if( GetAddressOfRegister(onlySymbol) == -1
                        && GetAddressOfSymbol(onlySymbol, csectNum) == Constants.ADDRESS_EXTREF ) {
                    int offset = locationCounterIncrease;
                    if( token.GetOperator().charAt(0) == '+' ) {
                        offset -= 1;
                    }

                    ObjectCode objectCodeUnit = new ObjectCode(Constants.RECORD_PREFIX_MODIFICATION,
                            objectCode.getValue(),
                            locationCounter - offset);
                    objectCodeUnit.SetSymbol(operand);
                    objectCodeUnit.SetFormat( objectCode.getKey() );
                    _objectCodes.get(csectNum).add(objectCodeUnit);
                }
            }
        }

        return true;
    }

    private Pair<Integer, Integer> CalculateObjectCode(SourceToken token, int locationCounter, int controlSectionNumber) {
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
            return new Pair<>(-1, -1);
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

            return new Pair<>(4, code);
        } else {
            if( instructionData.IsValidFormat(2) ) {
                // format 2
                code += instructionData.GetOpCode() << 8;

                code += GetAddressOfRegister( token.GetOperands().get(0) ) << 4;
                if( token.GetOperands().size() > 1 ) {
                    code += GetAddressOfRegister( token.GetOperands().get(1) );
                }

                return new Pair<>(2, code);
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

                return new Pair<>(Constants.SIZE_OF_WORD, code);
            }
        }
    }

    private Pair<Integer, Integer> CalculateObjectCodeBYTE(SourceToken token) {
        String operand = token.GetOperands().get(0);
        int code = 0;
        int left = 0, right = 0;
        int length = 0;

        if( operand.charAt(0) == 'C' ) {
            left = 2;
            right = operand.length() - 2;
            length = right - left + 1;
            for( int i = left; i <= right; i ++ ) {
                char c = operand.charAt(i);
                code += c << (8 * Math.abs(i - right));
            }
        } else if( operand.charAt(0) == 'X' ) {
            left = 2;
            right = 3;
            length = 1;
            for( int i = left; i <= right; i ++ ) {
                char c = operand.charAt(i);
                int tmp = c > '9' ? c - 'A' + 10 : c - '0';
                code += tmp << (4 * Math.abs(i - right));
            }
        }

        return new Pair<>(length, code);
    }

    private Pair<Integer, Integer> CalculateObjectCodeWORD(SourceToken token, int controlSectionNumber) {
        if( token.GetOperands().size() == 1 ) {
            String operand = token.GetOperands().get(0);
            int address = GetAddressOfSymbol(operand, controlSectionNumber);
            if( address != 0 ) {
                return new Pair<>(Constants.SIZE_OF_WORD,
                        Integer.parseInt( token.GetOperands().get(0) ));
            }
        }

        int value = 0;
        for(String operand : token.GetOperands()) {
            String onlyOperand = operand;
            if( operand.charAt(0) == '+' || operand.charAt(0) == '-' ) {
                onlyOperand = operand.substring(1);
            }

            if( TransformableToInteger(operand) ) {
                if( operand.charAt(0) == '+' ) {
                    value += Integer.parseInt(onlyOperand);
                } else {
                    value -= Integer.parseInt(onlyOperand);
                }
            }
        }

        return new Pair<>(Constants.SIZE_OF_WORD, value);
    }

    private boolean TransformableToInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private boolean PrintObjectCodes(String path) {
        PrintStream outPrintStream;
        try {
            File outputFile = new File(path);
            outPrintStream = new PrintStream(new FileOutputStream(outputFile));
            System.setOut(outPrintStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            return false;
        }


        for(Integer controlSectionNumber : _objectCodes.keySet()) {
            ArrayList<ObjectCode> objectCodes = _objectCodes.get(controlSectionNumber);

            ArrayList<ObjectCode> header = GetSpecificObjectCodeList(objectCodes, Constants.RECORD_PREFIX_HEADER);
            ArrayList<ObjectCode> externalDefines = GetSpecificObjectCodeList(objectCodes, Constants.RECORD_PREFIX_EXTDEF);
            ArrayList<ObjectCode> externalReferences = GetSpecificObjectCodeList(objectCodes, Constants.RECORD_PREFIX_EXTREF);
            ArrayList<ObjectCode> texts = GetSpecificObjectCodeList(objectCodes, Constants.RECORD_PREFIX_TEXT);
            ArrayList<ObjectCode> literals = GetSpecificObjectCodeList(objectCodes, Constants.RECORD_PREFIX_LITERAL);
            ArrayList<ObjectCode> modifications = GetSpecificObjectCodeList(objectCodes, Constants.RECORD_PREFIX_MODIFICATION);
            ArrayList<ObjectCode> end = GetSpecificObjectCodeList(objectCodes, Constants.RECORD_PREFIX_END);

            System.out.println( String.format("H%-6s%06X%06X",
                    header.get(0).GetSymbol(),
                    header.get(0).GetAddress(),
                    end.get(0).GetAddress() - header.get(0).GetAddress()) );

            if( externalDefines.size() > 0 ) {
                System.out.print(Constants.RECORD_PREFIX_EXTDEF);
                for(ObjectCode objectCode : externalDefines) {
                    System.out.print( String.format("%6s%06X", objectCode.GetSymbol(), objectCode.GetAddress()) );
                }
                System.out.println();
            }

            if( externalReferences.size() > 0 ) {
                System.out.print(Constants.RECORD_PREFIX_EXTREF);
                for(ObjectCode objectCode : externalReferences) {
                    System.out.print( String.format("%-6s", objectCode.GetSymbol()) );
                }
                System.out.println();
            }


            int length, startAddress;
            String textRecord;
            //
            // TEXT
            //
            length = 0;
            textRecord = "";
            startAddress = texts.get(0).GetAddress();
            for( int i = 0; i < texts.size(); i ++ ) {
                ObjectCode objectCode = texts.get(i);
                switch( objectCode.GetFormat() ) {
                    case 1:
                        length += 1 * 2;
                        textRecord += String.format("%02X", objectCode.GetCode());
                        break;
                    case 2:
                        length += 2 * 2;
                        textRecord += String.format("%04X", objectCode.GetCode());
                        break;
                    case 3:
                        length += 3 * 2;
                        textRecord += String.format("%06X", objectCode.GetCode());
                        break;
                    case 4:
                        length += 4 * 2;
                        textRecord += String.format("%08X", objectCode.GetCode());
                        break;
                }

                if( i < texts.size() - 1
                        && length + texts.get(i + 1).GetFormat() * 2 >= Constants.TEXT_RECORD_MAX ) {
                    String formatted = String.format("%c%06X%02X%s",
                            Constants.RECORD_PREFIX_TEXT,
                            startAddress,
                            textRecord.length() / 2,
                            textRecord);
                    System.out.println( formatted );

                    textRecord = "";
                    length = 0;

                    startAddress = texts.get(i + 1).GetAddress();
                }
            }
            if( textRecord.length() > 0 ) {
                String formatted = String.format("%c%06X%02X%s",
                        Constants.RECORD_PREFIX_TEXT,
                        startAddress,
                        textRecord.length() / 2,
                        textRecord);
                System.out.println( formatted );
            }


            //
            // LITERAL
            //
            if( literals.size() > 0 ) {
                length = 0;
                textRecord = "";
                startAddress = literals.get(0).GetAddress();
                for( int i = 0; i < literals.size(); i ++ ) {
                    ObjectCode objectCode = literals.get(i);
                    switch( objectCode.GetFormat() ) {
                        case 1:
                            length += 1 * 2;
                            textRecord += String.format("%02X", objectCode.GetCode());
                            break;
                        case 2:
                            length += 2 * 2;
                            textRecord += String.format("%04X", objectCode.GetCode());
                            break;
                        case 3:
                            length += 3 * 2;
                            textRecord += String.format("%06X", objectCode.GetCode());
                            break;
                    }

                    if( i < literals.size() - 1
                            && length + literals.get(i + 1).GetFormat() * 2 >= Constants.TEXT_RECORD_MAX ) {
                        String formatted = String.format("%c%06X%02X%s",
                                Constants.RECORD_PREFIX_TEXT,
                                startAddress,
                                textRecord.length() / 2,
                                textRecord);
                        System.out.println( formatted );

                        textRecord = "";
                        length = 0;

                        startAddress = literals.get(i + 1).GetAddress();
                    }
                }
                if( textRecord.length() > 0 ) {
                    String formatted = String.format("%c%06X%02X%s",
                            Constants.RECORD_PREFIX_TEXT,
                            startAddress,
                            textRecord.length() / 2,
                            textRecord);
                    System.out.println( formatted );
                }
            }


            //
            // MODIFICATION
            //
            for( ObjectCode objectCode : modifications ) {
                int offset = objectCode.GetFormat() == 4 ? 5 : 6;
                String symbol = objectCode.GetSymbol();
                if( symbol.charAt(0) != '-' && symbol.charAt(0) != '+' ) {
                    symbol = "+" + symbol;
                }
                System.out.println( String.format("M%06X%02X%-7s", objectCode.GetAddress(), offset, symbol) );
            }


            if( controlSectionNumber == 1 ) {
                System.out.println( String.format( Constants.RECORD_PREFIX_END + "%06X\n", 0) );
            } else {
                System.out.println( String.format( Constants.RECORD_PREFIX_END + "\n") );
            }
        }

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return true;
    }

    ArrayList<ObjectCode> GetSpecificObjectCodeList(ArrayList<ObjectCode> objectCodes, char type) {
        ArrayList<ObjectCode> ret = new ArrayList<>();
        for(ObjectCode objectCode : objectCodes) {
            if( objectCode.GetType() == type ) {
                ret.add(objectCode);
            }
        }

        return ret;
    }

}
