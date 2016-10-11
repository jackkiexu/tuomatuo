package com.lami.tuomatuo.utils;

import java.util.Random;

/**
 * Created by xjk on 2016/1/25.
 */
public class RandomUtils {

    public static void main(String[] args) {
        System.out.println(randomString(4,true));
    }

    private static Random randGen = null;
    private final static char[] numbersAndLetters = ("123456789ABCDEFGHJKLMNPQRSTUVWXYZ").toCharArray();
    private final static char[] numbers = ("0123456789").toCharArray();
    private static Object initLock = new Object();

    public static final String randomString(int length,boolean isNum) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                }
            }
        }
        char[] randBuffer = new char[length];
        if(isNum)
            for (int i = 0; i < randBuffer.length; i++)
                randBuffer[i] = numbers[randGen .nextInt(numbers.length)];
        else
            for (int i = 0; i < randBuffer.length; i++)
                randBuffer[i] = numbersAndLetters[randGen .nextInt(numbersAndLetters.length)];
        return new String(randBuffer);
    }
}
