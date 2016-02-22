package org.deku.leoz.central.config

/**
 * Leoz-central log listener configuration
 * Created by masc on 29.06.15.
 */
//@Configuration
//@Lazy(false)
//open class LogListenerConfiguration {
//    internal var log = LogFactory.getLog(this.javaClass)
//
//    /** Log listener instance  */
//    private val logListener: LogListener
//
//    init {
//        logListener = LogListener(ActiveMQConfiguration.instance)
//    }
//
//    private val brokerEventListener = object : Broker.DefaultEventListener() {
//        override fun onStart() {
//            logListener.start()
//        }
//
//        override fun onStop() {
//            logListener.stop()
//        }
//    }
//
//    @PostConstruct
//    fun onInitialize() {
//        // Register to broker start
//        ActiveMQBroker.instance.delegate.add(brokerEventListener)
//    }
//
//    @PreDestroy
//    fun onDestroy() {
//        logListener.close()
//    }
//}
