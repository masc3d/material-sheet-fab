package sx.android.rx

import com.trello.rxlifecycle.ActivityEvent
import com.trello.rxlifecycle.FragmentEvent
import com.trello.rxlifecycle.components.RxActivity
import com.trello.rxlifecycle.components.RxFragment
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscription

private val log = LoggerFactory.getLogger("Subscription.Extension")

/**
 * Created by masc on 06/07/16.
 */
fun Subscription.bindToLifecycle(fragment: RxFragment): Subscription {
    fragment.lifecycle()
            .filter { it == FragmentEvent.DESTROY_VIEW }
            .subscribe {
                log.info("unsubscribe ${fragment} ${it}")
                this.unsubscribe()
            }
    return this
}

fun Subscription.bindToLifecycle(fragment: RxAppCompatDialogFragment): Subscription {
    fragment.lifecycle()
            .filter { it == FragmentEvent.DESTROY_VIEW }
            .subscribe {
                this.unsubscribe()
            }
    return this
}

fun Subscription.bindToLifecycle(activity: RxActivity) {
    activity.lifecycle()
            .filter { it == FragmentEvent.DESTROY_VIEW }
            .subscribe {
                this.unsubscribe()
            }
}