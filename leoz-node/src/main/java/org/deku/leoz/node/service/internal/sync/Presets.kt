package org.deku.leoz.node.service.internal.sync

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.NumberPath

/**
 * Entity sync publisher preset
 */
data class PublisherPreset<T>(
        val entityPath: EntityPath<T>,
        val syncIdPath: NumberPath<Long>,
        val filter: ((entity: T, entityPath: EntityPath<T>, nodeUid: String) -> Predicate)? = null
)

/**
 * Entity sync consumer presets
 * Created by masc on 12.03.18.
 */
data class ConsumerPreset<T>(
        val entityPath: EntityPath<T>,
        val syncIdPath: NumberPath<Long>? = null
)