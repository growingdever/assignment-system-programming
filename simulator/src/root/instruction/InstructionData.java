package root.instruction;

/**
 * Created by loki on 15. 5. 7..
 */
public class InstructionData {
    String _mnemonic;
    String _format;
    String _opcode;
    String _numOfOperand;

    public InstructionData(String mnemonic, String format, String opcode, String numOfOperand) {
        _mnemonic = mnemonic;
        _format = format;
        _opcode = opcode;
        _numOfOperand = numOfOperand;
    }

    public String GetMnemonic() {
        return _mnemonic;
    }

    public boolean IsSameMnemonic(String mnemonic) {
        return _mnemonic.equals(mnemonic);
    }

    public boolean IsValidFormat(int format) {
        if (_format.length() == 1) {
            return Integer.parseInt(_format) == format;
        }

        return Integer.parseInt(_format.valueOf(_format.charAt(0))) == format
                || Integer.parseInt(_format.valueOf(_format.charAt(2))) == format;
    }

    public int GetOpCode() {
        return Integer.parseInt( _opcode.substring(2), 16 );
    }

    public int GetNumOfOperand() {
        return Integer.parseInt(_numOfOperand);
    }
}
