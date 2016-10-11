package org.deku.leoz.node.data

import org.eclipse.persistence.config.DescriptorCustomizer
import org.eclipse.persistence.descriptors.ClassDescriptor
import org.eclipse.persistence.descriptors.DescriptorEvent
import org.eclipse.persistence.descriptors.DescriptorEventAdapter
import org.slf4j.LoggerFactory

/**
 * Change listener handling entity events
 * Created by masc on 11/10/2016.
 */
class ChangeListener : DescriptorEventAdapter() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun postInsert(event: DescriptorEvent?) {
        super.postInsert(event)
//        log.info("PREPERSIST ${event.toString()}")
    }
}

/**
 * Descriptor customizor, setting up listeners.
 * Customizers are setup via annotations, eclispelink properties or persistence.xml
 */
class Customizer : DescriptorCustomizer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun customize(descriptor: ClassDescriptor?) {
        log.info("Registering event listener")
        descriptor!!.eventManager.addListener(ChangeListener())
    }
}