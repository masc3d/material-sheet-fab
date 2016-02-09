package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.App
import org.deku.leoz.central.data.sync.DatabaseSync
import org.deku.leoz.central.data.sync.EntitySync
import org.deku.leoz.node.config.PersistenceConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

/**
 * Leoz-central entity sync configuration
 * Created by masc on 20.06.15.
 */
@Configuration(App.PROFILE_CENTRAL)
@Lazy(false)
open class EntitySyncConfiguration {
    private val mLog = LogFactory.getLog(this.javaClass)

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    lateinit private var entityManagerFactory: EntityManagerFactory

    @Inject
    lateinit private var databaseSync: DatabaseSync

    /** Broker listener  */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            EntitySync.it().start()
        }

        override fun onStop() {
            EntitySync.it().close()
        }
    }

    @PostConstruct
    fun onInitialize() {
        EntitySync.it().entityManagerFactory = entityManagerFactory
        EntitySync.it().databaseSync = databaseSync

        // Start when broker is started
        ActiveMQBroker.instance.delegate.add(brokerEventListener)
        if (ActiveMQBroker.instance.isStarted)
            brokerEventListener.onStart()
    }
}
