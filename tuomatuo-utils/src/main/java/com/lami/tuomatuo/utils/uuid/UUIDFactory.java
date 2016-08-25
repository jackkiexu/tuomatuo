package com.lami.tuomatuo.utils.uuid;


import java.security.SecureRandom;

public class UUIDFactory
{
    private static SecureRandom random = new SecureRandom();

	private  static final UUIDGenerator datetimeGenerator = new TimeStampUUIDGenerator();
    private static final UUIDGenerator simpleUUIDGenerator = new SimpleUUIDGenerator();

    public static String newShortUUID(){
        return simpleUUIDGenerator.generate().toString();
    }

	public static String newDatetimeID(){
		return  datetimeGenerator.generate().toString();
	}

    public static long randomLong() {
        long result;
        while(true){
            result = Math.abs(random.nextLong());
            if(result != Long.MIN_VALUE){
                return result;
            }
        }
    }

}
