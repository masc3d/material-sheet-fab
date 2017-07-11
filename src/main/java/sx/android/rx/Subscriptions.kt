package sx.android.rx

import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.RxActivity
import com.trello.rxlifecycle2.components.RxFragment
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.slf4j.LoggerFactory
import org.reactivestreams.Subscription

private val log = LoggerFactory.getLogger("Subscription.Extension")

/**
 * Created by masc on 06/07/16.
 */
fun Subscription.bindToLifecycle(fragment: RxFragment): Subscription {
    fragment.lifecycle()
            .filter { it == FragmentEvent.DESTROY_VIEW }
            .subscribe {
                log.info("unsubscribe ${fragment} ${it}")
                this.cancel()
            }
    return this
}

fun Subscription.bindToLifecycle(fragment: RxAppCompatDialogFragment): Subscription {
    fragment.lifecycle()
            .filter { it == FragmentEvent.DESTROY_VIEW }
            .subscribe {
                this.cancel()
            }
    return this
}

fun Subscription.bindToLifecycle(activity: RxActivity) {
    activity.lifecycle()
            .filter { it == FragmentEvent.DESTROY_VIEW }
            .subscribe {
                this.cancel()
            }
}