package com.lami.tuomatuo.search.base.concurrent.phaser;

/**
 * A reusable synchronization barrier, similar in functionality to
 * {@link java.util.concurrent.CyclicBarrier} and
 * {@link java.util.concurrent.CountDownLatch}
 * but supporting more flexible usage.
 *
 * <p>
 *      <b>Registration</b> Unlike the case for other barriers, the
 *      number of parties <em>registered</em> to synchronize on a phaser
 *      may vary over time. Tasks may be registered at any time (using
 *      methods {@link #"register}), {@link #"bulkRegister}, or forms of
 *      constructors establishing initial numbers of parties, and
 *      optionally deregistered upon any arrival using {@link #"arriveAndDeregister}
 *      As is the case with most basic
 *      synchronization constructs, registration and deregistration affect
 *      only internal counts: they do not establish any further internal
 *      bookkeeping, so introduce such bookkeeping by subclassing this class
 * </p>
 *
 * <p>
 *     <b>Synchronization</b> Like a {@code CyclicBarrier} a {@code
 *     Phaser}, may be repeatedly awaited. Method {@link #"arriveAndAwaitAdvance}
 * </p>
 *
 * Created by xjk on 1/4/17.
 */
public class Phaser {
}
