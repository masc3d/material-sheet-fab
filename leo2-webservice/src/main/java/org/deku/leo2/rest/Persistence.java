package org.deku.leo2.rest;

import org.jinq.jpa.JPAQueryLogger;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.jinq.orm.stream.JinqStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by masc on 28.08.14.
 */
public class Persistence {
    private static Persistence mInstance;

    private Logger mLog = Logger.getLogger(Persistence.class.getName());
    private EntityManagerFactory mEntityManagerFactory;
    private EntityManager mEntityManager;
    private JinqJPAStreamProvider mJinqStreamProvider;

    private boolean mShowSql = false;
    private boolean mShowJinqQueries = false;

    public interface TransactionBlock {
        void perform(EntityManager em);
    }

    private Persistence() {
    }

    public static Persistence instance() {
        if (mInstance == null) {
            synchronized (Persistence.class) {
                mInstance = new Persistence();
            }
        }
        return mInstance;
    }

    private EntityManagerFactory createLocalEntityManagerFactory() {
        Properties props = new Properties();
//
//        File dbPath = new File(Config.getLocalHomeDirectory(), "db/movista");
//        props.setProperty("javax.persistence.jdbc.url", "jdbc:h2:file:" + dbPath.toString());
//        props.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
//
//        props.setProperty("javax.persistence.schema-generation.database.action", "create");
//        props.setProperty("javax.persistence.schema-generation.create-database-schemas", "true");
//
//        props.setProperty("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform");
//        props.setProperty("eclipselink.jdbc.batch-writing", "JDBC");
//
//        if (mShowSql) {
//            // Show SQL
//            props.setProperty("eclipselink.logging.level.sql", "FINE");
//            props.setProperty("eclipselink.logging.parameters", "true");
//        }

        return javax.persistence.Persistence.createEntityManagerFactory("leo2", props);
    }

    public void initialize() {
        if (mEntityManagerFactory != null)
            throw new RuntimeException("Persistence singleton already initialized");

        mEntityManagerFactory = this.createLocalEntityManagerFactory();
        mEntityManager = mEntityManagerFactory.createEntityManager();

        mJinqStreamProvider = new JinqJPAStreamProvider(mEntityManagerFactory);
        if (mShowJinqQueries) {
            mJinqStreamProvider.setHint("queryLogger", new JPAQueryLogger() {
                @Override
                public void logQuery(String query, Map<Integer, Object> positionParameters, Map<String, Object> namedParameters) {
                    mLog.info(query);
                }
            });
        }
        //mJinqStreamProvider.setHint("exceptionOnTranslationFail", true);
    }

    /**
     * Persistence entitiy manager
     *
     * @return
     */
    public EntityManager getEntityManager() {
        return mEntityManager;
    }

    /**
     * The JINQ stream provider
     *
     * @return
     */
    public JinqJPAStreamProvider getJinqStreamProvider() {
        return mJinqStreamProvider;
    }

    /**
     * JINQ query interface for querying performing a query on instances with specific type
     *
     * @param type Type of instance
     * @param <T>  Type of instance
     * @return
     */
    public <T> JinqStream<T> query(Class<T> type) {
        return this.query(type, false, false);
    }

    public <T> JinqStream<T> query(Class<T> type, boolean exceptionOnTranslationFail, boolean queryLogger) {
        JinqStream<T> jstream = mJinqStreamProvider.streamAll(mEntityManager, type);

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

    /**
     * Returns a persistent instance of a specific class.
     *
     * @param type       Type of instance
     * @param primaryKey Primary key value
     * @param <T>        Type of instance
     * @return Persistent instance of class or null on error
     */
    public <T> T persistentInstance(Class<T> type, Object primaryKey) {
        T t = null;
        try {
            t = this.getEntityManager().find(type, primaryKey);
            if (t == null) {
                t = type.newInstance();

                for (Method m : type.getMethods()) {
                    if (m.getAnnotation(javax.persistence.Id.class) != null) {
                        m = type.getMethod("s" + m.getName().substring(1), primaryKey.getClass());
                        m.invoke(t, primaryKey);
                        break;
                    }
                }

                this.getEntityManager().persist(t);
            }
        } catch (Exception e) {
            mLog.severe(e.toString());
        }
        return t;
    }

    /** Transaction block wrapper
     * @param b
     */
    public void transaction(TransactionBlock b) {
        EntityTransaction et = this.getEntityManager().getTransaction();
        try {
            et.begin();
            b.perform(this.getEntityManager());
            et.commit();
        } finally {
            if (et.isActive())
                et.rollback();
        }
    }

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
        if (mEntityManager != null)
            mEntityManager.close();
        if (mEntityManagerFactory != null)
            mEntityManagerFactory.close();
    }
}
