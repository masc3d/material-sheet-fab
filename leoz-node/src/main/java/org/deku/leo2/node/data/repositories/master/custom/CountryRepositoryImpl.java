package org.deku.leo2.node.data.repositories.master.custom;

import org.deku.leo2.node.data.repositories.master.CountryRepository;

import javax.inject.Inject;

/**
 * Created by JT on 12.05.15.
 */
public class CountryRepositoryImpl implements CountryRepositoryCustom {
    @Inject
    CountryRepository mRouteRepository;
}
