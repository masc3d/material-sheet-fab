package org.deku.leo2.central.data;

import org.deku.leo2.central.PersistenceContext;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Created by masc on 15.05.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PersistenceContext.Central.class,
        DataTest.TestConfiguration.class}, loader = AnnotationConfigContextLoader.class)
public class DataTest implements ApplicationContextAware {
    @Configuration
    @ComponentScan(basePackages = {"org.deku.leo2.central"})
    static class TestConfiguration {}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
