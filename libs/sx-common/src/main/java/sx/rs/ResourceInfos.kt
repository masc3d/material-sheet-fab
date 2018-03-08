package sx.rs

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.util.ReflectionUtils
import sx.reflect.allInterfaces
import java.lang.reflect.Parameter
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ResourceInfo

/**
 * JAX/RS resource info extensions
 * Created by masc on 08.03.18.
 */

/** Annotated parameter */
data class AnnotatedParameter<A : Annotation>(
        val parameter: Parameter,
        val annotation: A,
        val value: String?
)

/**
 * Find method or class level annotation
 *
 * NOTE: method relies on spring-core reflection utils for cached lookups
 *
 * @param a Annotation type
 */
fun <A : Annotation> ResourceInfo.annotationOfType(a: Class<A>): A? {
    return AnnotationUtils.findAnnotation(this.resourceMethod, a)
            ?: AnnotationUtils.findAnnotation(this.resourceClass, a)
}

/**
 * Find parameter level annotation for resource method on all interfaces
 *
 * NOTE: method relies on spring-core reflection utils for cached lookups
 *
 * @param a Annotation type
 * @param requestContext The request context used to look up parameter values
 */
fun <A : Annotation> ResourceInfo.annotatedParametersOfType(a: Class<A>, requestContext: ContainerRequestContext): Sequence<AnnotatedParameter<A>> {
    return sequenceOf(
            this.resourceClass
    ).plus(
            this.resourceClass.allInterfaces
    )
            // Filter and map to matching methods on all interfaces
            .mapNotNull {
                ReflectionUtils.findMethod(it, this.resourceMethod.name, *this.resourceMethod.parameterTypes)
            }
            .mapNotNull { method ->
                // Filter method parameters for annotation
                method.parameters.mapNotNull { p ->
                    AnnotationUtils.findAnnotation(p, a)?.let {
                        AnnotatedParameter(
                                parameter = p,
                                annotation = it,
                                value = AnnotationUtils.findAnnotation(p, QueryParam::class.java)?.let {
                                    requestContext.uriInfo.queryParameters.getFirst(it.value)
                                } ?: AnnotationUtils.findAnnotation(p, PathParam::class.java)?.let {
                                    requestContext.uriInfo.pathParameters.getFirst(it.value)
                                }
                        )
                    }
                }
                        .firstOrNull()
            }
}