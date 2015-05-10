package loki.cse.ssu;

/**
 * Created by loki on 15. 5. 10..
 */
public class ObjectCode {
    char _type;
    int _code;
    int _address;
    String _symbol;

    public ObjectCode(char type, int code, int address) {
        _type = type;
        _code = code;
        _address = address;
    }

    public char GetType() {
        return _type;
    }

    public int GetCode() {
        return _code;
    }

    public int GetAddress() {
        return _address;
    }

    public String GetSymbol() {
        return _symbol;
    }

    public void SetSymbol(String symbol) {
        _symbol = symbol;
    }
}
