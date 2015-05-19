package org.deku.leo2.central.data.repositories.custom;

import com.google.common.collect.Lists;
import org.deku.leo2.central.data.entities.Country;
import org.deku.leo2.central.data.entities.QCountry;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by JT on 12.05.15.
 */
public class CountryRepositoryImpl implements CountryRepositoryCustom {

    @Inject
    org.deku.leo2.central.data.repositories.CountryRepository mCountryRepository;

    @Transactional("jpa")
    public List<Country> findAll() {
        return Lists.newArrayList(mCountryRepository.findAll());
    }

//    @Transactional("jpa")
//    @Override
//    public List<Country> findWithQuery(String query) {
//        query = query.trim();
//
//        // QueryDSL
//        QCountry country = QCountry.country;
//        Iterable<Country> countries = mCountryRepository.findAll(
//                country.lkz.eq(query));
//
//        return Lists.newArrayList(countries);
//    }

//    @Transactional("jpa")
//    @Override
    public List<Country> findCountry(String query) {
        query = query.trim();

        // QueryDSL
        QCountry country = QCountry.country;
        Iterable<Country> countries= mCountryRepository.findAll(
                country.lkz.eq(query));

        return Lists.newArrayList(countries);
    }

//    public Country find(String query) {
//        query=query.trim();
//        QCountry country=QCountry.country;
//        //Country countryfound=mCountryRepository.find(country.lkz.eq(query));
//
//        Iterable<Country> countries=mCountryRepository.findOne(query);
//
//
//        Country countryfound =countries.iterator().next();
//
//        return countryfound;
//    }

}
