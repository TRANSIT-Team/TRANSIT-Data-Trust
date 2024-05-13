import { Component, Injectable } from '@angular/core';
// @ts-ignore
import isoCodes from './isoCodes.json';

export interface CountryIsoCodes {
  name:              string;
  countryCode:      string;
  countryCodeAlpha3?: string;
  phone?:             string;
  currency?:          string;
  stateProvinces?:    StateProvince[];
}
export interface StateProvince {
  name?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CountryService {
  countryIsoCodes: CountryIsoCodes[];
  constructor() {

    this.countryIsoCodes = isoCodes;

  }

  getCountries(){
    return this.countryIsoCodes;
  }

}
