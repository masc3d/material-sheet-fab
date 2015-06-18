package org.deku.leo2.node.data.repositories.custom;

import org.deku.leo2.node.data.entities.QCountry;
import org.deku.leo2.node.data.repositories.CountryRepository;

import javax.inject.Inject;

/**
 * Created by JT on 12.05.15.
 */
public class CountryRepositoryImpl implements CountryRepositoryCustom {
    @Inject
    CountryRepository mRouteRepository;
}
