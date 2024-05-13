import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';

import * as MapBox from 'mapbox-gl';

import {catchError, empty, iif, map, mergeMap, Observable, of, Subscription, throwError} from 'rxjs';

import mapboxgl from 'mapbox-gl';
import {SnackbarService} from '../_core/snackbar.service';
import {KeycloakService} from 'keycloak-angular';
import {HateoasResourceService, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {KeycloakProfile} from 'keycloak-js';
import {Company, CompanyDeliveryArea} from './shared/company';
import {User, UserGetId} from '../user/shared/user';
import {ZipCodesService} from '../_core/zipCodes.service';
import {environment} from 'src/environments/environment';
import {HttpClient} from '@angular/common/http';
import {GeoJson} from '../map/shared/map';
import {MapService} from '../map/shared/map.service';
import {CompanyService} from "./shared/company.service";

declare var require: any
var polyline = require('@mapbox/polyline');


class Geometry {
  coordinates?: Array<number[]>;
  type?: string;
}


@Component({
  selector: 'app-company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.css']
})
export class CompanyComponent implements OnInit {
  editForm: UntypedFormGroup = this.fb.group({
    deliveryAreaZips: ['', Validators.required],
  });

  editFormState: UntypedFormGroup = this.fb.group({
    statesGermanyControl: ['', Validators.required],
  });

  keycloakInstance: KeycloakProfile;
  currentCompany: Company = new Company();
  currentCompanyDeliveryArea: CompanyDeliveryArea = new CompanyDeliveryArea();
  deliveryAreaZips: string | undefined = "";
  deliveryAreaPolyline: string | undefined = "";
  map: mapboxgl.Map;
  selectedAreaType: string;
  areaTypes: string[] = ['Deutschland', 'Bundesland', 'PLZ-Gebiet'];
  statesGermany: string[] = ["Berlin", "Sachsen", "Brandenburg", "Hessen", "Sachsen-Anhalt", "Baden-Württemberg", "Bayern", "Bremen", "Hamburg", "Mecklenburg-Vorpommern", "Niedersachsen", "Nordrhein-Westfalen", "Rheinland-Pfalz", "Saarland", "Schleswig-Holstein", "Thüringen"];

  constructor(
    public snackbarService: SnackbarService,
    private fb: UntypedFormBuilder,
    private keycloakService: KeycloakService,
    private resourceHateoasService: HateoasResourceService,
    private zipCodesService: ZipCodesService,
    private http: HttpClient,
    private mapService: MapService,private companyService:CompanyService
  ) {
  }

  ngOnInit(): void {

    this.statesGermany = this.statesGermany.sort();
    this.getUserData();

  }


  onSubmit(areaType: string) {
    if (areaType == 'zip') {
      if (this.editForm.valid) {
        this.updateCompany(areaType);
      }
    }
    if (areaType == 'state') {
      if (this.editFormState.valid) {
        this.updateCompany(areaType);
      }
    }
  }


  setForm() {
    /*  this.editForm = this.fb.group({
        deliveryAreaZips: [this.currentCompany.deliveryAreaZips, Validators.required],
      });*/

    if (this.currentCompany != undefined) {
      this.deliveryAreaPolyline = this.currentCompany.deliveryAreaPolyline?.toString();
      this.deliveryAreaZips = this.currentCompany.deliveryAreaZips?.toString();
    }


  }

  getUserData() {
    this.companyService.getOwnCompany().pipe(
      map((company: Company) => {
        this.currentCompany = company;
        return company;
      }),
      mergeMap((company: Company) => company.getRelation<CompanyDeliveryArea>('companyDeliveryArea')),
      map((deliveryArea: CompanyDeliveryArea) => {
        //console.log(deliveryArea);
        this.currentCompanyDeliveryArea = deliveryArea;
        this.currentCompany.deliveryAreaPolyline = deliveryArea.deliveryAreaPolyline;
        this.currentCompany.deliveryAreaGeom = deliveryArea.deliveryAreaGeom;
        this.currentCompany.deliveryAreaZips = deliveryArea.deliveryAreaZips;
      }),
      map(() => {
        this.setForm();
        this.loadMap();
      }),
    ).subscribe();
  }


