package org.deku.leo2.central;

import org.jinq.jpa.JPAQueryLogger;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Created by masc on 28.08.14.
 */
@Named
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class Persistence {
    private static AtomicReference<Persistence> mInstance = new AtomicReference<>();

    private Logger mLog = Logger.getLogger(Persistence.class.getName());

    private EntityManagerFactory mEntityManagerFactory;
    private JinqJPAStreamProvider mJinqStreamProvider;

    private boolean mShowSql = false;
    private boolean mShowJinqQueries = false;

    /**
     * c'tor
     */
    public Persistence() {
        // Normally the c'tor would be private, but spring configuration requires a publicly visible one
        // thus reverting to this "helper" singleton implementation
        Persistence previous = mInstance.getAndSet(this);
        if(previous != null)
            throw new IllegalStateException("Singleton not allowed to instantiate twice");
    }

    /**
     * Singleton accessor
     * @return
     */
    public static Persistence instance() {
        return mInstance.get();
    }

    //region Spring entity manager factory using persistence.xml
    //    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//    Properties props = new Properties();
//      if (mShowSql) {
//          // Show SQL
//          props.setProperty("eclipselink.logging.level.sql", "FINE");
//          props.setProperty("eclipselink.logging.parameters", "true");
//      }

//      return javax.persistence.Persistence.createEntityManagerFactory("leo2");
//    }
    //endregion

    //region Spring inline persistence/unit configuration
    @Autowired
    private DataSource mDataSource;

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        transactionManager.setDataSource(mDataSource);

        return transactionManager;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://10.0.10.10:3306/dekuclient");
        dataSource.setUsername("leo2");
        dataSource.setPassword("leo2");

        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("zeroDateTimeBehavior", "convertToNull");
        dataSourceProperties.setProperty("connectTimeout", "1000");
        dataSource.setConnectionProperties(dataSourceProperties);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mDataSource);
        em.setPackagesToScan(new String[]{
                "org.deku.leo2.central.entities"
        });

        JpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties eclipseLinkProperties = new Properties();
        eclipseLinkProperties.setProperty("eclipselink.allow-zero-id", "true");
        eclipseLinkProperties.setProperty("eclipselink.weaving", "false");
        em.setJpaProperties(eclipseLinkProperties);

        //mJinqStreamProvider = this.createJinqStreamProvider(em.getNativeEntityManagerFactory());

        return em;
    }
    //endregion

    //region JINQ helpers
    /**
     * Create JINQ stream provider
     * @param emf
     * @return
     */
    private JinqJPAStreamProvider createJinqStreamProvider(EntityManagerFactory emf) {
        JinqJPAStreamProvider jinqStreamProvider = new JinqJPAStreamProvider(emf);
        if (mShowJinqQueries) {
            jinqStreamProvider.setHint("queryLogger", new JPAQueryLogger() {
                @Override
                public void logQuery(String query, Map<Integer, Object> positionParameters, Map<String, Object> namedParameters) {
                    mLog.info(query);
                }
            });
        }
        return jinqStreamProvider;
    }

    /**
     * The JINQ stream provider
     * @return
     */
    public JinqJPAStreamProvider getJinqStreamProvider() {
        return mJinqStreamProvider;
    }

    /**
     * JINQ query interface for querying performing a query on instances with specific type
     * @param type Type of instance
     * @param <T>  Type of instance
     * @return
     */
    public <T> JinqStream<T> query(EntityManager em, Class<T> type) {
        return this.query(em, type, false, false);
    }

    public <T> JinqStream<T> query(EntityManager em, Class<T> type, boolean exceptionOnTranslationFail, boolean queryLogger) {
        JinqStream<T> jstream = mJinqStreamProvider.streamAll(em, type);

        if (exceptionOnTranslationFail)
            jstream.setHint("exceptionOnTranslationFail", true);

        if (queryLogger) {
            jstream.setHint("queryLogger", new JPAQueryLogger() {
                @Override
                public void logQuery(String query, Map<Integer, Object> positionParameters, Map<String, Object> namedParameters) {
                    mLog.info(query);
                }
            });
        }
        return jstream;
    }
    //endregion

    //region JPA helpers
    public interface TransactionBlock {
        void perform(EntityManager em);
    }

    /**
     * Returns a persistent instance of a specific class.
     * @param type       Type of instance
     * @param primaryKey Primary key value
     * @param <T>        Type of instance
     * @return Persistent instance of class or null on error
     */
    public <T> T persistentInstance(EntityManager em, Class<T> type, Object primaryKey) {
        T t = null;
        try {
            t = em.find(type, primaryKey);
            if (t == null) {
                t = type.newInstance();

                for (Method m : type.getMethods()) {
                    if (m.getAnnotation(javax.persistence.Id.class) != null) {
                        m = type.getMethod("s" + m.getName().substring(1), primaryKey.getClass());
                        m.invoke(t, primaryKey);
                        break;
                    }
                }

                em.persist(t);
            }
        } catch (Exception e) {
            mLog.severe(e.toString());
        }
        return t;
    }

    /**
     * Transaction block wrapper
     * @param b
     */
    public void transaction(EntityManager em, TransactionBlock b) {
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            b.perform(em);
            et.commit();
        } finally {
            if (et.isActive())
                et.rollback();
        }
    }
    //endregion

//    @Aspect
//    public class DAOInterceptor {
//        private Logger log = Logger.getLogger(DAOInterceptor.class.getName());
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

    public void dispose() {
        if (mEntityManagerFactory != null)
            mEntityManagerFactory.close();
    }
}
