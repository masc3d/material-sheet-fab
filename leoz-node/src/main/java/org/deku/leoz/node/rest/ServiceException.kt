package org.deku.leoz.node.rest

import javax.ws.rs.core.Response

/**
 * REST service exception
 * @property errorCode Error code
 * @param message Message
 * @param cause Exception/throwable
 * @property entity Error entity to return
 * @property status HTTP status
 * Created by masc on 11.08.15.
 */
class ServiceException private constructor(
        val errorCode: Enum<*>? = null,
        val entity: Any? = null,
        val status: Response.Status = Response.Status.BAD_REQUEST,
        message: String,
        cause: Throwable? = null)
    : RuntimeException(message, cause) {


    @JvmOverloads constructor(
            errorCode: Enum<*>,
            message: String? = null,
            cause: Throwable? = null,
            status: Response.Status = Response.Status.BAD_REQUEST)
            : this(errorCode = errorCode, entity = null, status = status, message = message ?: errorCode.toString(), cause = cause)

    @JvmOverloads constructor(
            entity: Any,
            status: Response.Status = Response.Status.BAD_REQUEST) : this(entity = entity, status = status, message = entity.toString()
    )
}
