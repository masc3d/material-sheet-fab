package org.deku.leo2.node.data.repositories.custom;

import com.google.common.collect.Lists;
import org.deku.leo2.node.data.entities.Country;
import org.deku.leo2.node.data.entities.QCountry;
import org.deku.leo2.node.data.repositories.CountryRepository;
import org.deku.leo2.node.data.repositories.RouteRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by JT on 12.05.15.
 */
public class CountryRepositoryImpl implements CountryRepositoryCustom {
    @Inject
    CountryRepository mRouteRepository;
}
