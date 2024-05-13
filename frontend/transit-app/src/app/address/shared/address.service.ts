import {Injectable} from '@angular/core';
import {catchError, map, mergeMap, Observable, of, throwError} from "rxjs";
import {environment} from "../../../environments/environment";
import {Address} from "./address";
import {HateoasResourceService} from "@lagoshny/ngx-hateoas-client";
import {DirectionService} from "../../map/shared/direction.service";
import {LocationPoint} from "../../company/shared/company";
import {Order} from "../../order/shared/order";

@Injectable({
  providedIn: 'root'
})
export class AddressService {

  constructor(private resourceHateoasService: HateoasResourceService, private directionService: DirectionService) {
  }

  createAddress(newAddress: Address): Observable<any> {

    let newLocation: LocationPoint = new LocationPoint;
    newLocation.type = "Point";
    newLocation.coordinates = [50, 50];
    newAddress.locationPoint = newLocation;

    if (newAddress.isoCode == undefined || newAddress.isoCode == "") {
      newAddress.isoCode = 'DE';
    }
    if (newAddress.country == undefined || newAddress.country == "") {
      newAddress.country = 'Germany';
    }

    let addressText: string = newAddress.street + ', ' + newAddress.zip + ' ' + newAddress.city + ', ' + newAddress.country;
    return this.directionService.getCoordinatesFromAddresse(addressText).pipe(
      map((coord: any) => {

        if (coord.features[0] != undefined) {
          if (coord.features[0].geometry != undefined) {
            //console.log(coord.features[0].geometry.coordinates);
            let newLocation: LocationPoint = new LocationPoint;
            newLocation.type = "Point";
            newLocation.coordinates = [coord.features[0].geometry.coordinates[0], coord.features[0].geometry.coordinates[1]];
            newAddress.locationPoint = newLocation;
            //    console.log(addressText);
            //   console.log(coord);
            //  console.log(newLocation.coordinates);
          }
        }
      }),
      mergeMap((coord: any) => this.resourceHateoasService.createResource(Address, {body: newAddress})),
      catchError(err => {
          console.log('caught mapping error and rethrowing', err);
          return throwError(err);
        }
      )
    )
  }

  patchAddressLocationpoint(address: Address): Observable<any> {
    return this.resourceHateoasService.patchResourceById(Address, address.id, {body: {locationPoint: address.locationPoint}});
  }

  getAddress(id: any): Observable<any> {
    return this.resourceHateoasService.getResource(Address, id);
  }




  updateAddress(newAddress: Address): Observable<any> {

    let addressText: string = newAddress.street + ', ' + newAddress.zip + ' ' + newAddress.city + ', ' + newAddress.country;
    return this.directionService.getCoordinatesFromAddresse(addressText).pipe(
      map((coord: any) => {

        if (coord.features[0] != undefined) {
          if (coord.features[0].geometry != undefined) {
            //console.log(coord.features[0].geometry.coordinates);
            let newLocation: LocationPoint = new LocationPoint;
            newLocation.type = "Point";
            newLocation.coordinates = [coord.features[0].geometry.coordinates[0], coord.features[0].geometry.coordinates[1]];
            newAddress.locationPoint = newLocation;
          }
        }
      }),
      mergeMap((coord: any) => this.resourceHateoasService.updateResource(newAddress)),
      catchError(err => {
          console.log('caught mapping error and rethrowing', err);
          return throwError(err);
        }
      )
    )
  }

  updateAddressById(key: any, newAddress: any): Observable<any> {
    let addressText: string = newAddress.street + ', ' + newAddress.zip + ' ' + newAddress.city + ', ' + newAddress.country;

    let newLocation: LocationPoint = new LocationPoint;
    newLocation.type = "Point";
    newLocation.coordinates = [50, 50];
    newAddress.locationPoint = newLocation;

    //  if(newAddress.street!=undefined && newAddress.zip!=undefined && newAddress.city!=undefined && newAddress.street!=undefined ){


    return of(0).pipe(
      mergeMap(() => this.getAddress(key)),
      map((newAddress: any) => {
        addressText = newAddress.street + ', ' + newAddress.zip + ' ' + newAddress.city + ', ' + newAddress.country;
        return addressText;
      }),
      mergeMap((addressText: any) => this.directionService.getCoordinatesFromAddresse(addressText)),
      map((coord: any) => {
        if (coord.features[0] != undefined) {
          if (coord.features[0].geometry != undefined) {
            //console.log(coord.features[0].geometry.coordinates);
            let newLocation: LocationPoint = new LocationPoint;
            newLocation.type = "Point";
            newLocation.coordinates = [coord.features[0].geometry.coordinates[0], coord.features[0].geometry.coordinates[1]];
            newAddress.locationPoint = newLocation;
          }
        }
      }),
      mergeMap((coord: any) => this.resourceHateoasService.patchResourceById(Address, key, {body: newAddress})),
      catchError(err => {
          console.log('caught mapping error and rethrowing', err);
          return throwError(err);
        }
      )
    );

    //}
    //  else{

    /*
          let addressText: string = newAddress.street + ', ' + newAddress.zip + ' ' + newAddress.city + ', ' + newAddress.country;
          return this.directionService.getCoordinatesFromAddresse(addressText).pipe(
            map((coord: any) => {

              if (coord.features[0] != undefined) {
                if (coord.features[0].geometry != undefined) {
                  //console.log(coord.features[0].geometry.coordinates);
                  let newLocation: LocationPoint = new LocationPoint;
                  newLocation.type = "Point";
                  newLocation.coordinates = [coord.features[0].geometry.coordinates[0], coord.features[0].geometry.coordinates[1]];
                  newAddress.locationPoint = newLocation;
                  console.log(addressText);
                  console.log(coord);
                  console.log(newLocation.coordinates);
                }
              }
            }),
            mergeMap((coord: any) => this.resourceHateoasService.createResource(Address, {body: newAddress})),
            catchError(err => {
                console.log('caught mapping error and rethrowing', err);
                return throwError(err);
              }
            )
          );
      }
    */

  }
}
