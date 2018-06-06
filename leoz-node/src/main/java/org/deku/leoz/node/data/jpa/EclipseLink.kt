package org.deku.leoz.node.data.jpa

import org.eclipse.persistence.config.DescriptorCustomizer
import org.eclipse.persistence.descriptors.ClassDescriptor
import org.eclipse.persistence.descriptors.DescriptorEvent
import org.eclipse.persistence.descriptors.DescriptorEventAdapter
import org.eclipse.persistence.mappings.DirectToFieldMapping
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter
import org.eclipse.persistence.sessions.Session
import org.eclipse.persistence.sessions.SessionEvent
import org.eclipse.persistence.sessions.SessionEventAdapter
import org.slf4j.LoggerFactory
import sx.log.slf4j.debug
import sx.log.slf4j.trace
import sx.persistence.eclipselink.h2.UUIDConverter
import java.util.*

/**
 * JPA notification emitter singleton
 */
object EclipselinkJpaEmitter : JpaEmitter() {}

/**
 * Eclipselink event adapter / listener
 *
 * Those events only fire on direct entity manager interactions.
 * EG. a JPA delete will NOT fire delete events
 *
 * Created by masc on 11/10/2016.
 */
class EclipselinkEventListener(
        val descriptor: ClassDescriptor
) : DescriptorEventAdapter() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    enum class Type {
        ABOUT_TO_DELETE,
        ABOUT_TO_INSERT,
        ABOUT_TO_UPDATE,
        POST_BUILD,
        POST_CLONE,
        POST_DELETE,
        POST_INSERT,
        POST_MERGE,
        POST_REFRESH,
        POST_WRITE,
        POST_UPDATE,
        PRE_DELETE,
        PRE_INSERT,
        PRE_PERSIST,
        PRE_REMOVE,
        PRE_UPDATE,
        PRE_UPDATE_WITH_CHANGES,
        PRE_WRITE
    }

    val emitter = EclipselinkJpaEmitter

    data class Event(
            val type: Type,
            val event: DescriptorEvent
    )

    private fun DescriptorEvent.emit(type: Type) {
        val event = Event(type, this)
        log.trace { event }
    }

    override fun aboutToDelete(event: DescriptorEvent) {
        event.emit(Type.ABOUT_TO_DELETE)

        emitter.emitPreUpdate(
                JpaUpdate(
                        type = JpaUpdateType.DELETED,
                        entityType = this.descriptor.javaClass,
                        old = event.originalObject, value = event.`object`
                )
        )
    }

    override fun aboutToInsert(event: DescriptorEvent) {
        event.emit(Type.ABOUT_TO_INSERT)

        emitter.emitPreUpdate(
                JpaUpdate(
                        type = JpaUpdateType.INSERTED,
                        entityType = this.descriptor.javaClass,
                        old = event.originalObject, value = event.`object`
                )
        )
    }

    override fun aboutToUpdate(event: DescriptorEvent) {
        event.emit(Type.ABOUT_TO_UPDATE)

        emitter.emitPreUpdate(
                JpaUpdate(
                        type = JpaUpdateType.UPDATED,
                        entityType = this.descriptor.javaClass,
                        old = event.originalObject, value = event.`object`
                )
        )
    }

    override fun postBuild(event: DescriptorEvent) {
        event.emit(Type.POST_BUILD)
    }

    override fun postClone(event: DescriptorEvent) {
        event.emit(Type.POST_CLONE)
    }

    override fun postDelete(event: DescriptorEvent) {
        event.emit(Type.POST_DELETE)
    }

    override fun postInsert(event: DescriptorEvent) {
        event.emit(Type.POST_INSERT)
    }

    override fun postMerge(event: DescriptorEvent) {
        event.emit(Type.POST_MERGE)
    }

    override fun postRefresh(event: DescriptorEvent) {
        event.emit(Type.POST_REFRESH)
    }

    override fun postWrite(event: DescriptorEvent) {
        event.emit(Type.POST_WRITE)
    }

    override fun postUpdate(event: DescriptorEvent) {
        event.emit(Type.POST_UPDATE)
    }

    override fun preDelete(event: DescriptorEvent) {
        event.emit(Type.PRE_DELETE)
    }

    override fun preInsert(event: DescriptorEvent) {
        event.emit(Type.PRE_INSERT)
    }

    override fun prePersist(event: DescriptorEvent) {
        event.emit(Type.PRE_PERSIST)
    }

    override fun preRemove(event: DescriptorEvent) {
        event.emit(Type.PRE_REMOVE)
    }

    override fun preUpdate(event: DescriptorEvent) {
        event.emit(Type.PRE_UPDATE)
    }

    override fun preUpdateWithChanges(event: DescriptorEvent) {
        event.emit(Type.PRE_UPDATE_WITH_CHANGES)
    }

    override fun preWrite(event: DescriptorEvent) {
        event.emit(Type.PRE_WRITE)
    }
}

/**
 * Eclipselink Descriptor customizor
 *
 * Runtime setup for listeners and descriptor specific attribtues.
 */
class EclipselinkDescriptorCustomizer
    :
        DescriptorCustomizer {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun customize(descriptor: ClassDescriptor?) {
        if (descriptor == null)
            return
        log.info("Registering event listener [${descriptor.alias}]")

        // Setting all entites to cacheable
        descriptor.cachePolicy.setCacheable(true)

        // Register entity event listener
        val listener = EclipselinkEventListener(
                descriptor = descriptor
        )
        descriptor.eventManager.addListener(listener)
    }
}

/**
 * Eclipselink session customizer
 *
 * Runtime mappings and type conversions are applied here.
 */
class EclipselinkSessionListener : SessionEventAdapter() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun preLogin(event: SessionEvent) {
        for ((type, desc) in event.session.descriptors) {
            desc.mappings.forEach { mapping ->
                val dfm = mapping as? DirectToFieldMapping

                if (dfm != null) {
                    //region Converters
                    var converter = dfm.converter

                    // apply converters to fields which jpa cannot map directly.
                    // that way no (jpa provider speciric) annotations have to be used to entities.
                    if (converter != null && converter is SerializedObjectConverter) {
                        // Lookup return type and override converter for specific types
                        when (type.getMethod(mapping.getMethodName).returnType) {
                            UUID::class.java -> {
                                // H2 requires a simple pass through converter for UUID fields
                                converter = UUIDConverter()
                            }
                        }

                        dfm.converter = converter
                    }
                    //endregion
                }
            }
        }
    }

    override fun preBeginTransaction(event: SessionEvent?) {
        log.trace("PRE_TA_BEGIN")
        EclipselinkJpaEmitter.emitTransactionBegin()
    }

    override fun preCommitTransaction(event: SessionEvent?) {
        log.trace("PRE_TA_COMMIT")
        EclipselinkJpaEmitter.emitTransactionCommit()
    }
}




