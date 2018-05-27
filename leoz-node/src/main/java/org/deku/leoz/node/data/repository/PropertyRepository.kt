package org.deku.leoz.node.data.repository

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.querydsl.core.types.Predicate
import org.deku.leoz.node.data.jpa.LclProperty
import org.eclipse.persistence.config.CacheUsage
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import sx.annotationOfType
import java.io.StringWriter
import java.util.*
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
        JpaRepository<LclProperty, String>,
        QuerydslPredicateExecutor<LclProperty> {

    @QueryHints(
            QueryHint(name = org.eclipse.persistence.config.QueryHints.CACHE_USAGE, value = CacheUsage.CheckCacheThenDatabase)
    )
    override fun findOne(predicate: Predicate): Optional<LclProperty>
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
        jsonObjectMapper
                .writer()
                .writeValue(sw, value)
    }
    this.save(LclProperty(a.key.value, sw.toString()))
}

/**
 * Load object.
 * In case of deserialization failure this method will attempt to return a new instance of the class
 */
fun <T> PropertyRepository.loadObject(cls: Class<T>): T {
    try {
        val a = cls.annotationOfType(PropertyKey::class.java)
        val record = this.findById(a.key.value).orElse(null)
        return if (record != null) jsonObjectMapper
                .readerFor(cls)
                .readValue(record.value) else cls.getDeclaredConstructor().newInstance()
    } catch (e: InstantiationException) {
        throw e
    } catch (e: Exception) {
        LoggerFactory.getLogger(this.javaClass).error(e.message, e)
        return cls.getDeclaredConstructor().newInstance()
    }
}