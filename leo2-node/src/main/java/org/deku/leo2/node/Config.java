package org.deku.leo2.node;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.inject.Named;

/**
 * Created by masc on 01.06.15.
 */
@Named
@ConfigurationProperties(prefix="leo2", ignoreUnknownFields = true)
public class Config {
}
