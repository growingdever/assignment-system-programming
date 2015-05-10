package loki.cse.ssu;

import java.util.ArrayList;

/**
 * Created by loki on 15. 5. 7..
 */
public class SourceToken {
    String _label;
    String _operator;
    ArrayList<String> _operands;
    String _comment;
    boolean _generatedByLTORG;

    public SourceToken(String operator) {
        _operator = operator;

        _operands = new ArrayList<>();
        _generatedByLTORG = false;
    }

    public String GetLabel() {
        return _label;
    }
    public void SetLabel(String label) {
        _label = label;
    }

    public String GetOperator() {
        return _operator;
    }

    public ArrayList<String> GetOperands() {
        return _operands;
    }
    public void AddOperand(String operand) {
        _operands.add(operand);
    }

    public String GetComment() {
        return _comment;
    }
    public void SetComment(String comment) {
        _comment = comment;
    }

    public void SetGeneratedByLTORG(boolean b) {
        _generatedByLTORG = b;
    }
    public boolean IsGeneratedByLTORG() {
        return _generatedByLTORG;
    }
}
