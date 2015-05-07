package loki.cse.ssu;

/**
 * Created by loki on 15. 5. 7..
 */
public class Symbol {
    String _symbol;
    int _address;
    int _csectNum;

    public Symbol(String symbol, int address, int csectNum) {
        _symbol = symbol;
        _address = address;
        _csectNum = csectNum;
    }

    public String GetSymbol() {
        return _symbol;
    }

    public boolean IsSameSymbol(String symbol) {
        return _symbol.equals(symbol);
    }

    public int GetAddress() {
        return _address;
    }

    public int GetControlSectionNumber() {
        return _csectNum;
    }
}