  updateCompany(areaType: string) {

    const x$ = of('X');

    let selectedState: string = this.editFormState.controls["statesGermanyControl"].value
    this.zipCodesService.getZipsGermany().pipe(
      map((zipsGermany: any) => {
        let areaZips: string[] = [];

        if (areaType == 'country') {
          areaZips = zipsGermany;
        }

        /* if (areaType == 'state') {
           let selectedState: string = this.editFormState.controls["statesGermanyControl"].value
           this.zipCodesService.getZipsFromState(selectedState).subscribe((data: any) => {

             areaZips=data;
           });
         }*/

        if (areaType == 'zip') {
          let deliveryAreaZips: string = this.editForm.controls["deliveryAreaZips"].value
          areaZips = zipsGermany.filter((c: string) => c.substring(0, deliveryAreaZips.length) === deliveryAreaZips);
        }


        this.currentCompany.deliveryAreaZips = areaZips;

        return areaZips;


      }),
      //filter
      mergeMap(data => iif(() => areaType == 'state', this.zipCodesService.getZipsFromState(selectedState), x$)), // const x$ = of('X');
      map((data: any) => {
        if (areaType == 'state') {

          data.forEach((zipCodeState: any) => {
            if (zipCodeState.zipcode != undefined) {
              if (this.currentCompany != undefined) {
                if (this.currentCompany.deliveryAreaZips != undefined) {
                  if (this.currentCompany.deliveryAreaZips.indexOf(zipCodeState.zipcode) < 0) {
                    this.currentCompany.deliveryAreaZips.push(zipCodeState.zipcode);
                  }
                }
              }
            }
          });


          //this.currentCompany.deliveryAreaZips = data;
          // console.log(this.currentCompany.deliveryAreaZips);

          console.log(this.currentCompany);
        }
      }),
      mergeMap((areaZips: any) => this.http.post<string[]>(environment.geoservice + '/zips/' + 'combinepolygons', this.currentCompany.deliveryAreaZips)),
      map((data: any) => {

        //   console.log(data);
        //JSON.stringify
        if (data != undefined) {
          this.currentCompany.deliveryAreaPolyline = JSON.stringify(data);
        //  this.currentCompany.deliveryAreaGeom = JSON.stringify(data);
          this.loadMapCompanyZipAreaPolygon(this.currentCompany.deliveryAreaPolyline);
        }
        return null
      }),
      mergeMap((company: any) => this.currentCompany.getRelation<CompanyDeliveryArea>('companyDeliveryArea')),
      map((companyDeliveryArea: CompanyDeliveryArea) => {

        return companyDeliveryArea;

      }),
      mergeMap((company: any) => this.currentCompany.patchRelation(('companyDeliveryArea'), {
          body: {
            "deliveryAreaZips": this.currentCompany.deliveryAreaZips,
            "deliveryAreaPolyline": this.currentCompany.deliveryAreaPolyline,
           // "deliveryAreaGeom": this.currentCompany.deliveryAreaGeom,
          }
        }
      )),

      /*  mergeMap((companyDeliveryArea: any) => this.http.patch<any>(environment.backend + '/companies/' + this.currentCompany.id + '/deliveryarea/' + this.currentCompanyDeliveryArea.id,
          {
            "deliveryAreaZips": this.currentCompany.deliveryAreaZips,
            // "deliveryAreaGeom": this.currentCompany.deliveryAreaPolyline,
            "deliveryAreaPolyline": this.currentCompany.deliveryAreaPolyline,

          }
        )),*/
      map((zipsGermany: any) => {
        //console.log(this.currentCompany);
        this.setForm();

      })
    ).subscribe((d: any) => {
      this.snackbarService.openSnackBar('Aktualisiert', 'Ok', 'green-snackbar');
    })
  }

  loadMap() {

    this.map = new mapboxgl.Map({
      container: "map-area",
      accessToken: environment.mapbox.accessToken,
      style: "mapbox://styles/kobers/clb88rmiq001114n0x3vkgu4s",
      zoom: 6.0,
      // @ts-ignore
      center: [10.087587, 51.283556]
    });


    /// Add map controls
    this.map.addControl(new mapboxgl.NavigationControl());


    this.map.on('load', () => {

      this.map.loadImage(
        'assets/img/markers/flag-map-circle.png',
        (error, image: any) => {
          if (error) throw error;
          // Add the image to the map style.
          this.map.addImage('transit-marker', image);
        });


      // ziparea polygon source and layer for own company (should be always visible)
      this.map.addSource('ziparea-polygon-owncompany', {
        type: 'geojson',
        'data': ''
      });

      this.map.addLayer({
        'id': 'ziparea-polygon-owncompany',
        'type': 'fill',
        'source': 'ziparea-polygon-owncompany', // reference the data source
        'layout': {},
        'paint': {
          'fill-color': '#0080ff', // blue color fill
          'fill-opacity': 0.5
        }
      });


      if (this.currentCompany.deliveryAreaPolyline == undefined || this.currentCompany.deliveryAreaPolyline == "") {
        this.loadCompanyZipArea();
      } else {
        this.loadMapCompanyZipAreaPolygon(this.currentCompany.deliveryAreaPolyline);

      }


    });


  }

  loadCompanyZipArea() {
    let endpoint = "combinepolygons";
    if (this.currentCompany.deliveryAreaZips != undefined) {
      this.http.post<string[]>(environment.geoservice + '/zips/' + endpoint, this.currentCompany.deliveryAreaZips).subscribe((data: any) => {
        if (data != undefined) {
          this.loadMapCompanyZipAreaPolygon(data.polyline);
        }
      });
    }
  }



  loadMapCompanyZipAreaPolygon(companyPolyline: any) {


    let geodata: any = companyPolyline;
    //   let geodata: any = this.mapService.convertMutliLineToGeoJson(companyPolyline);

    let polygonData: any = {
      type: 'Feature',
      geometry: JSON.parse(geodata)
    }

    let polygonDataClass: GeoJson = new GeoJson();

    polygonDataClass.type = 'Feature';
    polygonDataClass.geometry = geodata;

    //console.log(polygonDataClass);

    // @ts-ignore
    this.map.getSource('ziparea-polygon-owncompany').setData(polygonData);

    // this.geojsonTest=(geodata);
  }

}
