package com.transit.geoservice.service.impl;

import com.transit.geoservice.domain.Zipcodesgermanystates;
import com.transit.geoservice.repository.ZipcodesgermanystatesRepository;
import com.transit.geoservice.service.ZipcodesgermanystatesInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZipcodesgermanystatesInterfaceImplementation implements ZipcodesgermanystatesInterface {

    @Autowired
    private ZipcodesgermanystatesRepository zipcodesgermanystatesRepository;

    @Override
    public List<Zipcodesgermanystates> getZipcodesgermanystatesByAll() {
        return zipcodesgermanystatesRepository.findAll();
    }
    
    @Override
    public Optional<Zipcodesgermanystates> getZipcodesgermanystatesByZip(String zip) {
        return zipcodesgermanystatesRepository.findByZipcode(zip);
    }
    @Override
    public List<Zipcodesgermanystates>  getZipcodesgermanystatesByState(String name) {
        return zipcodesgermanystatesRepository.findByStateIgnoreCase(name);
    }
}
