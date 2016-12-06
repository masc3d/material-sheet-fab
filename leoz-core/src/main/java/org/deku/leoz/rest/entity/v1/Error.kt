package org.deku.leoz.rest.entity.v1

import io.swagger.annotations.ApiModel

/**
 * REST service error
 */
@ApiModel(value = "Error", description = "Service error")
class Error(
        val httpStatus: Int?,
        val code: Int?,
        val message: String) {

    constructor(httpStatus: Int, message: String) : this(httpStatus, null, message) {
    }

    constructor(httpStatus: Int, e: Exception) : this(httpStatus, e.message!!) {
    }
}