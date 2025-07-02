/***************************************************
 * Licensed under MIT No Attribution (SPDX: MIT-0) *
 ***************************************************/

package org.reactivestreams.example.unicast;

import org.reactivestreams.Publisher;
import org.reactivestreams.example.unicast.AsyncIterablePublisher;
import org.reactivestreams.example.unicast.InfiniteIncrementNumberPublisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Test // Must be here for TestNG to find and run this, do not remove
public class UnboundedIntegerIncrementPublisherTest extends PublisherVerification<Integer> {

  private ExecutorService e;
  @BeforeClass void before() { e = Executors.newFixedThreadPool(4); }
  @AfterClass void after() { if (e != null) e.shutdown(); }

  public UnboundedIntegerIncrementPublisherTest() {
    super(new TestEnvironment());
  }

  @Override
  public Publisher<Integer> createPublisher(long elements) {
    return new InfiniteIncrementNumberPublisher(e);
  }

  @Override public Publisher<Integer> createFailedPublisher() {
    return new AsyncIterablePublisher<Integer>(new Iterable<Integer>() {
      @Override public Iterator<Integer> iterator() {
        throw new RuntimeException("Error state signal!");
      }
    }, e);
  }

  @Override public long maxElementsFromPublisher() {
    return super.publisherUnableToSignalOnComplete();
  }
}
