import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {
  Company,
  CompanyProperty,
  GlobalCompanyProperty,
  LocationPoint,
  CompanyAddress,
  CompanyDeliveryArea
} from '../../company/shared/company';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HateoasResourceService, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {Router} from "@angular/router";
import {KeycloakService} from 'keycloak-angular';
import {User, UserGetId, UserProperty, UserRegistration} from '../shared/user';
import {KeycloakProfile} from 'keycloak-js';
import {CountryIsoCodes, CountryService} from '../../_core/isoCodes';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {catchError, map, mergeMap, Observable, throwError} from 'rxjs';
import {DirectionService} from 'src/app/map/shared/direction.service';
import {SnackbarService} from 'src/app/_core/snackbar.service';
import {environment} from 'src/environments/environment';
import {ZipCodesService} from 'src/app/_core/zipCodes.service';


import {MapService} from '../../map/shared/map.service';
import {GeoJson} from '../../map/shared/map';

import * as MapBox from 'mapbox-gl';

import {empty, iif, of, Subscription,} from 'rxjs';

import mapboxgl from 'mapbox-gl';
import {ViewportScroller} from '@angular/common';


declare var require: any
var polyline = require('@mapbox/polyline');

import {Globals} from 'src/app/_core/globals';
import {zipObject} from "lodash";
import {CompanyService} from "../../company/shared/company.service";
import {PropertyResource} from "../../_core/AbstractResource";
import {Address} from "../../address/shared/address";
import {AuthService} from "../../_core/auth.service";
import {AddressService} from "../../address/shared/address.service";
import {FunctionsService} from "../../_core/functions.service";

class Geometry {
  coordinates?: Array<number[]>;
  type?: string;
}


@Component({
  selector: 'app-company-settings',
  templateUrl: './company-settings.component.html',
  styleUrls: ['./company-settings.component.css']
})
export class CompanySettingsComponent implements OnInit {
  sendingData = false;
  editClassItemProperies: CompanyProperty[] = [];
  selectedClassItemProperties: CompanyProperty[];
  dataItemForm: UntypedFormGroup;
  keycloakInstance: KeycloakProfile;
  countriesWithIso: CountryIsoCodes[];
  selectedCountriesWithIso: any = '';
  selectedCountry: any = '';
  companyId: any = '';
  selectedCountriesWithIsoIndex: any = 1;
  selectedGender: any = '';
  currentUser: User = new User();
  currentUserId: string = "";
  currentCompany: Company;
  currentDeliveryAreaCompany: Company = new Company();
  mainAddress: CompanyAddress = new CompanyAddress();
  alertMessage: string = '';
  alertDisplay: boolean = false;
  globalCompanyProperties: GlobalCompanyProperty[] = [];

  genders: any[] = [{key: 'w', value: 'weiblich'}, {key: 'm', value: 'männlich'}, {key: 'd', value: 'keine Angabe'}];
  editForm: UntypedFormGroup = this.fb.group({
    deliveryAreaZips: ['', Validators.required],
  });

  editFormState: UntypedFormGroup = this.fb.group({
    statesGermanyControl: ['', Validators.required],
  });

  currentCompanyDeliveryArea: CompanyDeliveryArea = new CompanyDeliveryArea();
  deliveryAreaZips: string | undefined = "";
  deliveryAreaPolyline: string | undefined = "";
  map: mapboxgl.Map;
  selectedAreaType: string;
  areaTypes: string[] = ['Deutschland', 'Bundesland', 'PLZ-Gebiet'];
  statesGermany: string[] = ["Berlin", "Sachsen", "Brandenburg", "Hessen", "Sachsen-Anhalt", "Baden-Württemberg", "Bayern", "Bremen", "Hamburg", "Mecklenburg-Vorpommern", "Niedersachsen", "Nordrhein-Westfalen", "Rheinland-Pfalz", "Saarland", "Schleswig-Holstein", "Thüringen"];


  constructor(private _snackBar: MatSnackBar, public authService: AuthService,
              private _router: Router,
              private resourceHateoasService: HateoasResourceService,
              private fb: UntypedFormBuilder,
              private keycloakService: KeycloakService,
              private directionService: DirectionService,
              private countrieService: CountryService,
              private snackbarService: SnackbarService,
              private zipCodesService: ZipCodesService,
              private http: HttpClient,
              private mapService: MapService,
              private viewportScroller: ViewportScroller,
              private router: Router,
              private globals: Globals,
              private companyService: CompanyService,
              private addressService: AddressService,
              private functionsService: FunctionsService,
  ) {
  }

