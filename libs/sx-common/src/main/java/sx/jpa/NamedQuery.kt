package sx.jpa

import com.querydsl.core.types.ParamExpression
import com.querydsl.core.types.dsl.Param
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.JPQLSerializer
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQuery
import org.slf4j.LoggerFactory
import java.beans.BeanInfo
import java.beans.Introspector
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

/**
 * QueryDSL based named JPA query
 * @param entityManager JPA entity manager
 * @param resultType The result type of the prepared query
 * @param paramsType The parameter structure type for the query (should only contain QueryDSL Param<T> fields)
 * @param hints JPA query hints
 * @param query Query supplier
 */
class NamedQuery<R, P>(
        val entityManager: EntityManager,
        val resultType: Class<R>,
        val paramsType: Class<P>,
        val hints: List<Pair<String, Any>> = listOf(),
        val query: (q: JPAQuery<R>, p: P) -> JPQLQuery<R>) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val paramTypeBeanInfo: BeanInfo
    val params: P

    /**
     * Parameter meta info
     */
    private data class ParamMetaInfo(
            val name: String,
            val type: Class<*>,
            val param: Param<*>
    )

    /** Parameter meta infos */
    private val paramMetaInfos: List<ParamMetaInfo>

    /** List of meta infos by QueryDSL param instance */
    private val metaInfoByParam: Map<Param<*>, ParamMetaInfo>

    /** JPA query string */
    private val jpql: String

    /**
     * QueryDSL JPQL serializer which produces named parameters
     */
    inner private class NamedParamJPQLSerializer()
        :
            JPQLSerializer(JPQLTemplates.DEFAULT) {

        override fun visit(param: ParamExpression<*>, context: Void?): Void? {
            this.append(":${this@NamedQuery.metaInfoByParam[param]!!.name}")
            return null
        }
    }

    /**
     * c'tor
     */
    init {
        // Create params instance
        this.params = this.paramsType.newInstance()

        // Introspect bean and create meta infos from it
        this.paramTypeBeanInfo = Introspector.getBeanInfo(paramsType)

        this.paramTypeBeanInfo.propertyDescriptors.forEach {
            log.info("${it.name} ${it.propertyType}")
        }

        this.paramMetaInfos = this.paramTypeBeanInfo.propertyDescriptors
                .filter { it.propertyType != Class::class.java }
                .map {
                    ParamMetaInfo(
                            name = it.name,
                            type = it.propertyType,
                            param = it.readMethod.invoke(this.params) as Param<*>
                    )
                }

        this.metaInfoByParam = mapOf<Param<*>, ParamMetaInfo>(
                *paramMetaInfos.map {
                    Pair(it.param, it)
                }.toTypedArray())

        // Create QueryDSL JPA query
        val qJpaQuery = this.query.invoke(
                JPAQuery<R>(this.entityManager),
                this.params
        )

        // Render JPQL
        val serializer = NamedParamJPQLSerializer()
        serializer.serialize(qJpaQuery.metadata, false, null)
        this.jpql = serializer.toString().trim()

        val jpaQuery = this.entityManager.createQuery(jpql, resultType)

        this.hints.forEach {
            jpaQuery.setHint(it.first, it.second)
        }

        this.entityManager.entityManagerFactory.addNamedQuery(
                // The name of the query is the parameterized query itself
                jpql,
                // Create query instance
                jpaQuery
        )
    }

    /**
     * Creates an instance of this prepared query
     * @param block Block for conveniently setting query parameters
     */
    fun create(
            entityManager: EntityManager = this.entityManager,
            block: (q: QueryParameterizer, p: P) -> Unit): QueryInstance {

        val typedQuery = entityManager.createNamedQuery(
                this@NamedQuery.jpql,
                resultType)

        val q = QueryInstance(typedQuery)

        block.invoke(
                QueryParameterizer(typedQuery),
                this@NamedQuery.params)

        return q
    }

    /**
     * Query parameterizer
     */
    inner class QueryParameterizer(
            private val query: TypedQuery<R>
    ) {
        /**
         * Set parameter on named query
         */
        fun <T> set(param: Param<T>, value: T?): QueryParameterizer {
            this.query.setParameter(
                    this@NamedQuery.metaInfoByParam[param]!!.name,
                    value)

            return this
        }
    }

    /**
     * Actual query instance/operations
     */
    inner class QueryInstance(
            private val query: TypedQuery<R>
    ) {
        fun execute(): List<R> {
            return this.query.resultList
        }
    }
}