package com.apache.catalina.tribes.util;

/**
 * Utilities for handling Throwables and Exception
 *
 * Created by xjk on 3/12/17.
 */
public class ExceptionUtils {

    /**
     * Checks whether the supplied Throwable is one that needs to be
     * rethrown and swallows all others
     * @param t the Throwable to check
     */
    public static void handleThrowable(Throwable t){
        if(t instanceof ThreadDeath){
            throw (ThreadDeath)t;
        }
        if(t instanceof StackOverflowError){
            // Swallow silently - it should be recoverable
            return;
        }

        if(t instanceof VirtualMachineError){
            throw (VirtualMachineError)t;
        }
        // All other instances of Throwable will be silently swallowed
    }

}
