package at.sepp.mods.villagers.api.util;

public class UnicodeEncoder {
    private static StringBuffer charAccumulator = new StringBuffer(2);

    private static final char MIN_HIGH_SURROGATE = ' ';

    private static final char MAX_HIGH_SURROGATE = ' ';

    private static final char MIN_LOW_SURROGATE = ' ';

    private static final char MAX_LOW_SURROGATE = ' ';

    private static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;

    public static String encode(String s) {
        StringBuffer sb = new StringBuffer();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] <= '') {
                sb.append(chars[i]);
            } else {
                int codePoint = Integer.MIN_VALUE;
                if (isLowSurrogate(chars[i]) || isHighSurrogate(chars[i])) {
                    charAccumulator.append(chars[i]);
                } else {
                    codePoint = chars[i];
                }
                if (charAccumulator.length() == 2) {
                    codePoint = toCodePoint(charAccumulator.charAt(0), charAccumulator.charAt(1));
                    charAccumulator.setLength(0);
                }
                if (charAccumulator.length() == 0)
                    sb.append(encode(codePoint));
            }
        }
        return sb.toString();
    }

    public static String encode(int c) {
        StringBuffer sb = new StringBuffer(10);
        sb.append(Integer.toHexString(c));
        while (sb.length() < 4)
            sb.insert(0, '0');
        sb.insert(0, "\\u");
        return sb.toString();
    }

    private static boolean isHighSurrogate(char ch) {
        return (ch >= ' ' && ch <= ' ' );
    }

    private static boolean isLowSurrogate(char ch) {
        return (ch >= ' '&& ch <= ' ');
    }

    private static int toCodePoint(char high, char low) {
        return (high - 55296 << 10) + low - 56320 + 65536;
    }
}
