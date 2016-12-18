package com.lami.tuomatuo.mq.netty.util.internal.logging;

/**
 * <em>Internal-use-only</em> logger used by Netty <strong>DO NOT</strong>
 * access this class outside of netty
 *
 * Created by xjk on 12/15/16.
 */
public interface InternalLogger {

    /**
     * Return the name of this {@link InternalLogger} instance
     *
     * @return name of this logger instance
     */

    String name();

    /**
     * Is the logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for the TRACE level,
     *          false otherwise
     */
    boolean isTraceEnabled();

    /**
     * Log a message at the TRACE level
     *
     * @param msg the message string to be logged
     */
    void trace(String msg);

    /**
     * Log a message at the TRACE level according to the specified format
     * and argument
     * <p>
     *     This form avoids superfluous object creation when the logger
     *     is disabled for the TRACE level.
     * </p>
     * @param format the format string
     * @param arg the argument
     */
    void trace(String format, Object arg);

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments
     * <p>
     *     This form avoids superfluous object creation when logger
     *     is disabled for the TRACE level
     * </p>
     * @param format the format string
     * @param argA   the first argument
     * @param argB   the second argument
     */
    void trace(String format, Object argA, Object argB);

    /**
     * Log a message at the TRACE level accroding to the specified format
     * and argument
     * <p>
     *     This form avoids superfluous string concatenation when the logger
     *     is disabled for the TRACE level. However, this variant incurs the hidden
     *     (and relatively small) cost of creating an {@code Object[]} before invoking the method
     *     even if this logger is disabled for TRACE. The variants taking {@link #trace(String, Object...)} and
     *     {@link #trace(String, Object, Object)} arguments exist solely in order to avoid this hidden cost
     * </p>
     * @param format        the format string
     * @param arguments     a list of 3 or more argument
     */
    void trace(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the TRACE level with an
     * accompanying message
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    void trace(String msg, Throwable t);

    /**
     *
     * @return
     */
    boolean isDebugEnabled();

    /**
     * Log a message at the WARN level according to the specified format
     * and argument
     * <p>
     *     This form avoids superfluous object creation when the logger
     *     is disabled for the WARN level
     * </p>
     * @param format    the format string
     * @param arg       the argument
     */
    void warn(String format, Object arg);

    /**
     * Log a message at the WARN level according to the specified format
     * and argument
     * <p>
     *     This form avoids superfluous object creation when the logger
     *     is disabled for the WARN level
     * </p>
     * @param format    the format string
     * @param argA       the argument
     */
    void warn(String format, Object argA, Object argB);

    /**
     * Log a message at the WARN level according to specified format
     * and argument
     *
     * <p>
     *     This form avoids superfluous string concatenation when the logger
     *     is disabled for the WARN level. However, this variant incurs the hidden
     *     (and relatively small) cost of creating an {@code Object[]} before invoking the method
     *     even if this logger is disabled for WARN. The variants taking
     *     {@link #warn(String, Object)} and {@link #warn(String, Object)}
     *     arguments exist solely in order to avoid this hidden cost
     * </p>
     *
     * @param format
     * @param arguments
     */
    void warn(String format, Object... arguments);


}
