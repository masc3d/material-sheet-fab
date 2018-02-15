package org.deku.leoz.rest

import org.deku.leoz.model.UserRole

/**
 * Created by masc on 15.02.18.
 */

/**
 * Restrict access by authorized user role
 * @param role User role
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class RestrictRoles(
        vararg val role: UserRole
)
