package org.deku.leoz.node.data.repositories.system

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.querydsl.core.types.Predicate
import org.deku.leoz.node.data.jpa.SysProperty
import org.eclipse.persistence.config.CacheUsage
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import sx.annotationOfType
import sx.annotationOfTypeOrNull
import java.io.StringWriter
import javax.persistence.QueryHint

/**
 * Available keys
 */
enum class PropertyKeys(val value: String) {
    BUNDLE_UPDATE_SERVICE("leoz.bundle-update-service")
}

/**
 * Property repository
 * Created by masc on 29.06.15.
 */
interface PropertyRepository :
        JpaRepository<SysProperty, String>,
        QueryDslPredicateExecutor<SysProperty> {

    @QueryHints(
            QueryHint(name = org.eclipse.persistence.config.QueryHints.CACHE_USAGE, value = CacheUsage.CheckCacheThenDatabase)
    )
    override fun findOne(predicate: Predicate): SysProperty
}

// Extension methods

/**
 * JSON object mapper (jackson)
 */
private val jsonObjectMapper by lazy {
    val om = ObjectMapper()
    om.enable(SerializationFeature.INDENT_OUTPUT);
    om.setDefaultPrettyPrinter(
            DefaultPrettyPrinter().withObjectIndenter(
                    DefaultPrettyPrinter.FixedSpaceIndenter()))
    om
}

/**
 * Property key annotation
 */
annotation class PropertyKey(val key: PropertyKeys)

/**
 * Save object
 */
fun PropertyRepository.saveObject(value: Any) {
    val a = value.javaClass.annotationOfType(PropertyKey::class.java)

    val sw = StringWriter()
    sw.use {
        jsonObjectMapper.writer().writeValue(sw, value)
    }
    this.save(SysProperty(a.key.value, sw.toString()))
}

/**
 * Load object.
 * In case of deserialization failure this method will attempt to return a new instance of the class
 */
fun <T> PropertyRepository.loadObject(cls: Class<T>): T {
    return try {
        val a = cls.annotationOfType(PropertyKey::class.java)

        jsonObjectMapper.readerFor(cls).readValue(this.findOne(a.key.value)?.value)
    } catch (e: Exception) {
        LoggerFactory.getLogger(this.javaClass).error(e.message, e)
        cls.newInstance()
    }
}