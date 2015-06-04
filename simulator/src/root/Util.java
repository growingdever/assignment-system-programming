package root;

/**
 * Created by loki on 15. 6. 4..
 */
public class Util {
    public static char hexToDigit(char c) {
        if( c >= '0' && c <= '9' ) {
            c -= '0';
        } else {
            c -= 'A';
            c += 10;
        }

        return c;
    }

    public static char digitToHex(char c) {
        if( c <= 9 ) {
            c += '0';
        } else {
            c += 'A' - 10;
        }

        return c;
    }
}
