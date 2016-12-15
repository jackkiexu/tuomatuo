package com.lami.tuomatuo.mq.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.jctools.queues.MpscChunkedArrayQueue;

import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * Utility that detects various properties specific to the current runtime
 * enviroment, such as Java verious and the availability of the
 * {@code sum.misc.Unsafe} object
 * <p>
 *     You can disable the use of {@code sum.misc.Unsafe} if you specify
 *     the system property <strong>io.netty.UNSAFE</strong>
 * </p>
 *
 * Created by xjk on 12/14/16.
 */
public class PlatformDependent {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);

    private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");

//    private static final boolean IS_ANDROID =


    private static final class AtomicLongCounter extends AtomicLong implements LongCounter{

        @Override
        public void add(long delta) {
            addAndGet(delta);
        }

        @Override
        public void increment() {
            incrementAndGet();
        }

        @Override
        public void decrement() {
            decrementAndGet();
        }

        @Override
        public long value() {
            return get();
        }
    }

    private PlatformDependent(){
        // only static method supported
    }
}
