package com.lami.tuomatuo.search.base.concurrent.forkjoinpool;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * Abstract base class for tasks that run within a {@link KForkJoinPool}.
 * A {@code KForkJoinTask} is a thread-like entity that is much
 * lighter weight than a normal thread. Huge numbers of tasks and
 * subtasks may be hosted by a small number of actual threads in a
 * KForkJoinPool, at the price of some usage limitations
 *
 * <p>
 *     A "main" {@code KForkJoinTask} begins execution when it is
 *     explicity submitted to a {@link KForkJoinPool}, or, if not already
 *     engaged in a KForkJoin computation, commenced in the {@link
 *     KForkJoinPool#commonPool()} via {@link #fork}, {@link #invoke}, or
 *     related methods. Once started, it will usually in turn start other
 *     subtasks. As indicated by the name of this class, many programs
 *     using {@code KForkJoinTask} employ only methods {@link #fork} and
 *     {@link #join}, or derivatives such as {@link #invokeAll(KForkJoinTask...) invokeAlll}
 *     However, this class also
 *     provides a number of other methods that can come into play in
 *     advanced usages, as well as extension mechanics that allow support
 *     of new forms of fork/join processing.
 * </p>
 *
 * <p>
 *     A {@code KForkJoinTask} is a lightweight form of {@link Future}.
 *     The efficiency of {@code KForkJoinTask}s stems from a set of
 *     restrictions (that are only partially statically enforceable)
 *     reflecting their main use as computational tasks calculating pure
 *     functions or operating on purely isolated objects. The primary
 *     coordination mechanism are {@link #fork}, that arranges
 *     asynchronous execution, and {@link #join}, that doesn't proceed
 *     ideally avoid {@code synchronized} methods or blocks, and should
 *     minimize other blocking synchronization apart from joining other
 *     tasks or using synchronizers such as Phasers that are advartised to
 *     cooperate with fork/join scheduling. Subdividable tasks should also
 *     not perform blocking I/O, and should ideally access variables that
 *     are completely independent of those accessed by other running
 *     tasks. These guidelines are loosely enforced by not permitting
 *     checked exceptions such as {@code IOExceptons} to be
 *     thrown. However, computations may still encounter unchecked
 *     exceptions, that are rethrown to callers attempting to join
 *     them. These exceptions may additionally include {@link
 *     java.util.concurrent.RejectedExecutionException} stemming from internal resource
 *     exhaustion, such as failure to allocate internal task
 *     queues. Rethrown exceptions behave in the same way as regular
 *     exceptions, but, when possible, contain stack traces (as displayed
 *     for example using {@code ex.printStackTrace()}) of both the thread
 *     that initiated that computation as well as the thread actually
 *     encountering the exception; minimally only the latter.
 * </p>
 *
 * <p>
 *     It is possible to define and use KForkJoinTasks that may block
 *     but doing do requires three further considerations:
 *     (1) Completion of few if any <em>other</em> tasks should be dependent on a task
 *          that blocks on external synchronization or I/O. Event-style async
 *          tasks that are never joined (for example, those subclassing {@link java.util.concurrent.CountedCompleter})
 *          often fall into this category
 *     (2) To minimize resource impact, tasks should be small; ideally performing only the
 *          (possibly) blocking action
 *     (3) Unless the {@link KForkJoinPool.ManagedBlocker} API is used, or the
 *     number of possibly
 *     blocked tasks is known to be less than the pool's {@link
 *     KForkJoinPool#getParallelism} level, the pool cannot guarantee that enough
 *     threads will be available to ensure progress or good
 *     performance
 * </p>
 *
 * <p>
 *     The primary method for awaiting completion and extracting
 *     results of a task is {@link #join}, but there are several variants:
 *     The {@link Future#get()} methods support interruptible and/or timed
 *     waits for completion and report results using {@code Future}
 *     conventitions. Method {@link #invoke} is semantically
 *     equivalent to {@code fork()}; join() but always attempts to begin
 *     execution in the current thread. The <em>quiet</em> forms of
 *     these method
 * </p>
 *
 * Created by xjk on 2/26/17.
 */
public abstract class KForkJoinTask<V> implements Future<V>, Serializable {
}
