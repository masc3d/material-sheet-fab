package org.deku.leo2.node.data.repositories;
import org.deku.leo2.node.data.entities.ValuesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.deku.leo2.node.data.entities.Values;


/**
 * Created by JT on 17.06.15.
 */
public interface ValueRepository extends JpaRepository<Values, ValuesPK>, QueryDslPredicateExecutor {
}
