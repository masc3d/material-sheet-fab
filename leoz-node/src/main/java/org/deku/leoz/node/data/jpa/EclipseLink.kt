package org.deku.leoz.node.data.jpa

import org.eclipse.persistence.config.DescriptorCustomizer
import org.eclipse.persistence.descriptors.ClassDescriptor
import org.eclipse.persistence.descriptors.DescriptorEvent
import org.eclipse.persistence.descriptors.DescriptorEventAdapter
import org.eclipse.persistence.descriptors.DescriptorEventManager
import org.slf4j.LoggerFactory
import sx.log.slf4j.debug
import sx.log.slf4j.trace

/**
 * Change listener handling entity events
 *
 * Those events only fire on direct entity manager interactions.
 * EG. a JPA delete will NOT fire delete events
 *
 * Created by masc on 11/10/2016.
 */
class EclipseEventListener : DescriptorEventAdapter() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun aboutToDelete(event: DescriptorEvent?) {
        log.trace { "ABOUTTODELETE ${event?.source}" }
    }

    override fun aboutToInsert(event: DescriptorEvent?) {
        log.trace { "ABOUTTOINSERT ${event?.source}" }
    }

    override fun aboutToUpdate(event: DescriptorEvent?) {
        log.trace { "ABOUTTOUPDATE ${event?.source}" }
    }

    override fun postBuild(event: DescriptorEvent?) {
        log.trace { "POSTBUILD ${event?.source}" }
    }

    override fun postClone(event: DescriptorEvent?) {
        log.trace { "POSTCLONE ${event?.source}" }
    }

    override fun postDelete(event: DescriptorEvent?) {
        log.trace { "POSTDELETE ${event?.source}" }
    }

    override fun postInsert(event: DescriptorEvent?) {
        log.trace { "POSTINSERT ${event?.source}" }
    }

    override fun postMerge(event: DescriptorEvent?) {
        log.trace { "POSTMERGE ${event?.source}" }
    }

    override fun postRefresh(event: DescriptorEvent?) {
        log.trace { "POSTREFRESH ${event?.source}" }
    }

    override fun postWrite(event: DescriptorEvent?) {
        log.trace { "POSTWRITE ${event?.source}" }
    }

    override fun postUpdate(event: DescriptorEvent?) {
        log.trace { "POSTUPDATE ${event?.source}" }
    }

    override fun preDelete(event: DescriptorEvent?) {
        log.trace { "PREDELETE ${event?.source}" }
    }

    override fun preInsert(event: DescriptorEvent?) {
        log.trace { "PREINSERT ${event?.source}" }
    }

    override fun prePersist(event: DescriptorEvent?) {
        log.trace { "PREPERSIST${event?.source}" }
    }

    override fun preRemove(event: DescriptorEvent?) {
        log.trace { "PREREMOVE ${event?.source}" }
    }

    override fun preUpdate(event: DescriptorEvent?) {
        log.trace { "PREUPDATE ${event?.source}" }
    }

    override fun preUpdateWithChanges(event: DescriptorEvent?) {
        log.trace { "PREUPDATEWITHCHANGES ${event?.source}" }
    }

    override fun preWrite(event: DescriptorEvent?) {
        log.trace { "PREWRITE ${event?.source}" }
    }
}

/**
 * Descriptor customizor, setting up listeners.
 * Customizers are setup via annotations, eclispelink properties or persistence.xml
 */
class EclipseLinkCustomizer : DescriptorCustomizer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun customize(descriptor: ClassDescriptor?) {
        if (descriptor == null)
            return
        log.info("Registering event listener [${descriptor.alias}]")

        // Setting all entites to cacheable
        descriptor.cachePolicy.setCacheable(true)

        // Register entity event listener
        descriptor.eventManager.addListener(EclipseEventListener())
    }
}