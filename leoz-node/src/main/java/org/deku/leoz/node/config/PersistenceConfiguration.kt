package org.deku.leoz.node.config

import org.deku.leoz.node.Storage
import org.eclipse.persistence.config.BatchWriting
import org.eclipse.persistence.config.PersistenceUnitProperties
import org.eclipse.persistence.tools.profiler.PerformanceMonitor
import org.flywaydb.core.Flyway
import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.Server
import org.jooq.SQLDialect
import org.jooq.conf.StatementType
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultDSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.*
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.io.File
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.persistence.Entity
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * Leoz-node database persistence context
 * Created by masc on 24-Jul-15.
 */
@Configuration(PersistenceConfiguration.QUALIFIER)
@ComponentScan(lazyInit = true, basePackageClasses = arrayOf(org.deku.leoz.node.data.Package::class))
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = true)
@EnableJpaRepositories(considerNestedRepositories = false, basePackageClasses = arrayOf(org.deku.leoz.node.data.Package::class))
open class PersistenceConfiguration {
    companion object {
        const val QUALIFIER = "db_embedded"
        const val QUALIFIER_JOOQ = "db_embedded_jooq"
        const val QUALIFIER_SERVER = "db_embedded_server"
    }

    private val log = LoggerFactory.getLogger(PersistenceConfiguration::class.java.name)

    /** Server properties holder */
    @Configuration
    @ConfigurationProperties(prefix = "persistence")
    open class Settings {
        class H2 {
            class Server {
                var port: Int = 0
                var enabled: Boolean = false
                var allowOthers: Boolean = false
            }

            var server = Server()
        }

        var h2 = H2()
    }

    @Inject
    private lateinit var settings: Settings
    @Inject
    private lateinit var storage: Storage

    /**
     * Enable SQL logging
     */
    private val showSql = false
    /**
     * Enable profiling
     */
    private val profiling = false

    /**
     * H2 server
     */
    private var h2Server: Server? = null

    @get:Bean
    @get:Qualifier(QUALIFIER)
    open val dataSource: DataSource
        get() {
            // Dev/debug flag for enabling h2 in memory database
            val H2_IN_MEMORY = false

            // Base URI
            val baseUri: String = if (!H2_IN_MEMORY) {
                "jdbc:h2:file:${storage.h2DatabaseFile}"
            } else {
                "jdbc:h2:mem:db1"
            }

            //region H2 setup
            val params = HashMap<String, String>()

            // Even though this is declared as "experimental" for h2 (http://www.h2database.com/javadoc/org/h2/engine/DbSettings.html#DATABASE_TO_UPPER)
            // Lowercase table/column names or much better readable in queries and schema migrations
            // TODO: enabling TO_UPPER until DataGrip/IntelliJ support disabling it properly https://youtrack.jetbrains.com/issue/DBE-3292
            // params.put("DATABASE_TO_UPPER", "false")

            // In-memory specific settings
            if (H2_IN_MEMORY) {
                params.put("INIT", "CREATE SCHEMA IF NOT EXISTS leoz")
                params.put("DB_CLOSE_DELAY", "-1")
            }
            //endregion

            // Build url and setup data source
            val dataSource = JdbcDataSource()

            dataSource.setUrl(baseUri +
                    params
                            .map { x -> "${x.key}=${x.value}" }
                            .joinToString(separator = ";", prefix = ";"))

            return JdbcConnectionPool.create(dataSource)
        }

    //region JPA
    @Bean
    @Qualifier(QUALIFIER)
    open fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = emf
        transactionManager.dataSource = this.dataSource

        return transactionManager
    }

    @Bean
    open fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
            // TODO. more robust behaviour when database is down.
            // eg. webservice fails when database is unreachable (on startup eg.)
            // more tests required referring to db outages during runtime
            val em = LocalContainerEntityManagerFactoryBean()
            em.dataSource = this.dataSource
            em.setPackagesToScan(org.deku.leoz.node.data.Package.name)

            // Setup specific jpa vendor adaptor
            val vendorAdapter = EclipseLinkJpaVendorAdapter()
            em.jpaVendorAdapter = vendorAdapter

            //region Setup eclipselink
            val eclipseLinkProperties = Properties()
            eclipseLinkProperties.setProperty(PersistenceUnitProperties.TARGET_DATABASE, org.eclipse.persistence.platform.database.H2Platform::class.java.canonicalName)

            //region Dev/debug code for automatically generating database from jpa entites
