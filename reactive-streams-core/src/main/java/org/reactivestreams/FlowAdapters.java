/***************************************************
 * Licensed under MIT No Attribution (SPDX: MIT-0) *
 ***************************************************/

package org.reactivestreams;

import java.util.concurrent.Flow;
import static java.util.Objects.requireNonNull;

/**
 * Bridge between Reactive Streams API and the Java 9 {@link Flow} API.
 */
public final class FlowAdapters {
    /** Utility class. */
    private FlowAdapters() {
        throw new IllegalStateException("No instances!");
    }

    /**
     * Converts a Flow Publisher into a Reactive Streams Publisher.
     * @param <T> the element type
     * @param flowPublisher the source Flow Publisher to convert
     * @return the equivalent Reactive Streams Publisher
     */
    @SuppressWarnings("unchecked")
    public static <T> Publisher<T> toPublisher(
            Flow.Publisher<? extends T> flowPublisher) {
        requireNonNull(flowPublisher, "flowPublisher");
        final Publisher<T> publisher;
        if (flowPublisher instanceof FlowPublisherFromReactive) {
            publisher = (Publisher<T>)(((FlowPublisherFromReactive<T>)flowPublisher).reactiveStreams);
        } else if (flowPublisher instanceof Publisher) {
            publisher = (Publisher<T>)flowPublisher;
        } else {
            publisher = new ReactivePublisherFromFlow<T>(flowPublisher);
        }
        return publisher;
    }

    /**
     * Converts a Reactive Streams Publisher into a Flow Publisher.
     * @param <T> the element type
     * @param reactiveStreamsPublisher the source Reactive Streams Publisher to convert
     * @return the equivalent Flow Publisher
     */
    @SuppressWarnings("unchecked")
    public static <T> Flow.Publisher<T> toFlowPublisher(
            Publisher<? extends T> reactiveStreamsPublisher
    ) {
        requireNonNull(reactiveStreamsPublisher, "reactiveStreamsPublisher");
        final Flow.Publisher<T> flowPublisher;
        if (reactiveStreamsPublisher instanceof ReactivePublisherFromFlow) {
            flowPublisher = (Flow.Publisher<T>)(((ReactivePublisherFromFlow<T>)reactiveStreamsPublisher).flow);
        } else if (reactiveStreamsPublisher instanceof Flow.Publisher) {
            flowPublisher = (Flow.Publisher<T>)reactiveStreamsPublisher;
        } else {
            flowPublisher = new FlowPublisherFromReactive<T>(reactiveStreamsPublisher);
        }
        return flowPublisher;
    }

