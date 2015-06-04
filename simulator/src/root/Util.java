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

    public static int twosComp(String str, int pow) {
        Integer num = Integer.valueOf(str, 16);
        int max = (int) Math.pow(2, pow);
        int mid = max / 2 - 1;
        return (num > mid) ? num - max : num;
    }
}
