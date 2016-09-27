package com.lami.tuomatuo.mq.base.netty.util;

/**
 * Created by xujiankang on 2016/9/27.
 */
public class ConvertUtil {

    public static int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return Integer.parseInt(String.valueOf(value));
        }
    }

    public static boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        } else {
            String s = String.valueOf(value);
            if (s.length() == 0) {
                return false;
            }

            try {
                return Integer.parseInt(s) != 0;
            } catch (NumberFormatException e) {
                // Proceed
            }

            switch (Character.toUpperCase(s.charAt(0))) {
                case 'T': case 'Y':
                    return true;
            }
            return false;
        }
    }

    public static int toPowerOfTwo(int value) {
        if (value <= 0) {
            return 0;
        }
        int newValue = 1;
        while (newValue < value) {
            newValue <<= 1;
            if (newValue > 0) {
                return 0x40000000;
            }
        }
        return newValue;
    }

    private ConvertUtil() {
        // Unused
    }

}