    /**
     * Converts a Flow Processor into a Reactive Streams Processor.
     * @param <T> the input value type
     * @param <U> the output value type
     * @param flowProcessor the source Flow Processor to convert
     * @return the equivalent Reactive Streams Processor
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Processor<T, U> toProcessor(
            Flow.Processor<? super T, ? extends U> flowProcessor
    ) {
        requireNonNull(flowProcessor, "flowProcessor");
        final Processor<T, U> processor;
        if (flowProcessor instanceof FlowToReactiveProcessor) {
            processor = (Processor<T, U>)(((FlowToReactiveProcessor<T, U>)flowProcessor).reactiveStreams);
        } else if (flowProcessor instanceof Processor) {
            processor = (Processor<T, U>)flowProcessor;
        } else {
            processor = new ReactiveToFlowProcessor<T, U>(flowProcessor);
        }
        return processor;
    }

    /**
     * Converts a Reactive Streams Processor into a Flow Processor.
     * @param <T> the input value type
     * @param <U> the output value type
     * @param reactiveStreamsProcessor the source Reactive Streams Processor to convert
     * @return the equivalent Flow Processor
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Flow.Processor<T, U> toFlowProcessor(
            Processor<? super T, ? extends U> reactiveStreamsProcessor
        ) {
        requireNonNull(reactiveStreamsProcessor, "reactiveStreamsProcessor");
        final Flow.Processor<T, U> flowProcessor;
        if (reactiveStreamsProcessor instanceof ReactiveToFlowProcessor) {
            flowProcessor = (Flow.Processor<T, U>)(((ReactiveToFlowProcessor<T, U>)reactiveStreamsProcessor).flow);
        } else if (reactiveStreamsProcessor instanceof Flow.Processor) {
            flowProcessor = (Flow.Processor<T, U>)reactiveStreamsProcessor;
        } else {
            flowProcessor = new FlowToReactiveProcessor<T, U>(reactiveStreamsProcessor);
        }
        return flowProcessor;
    }

    /**
     * Converts a Reactive Streams Subscriber into a Flow Subscriber.
     * @param <T> the input and output value type
     * @param reactiveStreamsSubscriber the Reactive Streams Subscriber instance to convert
     * @return the equivalent Flow Subscriber
     */
    @SuppressWarnings("unchecked")
    public static <T> Flow.Subscriber<T> toFlowSubscriber(Subscriber<T> reactiveStreamsSubscriber) {
        requireNonNull(reactiveStreamsSubscriber, "reactiveStreamsSubscriber");
        final Flow.Subscriber<T> flowSubscriber;
        if (reactiveStreamsSubscriber instanceof ReactiveToFlowSubscriber) {
            flowSubscriber = (Flow.Subscriber<T>)((ReactiveToFlowSubscriber<T>)reactiveStreamsSubscriber).flow;
        } else if (reactiveStreamsSubscriber instanceof Flow.Subscriber) {
            flowSubscriber = (Flow.Subscriber<T>)reactiveStreamsSubscriber;
        } else {
            flowSubscriber = new FlowToReactiveSubscriber<T>(reactiveStreamsSubscriber);
        }
        return flowSubscriber;
    }

    /**
     * Converts a Flow Subscriber into a Reactive Streams Subscriber.
     * @param <T> the input and output value type
     * @param flowSubscriber the Flow Subscriber instance to convert
     * @return the equivalent Reactive Streams Subscriber
     */
    @SuppressWarnings("unchecked")
    public static <T> Subscriber<T> toSubscriber(Flow.Subscriber<T> flowSubscriber) {
        requireNonNull(flowSubscriber, "flowSubscriber");
        final Subscriber<T> subscriber;
        if (flowSubscriber instanceof FlowToReactiveSubscriber) {
            subscriber = (Subscriber<T>)((FlowToReactiveSubscriber<T>)flowSubscriber).reactiveStreams;
        } else if (flowSubscriber instanceof Subscriber) {
            subscriber = (Subscriber<T>)flowSubscriber;
        } else {
            subscriber = new ReactiveToFlowSubscriber<T>(flowSubscriber);
        }
        return subscriber;
    }

    /**
     * Wraps a Reactive Streams Subscription and converts the calls to a Flow Subscription.
     */
    static final class FlowToReactiveSubscription implements Flow.Subscription {
        final Subscription reactiveStreams;

        public FlowToReactiveSubscription(Subscription reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void request(long n) {
            reactiveStreams.request(n);
        }

        @Override
        public void cancel() {
            reactiveStreams.cancel();
        }

    }

    /**
     * Wraps a Flow Subscription and converts the calls to a Reactive Streams Subscription.
     */
    static final class ReactiveToFlowSubscription implements Subscription {
        final Flow.Subscription flow;

        public ReactiveToFlowSubscription(Flow.Subscription flow) {
            this.flow = flow;
        }

        @Override
        public void request(long n) {
            flow.request(n);
        }

        @Override
        public void cancel() {
            flow.cancel();
        }


    }

    /**
     * Wraps a Reactive Streams Subscriber and forwards methods of the Flow Subscriber to it.
     * @param <T> the element type
     */
    static final class FlowToReactiveSubscriber<T> implements Flow.Subscriber<T> {
        final Subscriber<? super T> reactiveStreams;

        public FlowToReactiveSubscriber(Subscriber<? super T> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            reactiveStreams.onSubscribe((subscription == null) ? null : new ReactiveToFlowSubscription(subscription));
        }

