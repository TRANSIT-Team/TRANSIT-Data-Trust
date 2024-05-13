package com.transit.geoservice.service;

import com.transit.geoservice.domain.Zipcodesgermanystates;

import java.util.List;
import java.util.Optional;

public interface ZipcodesgermanystatesInterface {

   List<Zipcodesgermanystates> getZipcodesgermanystatesByState(String name);
   Optional<Zipcodesgermanystates> getZipcodesgermanystatesByZip(String zip);
   List<Zipcodesgermanystates> getZipcodesgermanystatesByAll();

}
