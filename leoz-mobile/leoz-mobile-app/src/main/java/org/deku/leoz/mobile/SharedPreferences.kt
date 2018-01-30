package org.deku.leoz.mobile

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper

enum class SharedPreference(val key: String) {
    /** The currently authenticated user (email) */
    AUTHENTICATED_USER_ID("authenticated_user_id"),
    CHANGELOG_VERSION("changelog_version"),
    OPTIMIZATION_OPTIONS("optimization_options")
}

/** Object mapper used to (de)serialize preferences objects */
private val mapper: ObjectMapper by lazy {
    ObjectMapper().also {
        it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
}

/**
 * Get (json) deserialized object
 * @param key Shared preference key
 * @param cls Type
 */
fun <T> SharedPreferences.getObject(key: String, cls: Class<T>): T =
    mapper.readValue(this.getString(key, "{}"), cls)

/**
 * Put (json) serialized object
 * @param key Shared preference key
 * @param obj Object to store
 */
@SuppressLint("CommitPrefEdits")
fun <T> SharedPreferences.putObject(key: String, obj: T) {
    this.edit().also {
        it.putString(key, mapper.writeValueAsString(obj))
        it.apply()
    }
}