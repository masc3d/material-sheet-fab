package sx.rx;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by masc on 24/06/16.
 */
public class RxJavaReplayTest {
    @Ignore
    @Test
    public void testDelayedSubscriptionWithBlockingObservable() throws InterruptedException {
        // Time/delay for each item being emitted
        int delay = 200;
        // Number of observables
        int count = 10;
        // Number of items emitted
        AtomicInteger emitCount = new AtomicInteger(0);

        Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());

        Stream<Observable<Integer>> ovStream = IntStream.range(0, count)
                .mapToObj(i -> Observable.create((Observable.OnSubscribe<Integer>)(s -> {
                    try {
                        int item = emitCount.incrementAndGet();
                        Thread.sleep(delay);
                        System.out.println(String.format("Emitting [%d]", item));
                        s.onNext(item);
                        s.onCompleted();
                    } catch (Throwable e) {
                        s.onError(e);
                    }
                })).subscribeOn(scheduler));

        List<Observable<Integer>> ovList = ovStream.collect(Collectors.toList());

        ConnectableObservable<Integer> cov = Observable.merge(ovList)
                .subscribeOn(Schedulers.newThread())
                .replay();

        // Cook it up
        cov.connect();

        // Wait until ~half the items have been emitted
        Thread.sleep(delay * count / 2);

        cov.subscribe(i -> {
            Assert.assertFalse("Blocking until the party is over.",
                    i < count && emitCount.get() == count);
            System.out.println(String.format("Observed [%d]", i));
        });

        cov.toCompletable().await();
        System.out.println("Done2");
    }

    @Ignore
    @Test
    public void testDelayedSubscriptionWithBlockingObservable2() throws InterruptedException {
        // Time/delay for each item being emitted
        int delay = 200;
        // Number of observables
        int count = 10;
        // Number of items emitted
        AtomicInteger emitCount = new AtomicInteger(0);

        Observable<Integer> ov = Observable.create(s -> {
            try {
                for (int i = 0; i < count; i++) {
                    Thread.sleep(delay);
                    int item = emitCount.incrementAndGet();
                    System.out.println(String.format("Emitting [%d]", item));
                    s.onNext(item);
                }
                s.onCompleted();
            } catch (Throwable e) {
                s.onError(e);
            }
        });

        ConnectableObservable<Integer> cov = ov
                .subscribeOn(Schedulers.newThread())
                .replay();

        // Cook it up
        cov.connect();

        // Wait until ~half the items have been emitted
        Thread.sleep(delay * count / 2);

        cov.subscribe(i -> {
            Assert.assertFalse("Blocking until the party is over.",
                    i < count && emitCount.get() == count);
            System.out.println(String.format("Observed [%d]", i));
        });

        cov.toCompletable().await();
        System.out.println("Done");
    }
}