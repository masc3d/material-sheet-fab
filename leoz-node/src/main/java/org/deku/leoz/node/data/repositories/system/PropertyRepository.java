package org.deku.leoz.node.data.repositories.system;

import org.deku.leoz.node.data.entities.system.Property;
import org.deku.leoz.node.data.entities.system.PropertyPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;


/**
 * Created by JT on 29.06.15.
 */
public interface PropertyRepository extends JpaRepository<Property,PropertyPK>, QueryDslPredicateExecutor  {

}
