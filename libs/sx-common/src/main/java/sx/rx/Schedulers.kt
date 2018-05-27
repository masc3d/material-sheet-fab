package sx.rx

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.internal.schedulers.SchedulerWhen
import io.reactivex.schedulers.Schedulers

/**
 * Wrap scheduler into a {SubscribeWhen} scheduler with limited concurrency
 * Created by masc on 21.11.17.
 * @param maxConcurrency Maximum concurrency
 */
fun Scheduler.limit(maxConcurrency: Int): Scheduler =
        SchedulerWhen({ workers ->
            Completable.merge(Flowable.merge(workers, maxConcurrency))
        }, this)