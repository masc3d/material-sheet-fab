package org.deku.leoz.service.entity

import io.swagger.annotations.ApiModel

/**
 * REST service error
 */
@ApiModel(value = "Error", description = "Service error")
class ServiceError(
        val code: Enum<*>? = null,
        val message: String = "") {

    constructor(code: Enum<*>? = null, cause: Throwable): this(code, cause.message ?: "")
}