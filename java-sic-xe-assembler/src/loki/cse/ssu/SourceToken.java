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

    public SourceToken(String operator) {
        _operator = operator;

        _operands = new ArrayList<>();
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
}