  ngOnInit() {

    this.setForm();
    this.getData();

    this.statesGermany = this.statesGermany.sort();
    this.getUserData();
  }


  scrollTo() {
    if (this.globals.scrollToSingle != "") {
      //  console.log(this.globals.scrollToSingle);

      setTimeout(() => {                           // <<<---using ()=> syntax
        this.viewportScroller.scrollToAnchor(this.globals.scrollToSingle);
        this.globals.scrollToSingle = "";
      }, 500);


    }
  }

  setForm() {

    if (this.currentCompany == undefined) {
      // @ts-ignore
      this.keycloakInstance = this.keycloakService.getKeycloakInstance().profile;

      this.countriesWithIso = this.countrieService.getCountries();
      this.countriesWithIso.sort((a: any, b: any) => a.name.localeCompare(b.name));

      this.dataItemForm = this.fb.group({
        userGender: ['', Validators.required],
        userJobPostion: ['', Validators.required],
        companyName: ['', Validators.required],
        companyCity: ['', Validators.required],
        companyZip: ['', Validators.required],
        companyAddress: ['', Validators.required],
        companyState: ['', Validators.required],
        companyCountry: [this.selectedCountriesWithIso, Validators.required],
        companyComment: ['',],
        companyProperties: this.fb.array([])
      });
    } else {


      // console.log(this.currentUser);
      //const website: any = this.currentCompany.companyProperties?.filter(e => e.key == "Website")[0]['value'];
      const website: any = "";
      let addresses: any = this.currentCompany.companyAddresses;

      Object.entries(addresses).forEach(([key, value], index) => {
        if (addresses[index].addressType === 'Hauptanschrift') {
          this.mainAddress = addresses[index];
        }
      });

      if (this.mainAddress.addressType == null && addresses.length > 0) {
        this.mainAddress = addresses[0];
      }

      this.dataItemForm = this.fb.group({
        companyName: [this.currentCompany.name, Validators.required],
        companyCity: [this.mainAddress.city, Validators.required],
        companyZip: [this.mainAddress.zip, Validators.required],
        companyAddress: [this.mainAddress.street, Validators.required],
        companyState: [this.mainAddress.state, Validators.required],
        companyCountry: [this.mainAddress.isoCode, Validators.required],
        companyComment: [this.mainAddress.addressExtra,],
        companyProperties: this.fb.array([])
      });

      this.selectedCountry = this.mainAddress.country;
      this.selectedCountriesWithIso = this.mainAddress.isoCode

      this.globalCompanyProperties.forEach((p) => {
        if (p.name != "default") {

          const existingPropertyNames = this.companyProperties().controls.map(cp => cp.value.name);

          if (p.name !== "default" && !existingPropertyNames.includes(p.name)) {
            this.companyProperties().push(this.editCompanyProperty(p.id, p.name, p.value));
          }
        }
      });


    }
  }

  changeCountry(country: any) {
    this.selectedCountriesWithIso = country
    this.selectedCountry = this.countriesWithIso.filter(i => i.countryCode.includes(country));
  }

  onSubmit() {
    // console.log('onSubmit');
    if (this.dataItemForm.valid) {
      this.submitSettings();
    }
  }


  getData() {

    this.authService.getUser().pipe(
      map((user: any) => {
        this.currentUser = user;
        let companyId: any = user.companyId;
        this.companyId = companyId;
        return user;
      }),
      mergeMap((user: any) => this.companyService.getOwnCompany()),
      map((company: Company) => {
        this.currentCompany = company;
        return company;
      }),
      mergeMap((company: Company) => company.getRelatedCollection<ResourceCollection<CompanyAddress>>('companyAddresses')),
      map((companyAddresses: ResourceCollection<CompanyAddress>) => {
        this.currentCompany.companyAddresses = [];
        Object.entries(companyAddresses.resources).forEach(([key, value], index) => {
          this.currentCompany.companyAddresses?.push(companyAddresses.resources[index])
        });

      }),
      mergeMap(() => this.companyService.getGlobalCompanyProperties()),
      map((collection: ResourceCollection<GlobalCompanyProperty>) => {
        this.globalCompanyProperties = collection.resources;

        this.currentCompany.companyProperties?.forEach((p) => {
          let gPtemp: GlobalCompanyProperty = this.globalCompanyProperties.filter((gP: GlobalCompanyProperty) => gP.name == p.key)[0];
          if (gPtemp != undefined) {
            gPtemp.value = p.value;
          }
        });

      }),
    ).subscribe(() => {
      this.setForm();
      this.scrollTo();
    });
  }

