package org.deku.leo2.central.data.sync

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leo2.messaging.activemq.ActiveMQContext
import org.deku.leo2.node.data.sync.EntityPublisher
import sx.Disposable
import sx.LazyInstance
import sx.event
import sx.event.EventDelegate

import javax.persistence.EntityManagerFactory
import kotlin.platform.platformStatic
import kotlin.properties.Delegates

/**
 * Supervising sync class
 * Created by masc on 19.06.15.
 */
public class EntitySync private constructor() : Disposable {
    //region Singleton
    companion object Singleton {
        private val instance = EntitySync()
        @platformStatic public fun it(): EntitySync {
            return instance;
        }
    }
    //endregion

    /** Log */
    private val log = LogFactory.getLog(this.javaClass)
    /** Entity publisher */
    private var entityPublisher: EntityPublisher by Delegates.notNull()

    /** Database sync event listener */
    private val databaseSyncEvent = DatabaseSync.EventListener { clazz, timestamp ->
        this.entityPublisher.publish(clazz, timestamp);
    };

    /** Injected: jpa entity manager factory  */
    public var entityManagerFactory: EntityManagerFactory by Delegates.notNull()
    /** Injected: database sync instance */
    public var databaseSync: DatabaseSync by Delegates.notNull()

    /** Start entity sync */
    public fun start() {
        // Register event
        this.databaseSync.getEventDelegate().add(databaseSyncEvent)

        // Configure and start publisher
        this.entityPublisher = EntityPublisher(ActiveMQContext.instance(), this.entityManagerFactory)
        this.entityPublisher.start()
    }

    /** Dispose */
    override fun dispose() {
        this.entityPublisher.stop()
    }
}
