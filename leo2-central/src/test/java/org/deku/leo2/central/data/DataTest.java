package org.deku.leo2.central.data;

import org.deku.leo2.central.PersistenceContext;
import org.deku.leo2.central.rest.services.v1.RoutingService;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Created by masc on 15.05.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceContext.class, RoutingService.class}, loader = AnnotationConfigContextLoader.class)
@ComponentScan
public class DataTest {
}
