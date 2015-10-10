package org.deku.leoz.rest.entities.v1

import com.wordnik.swagger.annotations.ApiModel

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

    constructor(httpStatus: Int, e: Exception) : this(httpStatus, e.getMessage()!!) {
    }
}