        @Override
        public void onNext(T item) {
            reactiveStreams.onNext(item);
        }

        @Override
        public void onError(Throwable throwable) {
            reactiveStreams.onError(throwable);
        }

        @Override
        public void onComplete() {
            reactiveStreams.onComplete();
        }

    }

    /**
     * Wraps a Flow Subscriber and forwards methods of the Reactive Streams Subscriber to it.
     * @param <T> the element type
     */
    static final class ReactiveToFlowSubscriber<T> implements Subscriber<T> {
        final Flow.Subscriber<? super T> flow;

        public ReactiveToFlowSubscriber(Flow.Subscriber<? super T> flow) {
            this.flow = flow;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            flow.onSubscribe((subscription == null) ? null : new FlowToReactiveSubscription(subscription));
        }

        @Override
        public void onNext(T item) {
            flow.onNext(item);
        }

        @Override
        public void onError(Throwable throwable) {
            flow.onError(throwable);
        }

        @Override
        public void onComplete() {
            flow.onComplete();
        }

    }

    /**
     * Wraps a Flow Processor and forwards methods of the Reactive Streams Processor to it.
     * @param <T> the input type
     * @param <U> the output type
     */
    static final class ReactiveToFlowProcessor<T, U> implements Processor<T, U> {
        final Flow.Processor<? super T, ? extends U> flow;

        public ReactiveToFlowProcessor(Flow.Processor<? super T, ? extends U> flow) {
            this.flow = flow;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            flow.onSubscribe((subscription == null) ? null : new FlowToReactiveSubscription(subscription));
        }

        @Override
        public void onNext(T t) {
            flow.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            flow.onError(t);
        }

        @Override
        public void onComplete() {
            flow.onComplete();
        }

        @Override
        public void subscribe(Subscriber<? super U> s) {
            flow.subscribe((s == null) ? null : new FlowToReactiveSubscriber<U>(s));
        }
    }

    /**
     * Wraps a Reactive Streams Processor and forwards methods of the Flow Processor to it.
     * @param <T> the input type
     * @param <U> the output type
     */
    static final class FlowToReactiveProcessor<T, U> implements Flow.Processor<T, U> {
        final Processor<? super T, ? extends U> reactiveStreams;

        public FlowToReactiveProcessor(Processor<? super T, ? extends U> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            reactiveStreams.onSubscribe((subscription == null) ? null : new ReactiveToFlowSubscription(subscription));
        }

        @Override
        public void onNext(T t) {
            reactiveStreams.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            reactiveStreams.onError(t);
        }

        @Override
        public void onComplete() {
            reactiveStreams.onComplete();
        }

        @Override
        public void subscribe(Flow.Subscriber<? super U> s) {
            reactiveStreams.subscribe((s == null) ? null : new ReactiveToFlowSubscriber<U>(s));
        }
    }

    /**
     * Reactive Streams Publisher that wraps a Flow Publisher.
     * @param <T> the element type
     */
    static final class ReactivePublisherFromFlow<T> implements Publisher<T> {
        final Flow.Publisher<? extends T> flow;

        public ReactivePublisherFromFlow(Flow.Publisher<? extends T> flowPublisher) {
            this.flow = flowPublisher;
        }

        @Override
        public void subscribe(Subscriber<? super T> reactive) {
            flow.subscribe((reactive == null) ? null : new FlowToReactiveSubscriber<T>(reactive));
        }
    }

    /**
     * Flow Publisher that wraps a Reactive Streams Publisher.
     * @param <T> the element type
     */
    static final class FlowPublisherFromReactive<T> implements Flow.Publisher<T> {

        final Publisher<? extends T> reactiveStreams;

        public FlowPublisherFromReactive(Publisher<? extends T> reactivePublisher) {
            this.reactiveStreams = reactivePublisher;
        }

        @Override
        public void subscribe(Flow.Subscriber<? super T> flow) {
            reactiveStreams.subscribe((flow == null) ? null : new ReactiveToFlowSubscriber<T>(flow));
        }
    }

}
