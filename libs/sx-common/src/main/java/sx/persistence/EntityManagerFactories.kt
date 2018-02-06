package sx.persistence

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

/**
 * EntityManagerFactory extensions
 * Created by masc on 06.02.18.
 */

/**
 * Creates a new entity manager and runs the block in its context
 * @param block Block to execute
 */
fun <T> EntityManagerFactory.withEntityManager(block: (em: EntityManager) -> T): T {
    return this.createEntityManager().let { em ->
        try {
            block(em)
        } finally {
            em.close()
        }
    }
}