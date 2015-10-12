package org.deku.leoz.central.data.sync

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.messaging.activemq.ActiveMQContext
import org.deku.leoz.node.data.sync.EntityPublisher
import sx.Disposable
import sx.LazyInstance
import sx.event.EventDelegate
import java.sql.Timestamp
import javax.inject.Named

import javax.persistence.EntityManagerFactory
import kotlin.properties.Delegates

/**
 * Supervising sync class
 * TODO: move to EntitySyncConfiguration
 * Created by masc on 19.06.15.
 */
@Named
class EntitySync private constructor() : Disposable {
    //region Singleton
    companion object Singleton {
        private val instance = EntitySync()
        @JvmStatic fun it(): EntitySync {
            return instance;
        }
    }
    //endregion

    /** Log */
    private val log = LogFactory.getLog(this.javaClass)
    /** Entity publisher */
    private var entityPublisher: EntityPublisher by Delegates.notNull()

    /** Injected: jpa entity manager factory  */
    var entityManagerFactory: EntityManagerFactory by Delegates.notNull()
    /** Injected: database sync instance */
    var databaseSync: DatabaseSync by Delegates.notNull()

    /** Database sync event listener */
    private val databaseSyncEvent = object : DatabaseSync.EventListener {
        override fun onUpdate(entityType: Class<out Any?>, currentTimestamp: Timestamp?) {
            // Publish notification to consumers on databasesync update
            entityPublisher.publish(entityType, currentTimestamp);
        }
    }

    /** Start entity sync */
    fun start() {
        // Register event
        this.databaseSync.eventDelegate.add(databaseSyncEvent)

        // Configure and start publisher
        this.entityPublisher = EntityPublisher(
                ActiveMQContext.instance,
                this.entityManagerFactory)

        this.entityPublisher.start()
    }

    /** Dispose */
    override fun dispose() {
        this.entityPublisher.stop()
    }
}