  companyProperties(): UntypedFormArray {
    return this.dataItemForm.get('companyProperties') as UntypedFormArray;
  }

  editCompanyProperty(k?: string, n?: string, val?: string): UntypedFormGroup {
    return this.fb.group({
      key: k,
      name: n,
      value: val,
      disabled: false
    });
  }

  submitSettings() {
    let error: boolean = false;
    this.alertDisplay = false;

    let updateCompany: Company = this.currentCompany;
    if (this.currentCompany == undefined) {
      updateCompany = new Company();

    } else {

    }

    let updateUser: User = this.currentUser;

    updateUser.lastModifiedBy = undefined;
    updateUser.createDate = undefined;
    updateUser.createdBy = undefined;
    updateUser.modifyDate = undefined;
    updateCompany.lastModifiedBy = undefined;
    updateCompany.createDate = undefined;
    updateCompany.createdBy = undefined;
    updateCompany.modifyDate = undefined;

    updateUser.keycloakId = this.keycloakService.getKeycloakInstance()?.profile?.id;
    updateUser.realmRoles = ['ownerCompany', 'adminCompany'];


    updateCompany.name = this.dataItemForm.controls["companyName"].value;
    updateCompany.companyProperties = [];
    let cp: any = this.dataItemForm.controls["companyProperties"];
    cp.controls.forEach((p: any) => {

      if (p.value != undefined) {
        if (p.value.value != undefined) {
          if (p.value.value != "") {
            let companyProperty: PropertyResource = new PropertyResource();
            companyProperty.key = p.value.name;
            companyProperty.value = p.value.value;
            companyProperty.type = "text";
            if (p.value.name == "Email") {
              if (!this.functionsService.isValidEmail(p.value.value)) {
                this.snackbarService.openSnackBar('Tragen Sie eine gültige E-Mail ein.', 'Ok', 'red-snackbar');
                error = true;
              }
            }
            updateCompany.companyProperties!.push(companyProperty);
          }
        }
      }
    })


    let checkDefault: any = updateCompany.companyProperties.filter((a) => a.key == "default")[0];

    if (checkDefault) {
      checkDefault.value = "default";
      checkDefault.type = "default";
    } else {
      let companyProperty: PropertyResource = new PropertyResource();
      companyProperty.key = "default";
      companyProperty.value = "default";
      companyProperty.type = "default";
      updateCompany.companyProperties!.push(companyProperty);
    }

    let companyAddress: Address = new Address();
    //companyAddress.addressType = "Hauptanschrift";
    companyAddress.id = this.mainAddress.id;
    companyAddress.street = this.dataItemForm.controls["companyAddress"].value;
    companyAddress.zip = this.dataItemForm.controls["companyZip"].value;
    companyAddress.city = this.dataItemForm.controls["companyCity"].value;
    companyAddress.state = this.dataItemForm.controls["companyState"].value;
    companyAddress.country = this.selectedCountry;
    companyAddress.isoCode = this.selectedCountriesWithIso;
    companyAddress.addressExtra = this.dataItemForm.controls["companyComment"].value;

    let companyLocation: LocationPoint = new LocationPoint;
    companyLocation.type = "Point";
    companyLocation.coordinates = [50, 50];
    companyAddress.locationPoint = companyLocation;


    if (!error) {

      this.sendingData = true;

      of(0).pipe(
        mergeMap((user: any) => this.resourceHateoasService.updateResourceById(Company, this.currentCompany.id, {
            body: updateCompany
          }
        )),
        catchError((err, caught) => {
          error = true;
          this.errorHandling(err);
          return throwError(err);
        }),
        mergeMap((user: any) => this.addressService.updateAddressById(this.mainAddress.id, companyAddress)),
        catchError((err, caught) => {
          error = true;
          this.errorHandling(err);
          return throwError(err);
        }),
        map((c: any) => {
          if (!error) {

            this.snackbarService.openSnackBar('Aktualisiert', 'Ok', 'green-snackbar');
          } else {

          }
          this.sendingData = false;
        }),
        catchError((err, caught) => {
          error = true;

          this.snackbarService.openSnackBar('Ein Fehler ist aufgetreten', 'Ok', 'red-snackbar');
          this.errorHandling(err);
          this.sendingData = false;
          return throwError(err);
        }),
      ).subscribe();
    }

  }


