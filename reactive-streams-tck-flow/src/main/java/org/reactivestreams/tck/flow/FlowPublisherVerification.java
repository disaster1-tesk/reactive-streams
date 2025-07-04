/***************************************************
 * Licensed under MIT No Attribution (SPDX: MIT-0) *
 ***************************************************/

package org.reactivestreams.tck.flow;

import org.reactivestreams.Publisher;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import java.util.concurrent.Flow;

/**
 * Provides tests for verifying a Java 9+ {@link Flow.Publisher} specification rules.
 *
 * @see Flow.Publisher
 */
public abstract class FlowPublisherVerification<T> extends PublisherVerification<T> {

  public FlowPublisherVerification(TestEnvironment env, long publisherReferenceGCTimeoutMillis) {
    super(env, publisherReferenceGCTimeoutMillis);
  }

  public FlowPublisherVerification(TestEnvironment env) {
    super(env);
  }

  @Override
  final public Publisher<T> createPublisher(long elements) {
    final Flow.Publisher<T> flowPublisher = createFlowPublisher(elements);
    return FlowAdapters.toPublisher(flowPublisher);
  }
  /**
   * This is the main method you must implement in your test incarnation.
   * It must create a Publisher for a stream with exactly the given number of elements.
   * If `elements` is `Long.MAX_VALUE` the produced stream must be infinite.
   */
  public abstract Flow.Publisher<T> createFlowPublisher(long elements);

  @Override
  final public Publisher<T> createFailedPublisher() {
    final Flow.Publisher<T> failed = createFailedFlowPublisher();
    if (failed == null) return null; // because `null` means "SKIP" in createFailedPublisher
    else return FlowAdapters.toPublisher(failed);
  }
  /**
   * By implementing this method, additional TCK tests concerning a "failed" publishers will be run.
   *
   * The expected behaviour of the {@link Flow.Publisher} returned by this method is hand out a subscription,
   * followed by signalling {@code onError} on it, as specified by Rule 1.9.
   *
   * If you ignore these additional tests, return {@code null} from this method.
   */
  public abstract Flow.Publisher<T> createFailedFlowPublisher();
}
