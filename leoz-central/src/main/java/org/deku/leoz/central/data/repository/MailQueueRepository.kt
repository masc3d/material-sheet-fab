package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named

@Named
class MailQueueRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext

    fun insertSms(receiver: String, message: String) {

        if (receiver.startsWith('0')) {
            throw IllegalArgumentException("The receivers mobile number must not start with [0]. The number should consist of two sections: 1) Leading country area code without zeros 2) national phone number without the leading zero")
        }
        if (receiver.toLongOrNull() == null) {
            throw IllegalArgumentException("Receiver [$receiver] must not contain letters, symbols or special characters.")
        }

        val r = dslContext.newRecord(Tables.TBLMAILQUEUE).also {
            it.station = 2
            it.queue = message
            it.status = Status.NEW
            it.defid = DefID.SMS
            it.mailaddress = receiver
            it.deleteafetersend = DeleteAfterSend.NO
            it.belegnummer = 0.0
        }

        r.store()
    }

    private open class DefID {
        companion object {
            const val SMS: Int = 10
        }
    }

    private open class Status {
        companion object {
            const val NEW: Int = 0
        }
    }

    private open class DeleteAfterSend {
        companion object {
            const val NO: Int = 0
            const val YES: Int = -1
        }
    }
}