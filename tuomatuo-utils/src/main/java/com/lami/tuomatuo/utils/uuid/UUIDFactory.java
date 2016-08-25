package com.lami.tuomatuo.utils.uuid;


import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UUIDFactory {

	private  static final UUIDGenerator datetimeGenerator = new TimeStampUUIDGenerator();
    private static final UUIDGenerator simpleUUIDGenerator = new ShortUUIDGenerator();

    public static String shortUUID(){
        return simpleUUIDGenerator.generate().toString();
    }

	public static String timeStampUUID(){
		return  datetimeGenerator.generate().toString();
	}

}
