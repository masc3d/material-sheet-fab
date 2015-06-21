package org.deku.leo2.node.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinq.jpa.JPAQueryLogger;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.jinq.orm.stream.JinqStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by masc on 14.05.15.
 */
public class PersistenceUtil {
    private Log mLog = LogFactory.getLog(PersistenceContext.class);

    private JinqJPAStreamProvider mJinqStreamProvider;
    private boolean mShowJinqQueries = false;

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
        void perform() throws Exception;
    }

    /**
     * Returns a persistent instance of a specific class.
     * @param type       Type of instance
     * @param primaryKey Primary key value
     * @param <T>        Type of instance
     * @return Persistent instance of class or null on error
     */
    public static <T> T persistentInstance(EntityManager em, Class<T> type, Object primaryKey) throws Exception {
        T t = null;
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
        return t;
    }

    /**
     * Transaction block wrapper
     * @param b
     */
    public static void transaction(EntityManager em, TransactionBlock b) {
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            b.perform();
            et.commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (et.isActive())
                et.rollback();
        }
    }
    //endregion
}
