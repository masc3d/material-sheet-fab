package org.deku.leoz.node.data

import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

/**
 * Created by masc on 26.01.18.
 */
fun <T> TransactionTemplate.run(block: () -> T): T {
    return this.execute(object: TransactionCallback<T> {
        override fun doInTransaction(status: TransactionStatus?): T {
            return block()
        }
    }) as T
}