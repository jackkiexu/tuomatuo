package com.lami.tuomatuo.utils.uuid;


import java.security.SecureRandom;

/**
 * A factory for creating ID objects.
 */
public class IDFactory
{
    private static SecureRandom random = new SecureRandom();

	/** The Constant datetimeGenerator. */
	private  static final IDGenerator datetimeGenerator = new DatetimeKeyGenerator();

    private static final IDGenerator simpleUUIDGenerator = new SimpleUUIDGenerator();

    /**
     * New 32-bit short uuid.
     *
     * @return the string
     */
    public static String newShortUUID(){
        return simpleUUIDGenerator.generate().toString();
    }
	
	/**
	 * New datetime id.
	 * 
	 * @return the string
	 */
	public static String newDatetimeID(){
		return  datetimeGenerator.generate().toString();
	}

    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        long result;
        while(true){
            result = Math.abs(random.nextLong());
            if(result != Long.MIN_VALUE){
                return result;
            }
        }
    }

    /**
     * 基于Base62编码的SecureRandom随机生成bytes.
     */
    public static String randomBase62(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Encodes.encodeBase62(randomBytes);
    }
}