  errorHandling(err: any) {


    this.alertMessage = 'Es ist ein Fehler aufgetreten. Bitte versuchen Sie es erneut.';

    console.log(err);
    if (err != null) {
      if (err.error != null) {


        if (err.error != null) {


        } else {
          this.alertMessage = '';


          Object.entries(err.error.errors).forEach(([key, value], index) => {
            this.alertMessage += err.error.errors[index].message + ' '
          });

          this._snackBar.open("Ein Fehler ist aufgetreten.", "Ok", {
            duration: 3000,
          });

        }
      }

    }
    this.alertDisplay = true;
  }


  onDeliveryAreaSubmit(areaType: string) {
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


  setMapForm() {
    /*  this.editForm = this.fb.group({
        deliveryAreaZips: [this.currentCompany.deliveryAreaZips, Validators.required],
      });*/

    if (this.currentDeliveryAreaCompany != undefined) {
      this.deliveryAreaPolyline = this.currentDeliveryAreaCompany.deliveryAreaPolyline?.toString();
      this.deliveryAreaZips = this.currentDeliveryAreaCompany.deliveryAreaZips?.toString();
    }


  }

  getUserData() {

    this.authService.getUser().pipe(
      map((user: any) => {
        let companyId: any = user.companyId;
        return companyId;
      }),
      mergeMap((companyId: any) => this.companyService.getOwnCompany()),
      map((company: Company) => {
        this.currentDeliveryAreaCompany = company;
        return company;
      }),
      mergeMap((company: Company) => company.getRelation<CompanyDeliveryArea>('companyDeliveryArea')),
      map((deliveryArea: CompanyDeliveryArea) => {

        this.currentCompanyDeliveryArea = deliveryArea;
        if (deliveryArea.deliveryAreaPolyline != undefined) {
          this.currentDeliveryAreaCompany.deliveryAreaPolyline = deliveryArea.deliveryAreaPolyline;
        } else {
          this.currentDeliveryAreaCompany.deliveryAreaPolyline = "";
        }

        if (deliveryArea.deliveryAreaGeom != undefined) {
          this.currentDeliveryAreaCompany.deliveryAreaGeom = deliveryArea.deliveryAreaGeom;
        } else {
          this.currentDeliveryAreaCompany.deliveryAreaGeom = "";
        }

        if (deliveryArea.deliveryAreaZips != undefined) {
          this.currentDeliveryAreaCompany.deliveryAreaZips = deliveryArea.deliveryAreaZips;
        } else {
          this.currentDeliveryAreaCompany.deliveryAreaZips = [];
        }
      }),
      map(() => {
        this.setMapForm();
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
            let deliveryAreaZips: string = this.editForm.controls["deliveryAreaZips"].value;

            let sep: string = '';
            // Check if the deliveryAreaZips contains a comma
            if (deliveryAreaZips.includes(',')) {
              sep = ',';
            }
            if (deliveryAreaZips.includes('.')) {
              sep = '.';
            }
            if (deliveryAreaZips.includes(';')) {
              sep = ';';
            }
            if (sep != '') {
              const zips: string[] = deliveryAreaZips.split(sep).map(item => item.trim());
              zips.forEach((zip: string) => {
                areaZips.push(...zipsGermany.filter((c: string) => c.substring(0, zip.length) === zip));
              });
            } else {
              areaZips = zipsGermany.filter((c: string) => c.substring(0, deliveryAreaZips.length) === deliveryAreaZips);
            }
          }

          this.currentDeliveryAreaCompany.deliveryAreaZips = areaZips;

          return areaZips;


        }
      ),
      //filter
      mergeMap(data => iif(() => areaType == 'state', this.zipCodesService.getZipsFromState(selectedState), x$)), // const x$ = of('X');
      map((data: any) => {
        if (areaType == 'state') {

          data.forEach((zipCodeState: any) => {
            if (zipCodeState.zipcode != undefined) {
              if (this.currentDeliveryAreaCompany != undefined) {
                if (this.currentDeliveryAreaCompany.deliveryAreaZips != undefined) {
                  if (this.currentDeliveryAreaCompany.deliveryAreaZips.indexOf(zipCodeState.zipcode) < 0) {
                    this.currentDeliveryAreaCompany.deliveryAreaZips.push(zipCodeState.zipcode);
                  }
                }
              }
            }
          });


          //this.currentCompany.deliveryAreaZips = data;
          // console.log(this.currentCompany.deliveryAreaZips);

          //console.log(this.currentDeliveryAreaCompany.deliveryAreaZips);
        }
      }),
      mergeMap((areaZips: any) => iif(() => this.currentDeliveryAreaCompany.deliveryAreaZips != null, this.http.post<string[]>(environment.geoservice + '/zips/' + 'combinepolygons', this.currentDeliveryAreaCompany.deliveryAreaZips), of(0))),
      map((data: any) => {

        // console.log(data);
        //JSON.stringify
        if (data != undefined) {
          this.currentDeliveryAreaCompany.deliveryAreaPolyline = JSON.stringify(data);
          //  this.currentCompany.deliveryAreaGeom = JSON.stringify(data);
          this.loadMapCompanyZipAreaPolygon(this.currentDeliveryAreaCompany.deliveryAreaPolyline, true);

          this.deliveryAreaPolyline = this.currentDeliveryAreaCompany.deliveryAreaPolyline;
        }
        return null
      }),
      mergeMap((company: any) => this.currentDeliveryAreaCompany.getRelation<CompanyDeliveryArea>('companyDeliveryArea')),
      map((companyDeliveryArea: CompanyDeliveryArea) => {

        return companyDeliveryArea;

      }),
      mergeMap((company: any) => this.currentDeliveryAreaCompany.patchRelation(('companyDeliveryArea'), {
          body: {
            "deliveryAreaZips": this.currentDeliveryAreaCompany.deliveryAreaZips,
            "deliveryAreaPolyline": this.currentDeliveryAreaCompany.deliveryAreaPolyline,
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
      //  console.log(this.currentDeliveryAreaCompany);
        this.setForm();

      })
    ).subscribe((d: any) => {
      this.snackbarService.openSnackBar('Aktualisiert', 'Ok', 'green-snackbar');
    })
  }

  loadMap() {

    this.map = new mapboxgl.Map({
      container: "map-deliveryArea",
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


      if (this.currentDeliveryAreaCompany.deliveryAreaPolyline == undefined || this.currentDeliveryAreaCompany.deliveryAreaPolyline == "") {
        this.loadCompanyZipArea();
      } else {
        this.loadMapCompanyZipAreaPolygon(this.currentDeliveryAreaCompany.deliveryAreaPolyline, true);

      }


    });


  }

  loadCompanyZipArea() {
    let endpoint = "combinepolygons";
    //console.log(this.currentDeliveryAreaCompany.deliveryAreaZips);
    if (this.currentDeliveryAreaCompany.deliveryAreaZips != undefined) {
      if (this.currentDeliveryAreaCompany.deliveryAreaZips.length > 0) {
        this.http.post<string[]>(environment.geoservice + '/zips/' + endpoint, this.currentDeliveryAreaCompany.deliveryAreaZips).subscribe((data: any) => {
          if (data != undefined) {
            this.loadMapCompanyZipAreaPolygon(data, false);
          }
        });
      }
    }
  }


  loadMapCompanyZipAreaPolygon(companyPolyline
                                 :
                                 any, parse
                                 :
                                 boolean = true
  ) {


    let geodata: any = companyPolyline;
    //   let geodata: any = this.mapService.convertMutliLineToGeoJson(companyPolyline);


    if (parse) {

      geodata = JSON.parse(geodata);

    }


    let polygonData: any = {
      type: 'Feature',
      geometry: geodata
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
