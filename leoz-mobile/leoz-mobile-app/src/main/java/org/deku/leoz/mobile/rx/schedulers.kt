package org.deku.leoz.mobile.rx

/**
 * Application specific schedulers
 * Created by masc on 13.09.17.
 */
class Schedulers {
    /** Dedicated scheduler for database operations (single thread) */
    val database by lazy { io.reactivex.schedulers.Schedulers.single() }
}