package sx.persistence.eclipselink.h2

import org.eclipse.persistence.mappings.DatabaseMapping
import org.eclipse.persistence.sessions.Session
import javax.persistence.Converter

/**
 * UUID converter for eclipselink and h2.
 * This is a passthrough implementation, as eclipselink (2.7.1) applies direct field mapping, which fails on UUID fields.
 * Created by masc on 24.03.18.
 */
@Converter
class UUIDConverter : org.eclipse.persistence.mappings.converters.Converter {
    override fun convertObjectValueToDataValue(objectValue: Any?, session: Session): Any? {
        return objectValue
    }

    override fun convertDataValueToObjectValue(dataValue: Any?, session: Session): Any? {
        return dataValue
    }

    override fun isMutable(): Boolean {
        return false
    }

    override fun initialize(mapping: DatabaseMapping, session: Session) {
    }
}