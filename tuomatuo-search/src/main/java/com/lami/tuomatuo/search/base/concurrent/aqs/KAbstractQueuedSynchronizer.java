package com.lami.tuomatuo.search.base.concurrent.aqs;

import java.io.Serializable;

/**
 * Provides a framework for implementing blocking locks locks and related
 * synchronizers (semaphores, event, etc) that rely on
 * first-in-first-out (FIFO) wait queues. This class sidesigned to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic(@code int) value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released. Given these, the other methods in this class carry
 * out all queuing and blocking mechanics. Subclasses can maintain
 * other state fields, but only the atomically updated {@code int}
 * value manipulated using methods {@link #getState}, {@link #setState}
 * and {@link #compareAndSetState} is tracked with repect
 * to synchronization
 *
 * <p>
 *     Subclasses should be defined as non-public internal helper
 *     classes that are used to implement the synchronization properties
 *     of their enclosing class, Class
 *     {@code KAbstractQueueSynchronizer} does not implement any
 *     synchronization interface. Instead it defines methods such as
 *     synchronization interface. Instead it defines methods such as
 *     {@link #acquireInterruptibly} that can be invoked as
 *     appropriate by concrete locks and related synchronizers to
 *     implement their public methods
 * </p>
 *
 * <p>
 *     This class supports either or both a default <em>exclusive</em>
 *     mode and a <em>shared</em> mode. When acquired in exclusive mode,
 *     attempted acquires by other threads cannot succeed. This class
 *     does not understand; these differences except in the mechanical
 *     sense that when a shared mode acquire succeeds, the next
 *     waiting thread (if n=one exists) must also determine modes share
 *     the same FIFO queue. Usually, implementation subclass support only
 *     one of these modes, but both can come into play for example in a
 *     {@link ReadWriteLock}, Subclass that support only exclusive or
 *     only shared modes need not define the methods supporting the unused mode
 * </p>
 *
 * <p>
 *     This class defines a nested {@link com.lami.tuomatuo.search.base.concurrent.spinlock.MyAbstractQueuedSynchronizer.ConditionObject} class that
 *     can be use as a {@link Condition} implementation by subclasses
 *     supporting exclusive mode for which method {@link #isHeldExclusively}
 *     reports whether synchronization is exclusively
 *     held with respect to the current thread, method {@link #release}
 *     invoked with the current {@link #getState} value fully releases
 *     this object, and {@link #acquire}, given this saved state value,
 *     ebentually restores this object to its previous acquired state. No
 *     {@code KAbstractQueueSynchronizer} method otherwise creates such a
 *     condition, so if this constraint cannot be met, do not use it. The
 *     behavior of {@link ConditionObject} depends of course on the
 *     semantics if its synchronizer implementation
 * </p>
 *
 * Created by xujiankang on 2017/1/18.
 */
public abstract class KAbstractQueuedSynchronizer extends KAbstractOwnableSynchronizer implements Serializable {

    private static final long serialVersionUID = 7373984972572414691L;

}
