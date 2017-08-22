package org.deku.leoz.mobile

enum class SharedPreference(val key: String) {
    /** The currently authenticated user (email) */
    AUTHENTICATED_USER_ID("authenticated_user_id"),
    CHANGELOG_VERSION("changelog_version")
}