//        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.database.action", "create")
//        eclipseLinkProperties.setProperty("javax.persistence.schema-generation.create-database-schemas", "true")
            //endregion

            //region Dev/debug code for letting eclipselink/jpa generate DDL/SQL from entites
            if (false) {
                val sqlFile = File("sql/leoz-ddl.sql")
                File(sqlFile.getParent()).mkdirs()

                eclipseLinkProperties.setProperty(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, "create")
                eclipseLinkProperties.setProperty(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, sqlFile.toString())
                eclipseLinkProperties.setProperty(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPT_TERMINATE_STATEMENTS, "true")
            }
            //endregion

            //region Caching
            eclipseLinkProperties.setProperty(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true")
            //endregion

            //region Profiling
            if (this.profiling) {
                eclipseLinkProperties.setProperty(PersistenceUnitProperties.PROFILER, PerformanceMonitor::class.java.simpleName)
            }
            //endregion

            // Some master tables may have zero id values
            eclipseLinkProperties.setProperty(PersistenceUnitProperties.ID_VALIDATION, "NULL")

            // Enable jdbc batch writing
            eclipseLinkProperties.setProperty(PersistenceUnitProperties.BATCH_WRITING, BatchWriting.JDBC)
            eclipseLinkProperties.setProperty(PersistenceUnitProperties.JDBC_BIND_PARAMETERS, "true")
            eclipseLinkProperties.setProperty(PersistenceUnitProperties.CACHE_STATEMENTS, "true")

            // Weaving is required for lazy loading (amongst other features). Requires a LoadTimeWeaver to be setup (may require -javaagent as JVMARGS depending on setup)
            eclipseLinkProperties.setProperty(PersistenceUnitProperties.WEAVING, "static")

            if (showSql) {
                vendorAdapter.setShowSql(true)
                eclipseLinkProperties.setProperty(PersistenceUnitProperties.LOGGING_PARAMETERS, "true")
            }

            //region Entity setup
            // Scan and iterate entity classes
            val scanner = ClassPathScanningCandidateComponentProvider(false)
            scanner.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))

            for (bd in scanner.findCandidateComponents(org.deku.leoz.node.data.Package.name)) {
                // Setup event listeners for all entity classes
                eclipseLinkProperties.setProperty(
                        "${PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_}${bd.beanClassName}",
                        org.deku.leoz.node.data.Customizer::class.java.canonicalName)
            }
            //endregion

            em.setJpaProperties(eclipseLinkProperties)

            return em
        }
    //endregion

    //region JOOQ
    @get:Bean
    @get:Qualifier(QUALIFIER_JOOQ)
    open val jooqTransactionAwareDataSourceProxy: TransactionAwareDataSourceProxy
        get() = TransactionAwareDataSourceProxy(this.dataSource)

    @get:Bean
    @get:Qualifier(QUALIFIER_JOOQ)
    open val jooqTransactionManager: DataSourceTransactionManager
        get() = DataSourceTransactionManager(this.dataSource)

    @get:Bean
    @get:Qualifier(QUALIFIER_JOOQ)
    open val jooqConnectionProvider: DataSourceConnectionProvider
        get() = DataSourceConnectionProvider(this.jooqTransactionAwareDataSourceProxy)

    @get:Bean
    @get:Qualifier(QUALIFIER_JOOQ)
    open val dslContext: DefaultDSLContext
        get() {
            val settings = org.jooq.conf.Settings().withStatementType(StatementType.PREPARED_STATEMENT)
            return DefaultDSLContext(this.jooqConnectionProvider, SQLDialect.H2, settings)
        }
    //endregion

    @PostConstruct
    open fun onInitialize() {
        // Migrate schema
        log.info("Migrating embedded data source schema")
        val flyway = Flyway()
        flyway.dataSource = this.dataSource
        flyway.setLocations("classpath:/db/node/migration")
        flyway.isIgnoreFutureMigrations = false
        flyway.migrate()

        // Start h2 server
        val server: Server?
        if (this.settings.h2.server.enabled) {
            log.info("Starting H2 server on port [${this.settings.h2.server.port}]")

            val args = mutableListOf<String>()
            args.addAll(arrayOf("-baseDir", "${storage.h2DatabaseFile.parentFile}"))
            args.addAll(arrayOf("-tcpPort", "${this.settings.h2.server.port}"))
            if (this.settings.h2.server.allowOthers)
                args.add("-tcpAllowOthers")

            server = Server.createTcpServer(*args.toTypedArray())
            server.start()
        } else {
            server = null
        }

        this.h2Server = server
    }

    @PreDestroy
    open fun onDestroy() {
        val server = this.h2Server
        if (server != null) {
            log.info("Closing H2 server")
            server.stop()
        }
    }

    //        @Override
    //        public PlatformTransactionManager annotationDrivenTransactionManager() {
    //            return transactionManger(entityManagerFactory().getObject());
    //        }

    //        @Override
    //        public PlatformTransactionManager annotationDrivenTransactionManager() {
    //            return null;
    //        }

    //    @Aspect
    //    public class DAOInterceptor {
    //        private Logger log = Logger.getLog(DAOInterceptor.class.getName());
    //
    //        @Around("execution(* com.webforefront.jpa.service..*.*(..))")
    //        public Object logQueryTimes(ProceedingJoinPoint pjp) throws Throwable {
    //            StopWatch stopWatch = new StopWatch();
    //            stopWatch.start();
    //            Object retVal = pjp.proceed();
    //            stopWatch.stop();
    //            String str = pjp.getTarget().toString();
    //            log.info(str.substring(str.lastIndexOf(".")+1, str.lastIndexOf("@")) + " - " + pjp.getSignature().getName() + ": " + stopWatch.getTime() + "ms");
    //            return retVal;
    //        }
    //    }
}

