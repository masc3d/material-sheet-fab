package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Routines
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
class JooqMailQueueRepository {
    private val log = LoggerFactory.getLogger(JooqMailQueueRepository::class.java)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dsl: DSLContext

    fun insertSms(receiver: String, message: String) {



        try {
            if (receiver.startsWith('0')) {
                throw IllegalArgumentException("The receivers mobile number must not start with [0]. The number should consist of two sections: 1) Leading country area code without zeros 2) national phone number without the leading zero")
            }
            if (receiver.toLongOrNull() == null) {
                throw IllegalArgumentException("Receiver [$receiver] must not contain letters, symbols or special characters.")
            }

            val r = dsl.newRecord(Tables.TBLMAILQUEUE).also {
                it.id = dsl.select(Routines.fTan(54)).fetch().first().value1().toInt()
                it.station = 2
                it.queue = message
                it.status = Status.NEW
                it.defid = DefID.SMS
                it.mailaddress = receiver
                it.deleteafetersend = DeleteAfterSend.NO
                it.belegnummer = 0.0
            }

            r.store()
        } catch (e: Exception) {
            log.error(e.message)
            throw e
        }

    }

    private class DefID {
        companion object {
            const val SMS: Int = 10
        }
    }

    private class Status {
        companion object {
            const val NEW: Int = 0
        }
    }

    private class DeleteAfterSend {
        companion object {
            const val NO: Int = 0
            const val YES: Int = -1
        }
    }
}