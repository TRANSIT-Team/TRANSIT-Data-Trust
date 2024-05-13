import {HttpClient} from '@angular/common/http';
import {Component, Injectable} from '@angular/core';
// @ts-ignore
//import zipCodesPoly from './zipCodesGermanyPoly.geojson';
// @ts-ignore
//import zipCodesStates from './zipCodesGermanyStates.json';


import {Feature, FeatureCollection} from "geojson";
import {map} from 'rxjs';
import {environment} from '../../environments/environment';


export class zipCodeWithPoly {
  type?: string;
  name?: string;
  crs?: CRS;
  features?: Feature[];

}

export class CRS {
  type?: string;
  properties?: Properties;
}

export class Properties {
  name?: string;
}


export class zipCodeWithState {
  country_code?: string;
  zipcode?: string;
  place?: string;
  state?: string;
  state_code?: string;
  province?: string;
  province_code?: string;
  community?: string;
  community_code?: string;
  latitude?: string;
  longitude?: string;
}

export class ZipCodeState {
  zipcode?: string;
  country_code?: string;
  place?: string;
  state?: string;
  latitude?: number;
  longitude?: number;
}


@Injectable({
  providedIn: 'root'
})


export class ZipCodesService {
  statesGermany: string[] = ["Berlin", "Sachsen", "Brandenburg", "Hessen", "Sachsen-Anhalt", "Baden-Württemberg", "Bayern", "Bremen", "Hamburg", "Mecklenburg-Vorpommern", "Niedersachsen", "Nordrhein-Westfalen", "Rheinland-Pfalz", "Saarland", "Schleswig-Holstein", "Thüringen"];


  zipCodesPoly: zipCodeWithPoly;
  zipCodesStates: zipCodeWithState[];

  constructor(private http: HttpClient,) {

    //this.zipCodesPoly = zipCodesPoly;
    this.zipCodesStates = [];

  }

  getZipsFromState(stateGermany: string) {
      return this.http.get<string[]>(environment.geoservice + '/zipstates/state/' + stateGermany);
  }



  getZipsGermany() {

    return this.http.get<ZipCodeState[]>(environment.geoservice + '/zipstates/').pipe(
      map((data: ZipCodeState[]) => {
        let zipsGermany: string[] = [];
        let ZipCodeStatesGermany: ZipCodeState[] = [];
        ZipCodeStatesGermany = data;
        ZipCodeStatesGermany.forEach((zipCodeState) => {
          if (zipCodeState.zipcode != undefined) {
            if (zipsGermany.indexOf(zipCodeState.zipcode) < 0) {
              zipsGermany.push(zipCodeState.zipcode);
            }
          }
        })
        return zipsGermany
      }),
    );
  }


}
