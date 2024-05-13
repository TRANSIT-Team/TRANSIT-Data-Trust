import {Component, OnInit} from '@angular/core';

import * as XLSX from 'xlsx';
import {Order} from "../order/shared/order";
import {Company, GlobalCompanyProperty, LocationPoint} from "../company/shared/company";
import {UniqueResource} from "../_core/AbstractResource";
import {catchError, map, mergeMap, throwError} from "rxjs";
import {User, UserGetId} from "../user/shared/user";
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {KeycloakService} from "keycloak-angular";
import {KeycloakProfile} from "keycloak-js";
import {importOrder} from "./shared/import-data";
import {SnackbarService} from "../_core/snackbar.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Address} from "../address/shared/address";


@Component({
  selector: 'app-import-data',
  templateUrl: './import-data.component.html',
  styleUrls: ['./import-data.component.css']
})
export class ImportDataComponent implements OnInit {
  file: File | undefined;
  uploadReady = false;
  importData: any = [];

  currentProcessed = 1;
  uploadStarted = false;
  keycloakInstance: KeycloakProfile;

  orders: importOrder[] = [];
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];

  constructor(private hateoasResourceService: HateoasResourceService,
              private keycloakService: KeycloakService,
              public snackbarService: SnackbarService,
              private router: Router,
              private route: ActivatedRoute,
              ) {
  }

  ngOnInit(): void {
    // @ts-ignore
    this.keycloakInstance = this.keycloakService.getKeycloakInstance().profile;
  }

  onFileChange(event: any) {
    this.file = event.target.files[0];
    this.processExcel();
  }

  getUniqueId(parts: number): string {
    const stringArr = [];
    for (let i = 0; i < parts; i++) {
      // tslint:disable-next-line:no-bitwise
      const S4 = (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
      stringArr.push(S4);
    }
    return stringArr.join('-');
  }

  processExcel() {
    if (!this.file) return;

    const reader: FileReader = new FileReader();
    reader.onload = (e: any) => {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, {type: 'array'});

      const sheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[sheetName];


      let imports = XLSX.utils.sheet_to_json(worksheet, {raw: true});


      this.importData = imports.map((row: any) => {
        // Assuming 'dateColumn' is the name of your date column


        if (typeof row.pickupDate === 'number') {
          const excelDateNumber = row.pickupDate;

          // Adjust the date from Excel's epoch to JavaScript's epoch (Unix epoch)
          const unixEpochDiff = 25569; // Number of days between Excel and Unix epochs
          const millisecondsPerDay = 24 * 60 * 60 * 1000;
          const excelMilliseconds = (excelDateNumber - unixEpochDiff) * millisecondsPerDay;

          // Create a JavaScript Date object
          row.pickupDate = new Date(excelMilliseconds);
        }
        if (typeof row.destinationDate === 'number') {
          const excelDateNumber = row.destinationDate;

          // Adjust the date from Excel's epoch to JavaScript's epoch (Unix epoch)
          const unixEpochDiff = 25569; // Number of days between Excel and Unix epochs
          const millisecondsPerDay = 24 * 60 * 60 * 1000;
          const excelMilliseconds = (excelDateNumber - unixEpochDiff) * millisecondsPerDay;

          // Create a JavaScript Date object
          row.destinationDate = new Date(excelMilliseconds);
        }
        if (typeof row.toZip === 'number') {
          //row.fromZip = String(row.fromZip); // Convert ZIP code to string
          row.fromZip = row.toZip.toString().padStart(5, '0');// Convert ZIP code to string
        }
        if (typeof row.fromZip === 'number') {
          row.fromZip = row.fromZip.toString().padStart(5, '0');// Convert ZIP code to string
        }

        return row;
      });


      if (this.importData.length > 0) {
        this.uploadReady = true;
      } else {
        this.uploadReady = false;
      }


      console.log(this.importData);
      this.orders = [];
      this.orders = this.convertToOrders();
      console.log(this.orders);
    };

    reader.readAsArrayBuffer(this.file);

  }

  convertToOrders(): any {
    let importOrders: importOrder[] = [];
    this.importData.forEach((dataRow: any) => {

      let newFromAddress: Address = new Address();
      newFromAddress.addressExtra = dataRow.fromAddressExtra;
      newFromAddress.street = dataRow.fromStreet;
      newFromAddress.city = dataRow.fromCity;
      newFromAddress.zip = dataRow.fromZip;
      newFromAddress.state = dataRow.fromState;
      newFromAddress.country = dataRow.fromCountry;
      newFromAddress.companyName = dataRow.fromCompany;
      newFromAddress.clientName = dataRow.fromName;

      //test values
      newFromAddress.isoCode = 'DE';
      let newLocation: LocationPoint = new LocationPoint;
      newLocation.type = "Point";
      newLocation.coordinates = [50, 50];
      newFromAddress.locationPoint = newLocation;


      let newToAddress: Address = new Address();
      newToAddress.isoCode = 'DE';
      newToAddress.addressExtra = dataRow.toAddressExtra;
      newToAddress.street = dataRow.toStreet;
      newToAddress.city = dataRow.toCity;
      newToAddress.zip = dataRow.toZip;
      newToAddress.state = dataRow.toState;
      newToAddress.country = dataRow.toCountry;
      newToAddress.companyName = dataRow.toCompany;
      newToAddress.clientName = dataRow.toName;

      //test values
      newToAddress.locationPoint = newLocation;

      let newOrder: importOrder = new importOrder();

      newOrder.id = this.getUniqueId(3);
      newOrder.addressIdFrom = new UniqueResource();
      newOrder.addressFrom = newFromAddress;
      newOrder.addressIdTo = new UniqueResource();
      newOrder.addressTo = newToAddress;


      newOrder.companyId = new Company();
      newOrder.orderStatus = 'OPEN';

      newOrder.pickUpDate = dataRow.pickupDate;
      newOrder.destinationDate = dataRow.destinationDate;

      importOrders.push(newOrder);
    });

    return importOrders;
  }


  uploadOrders() {
    this.uploadStarted = true;
    let orders: Order[]=[];


    this.orders.forEach((order) => {
      let o: Order = new Order();
      o = order;
      o.id = "";
      orders.push(o);
    })
    let kcUserId: any = this.keycloakInstance.id;

    orders.forEach((newOrder: Order) => {


      //console.log(newOrder);

      this.hateoasResourceService.createResource(Address, {body: newOrder.addressFrom!}).pipe(
        map((responseNewAddress: any) => {
          if (newOrder.addressIdFrom) {
            newOrder.addressIdFrom.id = responseNewAddress.id;
          }
          return null;
        }),
        mergeMap(() => this.hateoasResourceService.createResource(Address, {body: newOrder.addressTo!})),
        map((responseNewAddress: any) => {
          if (newOrder.addressIdTo) {
            newOrder.addressIdTo.id = responseNewAddress.id;
          }
          return null;
        }),
        mergeMap(() => this.hateoasResourceService.getResource(UserGetId, kcUserId, {})),
        map((userId: any) => {
          return userId;
        }),
        mergeMap((userId: any) => this.hateoasResourceService.getResource(User, userId.userId, {})),
        map((user: any) => {

          if (newOrder.companyId) {
            newOrder.companyId.id = user.companyId;
            console.log(user.companyId);
          }
          return user;
        }),
        mergeMap(() => this.hateoasResourceService.createResource(Order, {body: newOrder})),
        map((order: Order) => {

          this.currentProcessed++;
          if (this.currentProcessed > orders.length) {
            this.currentProcessed = orders.length;
            this.uploadStarted = false;
            this.snackbarService.openSnackBar('Aufträge importiert.', 'Ok', 'green-snackbar');
            this.router.navigate(['/demo/orders'], {relativeTo: this.route});
          }
          return order;
        }),
        catchError(err => {
            console.log('caught mapping error and rethrowing', err);
            this.uploadStarted = false;
            return throwError(err);
          }
        )
      ).subscribe((() => {
        this.uploadStarted = false;
      }));

    });


  }


  insertRow(event: any) {

  }


  updateRow(event: any) {

    let tmpRowData = event.oldData;
    tmpRowData.modifyDate = null;
    tmpRowData.createDate = null;
    tmpRowData.lastModifiedBy = null;
    tmpRowData.createdBy = null;

    for (let key in event.newData) {
      tmpRowData[key] = event.newData[key];
    }


  }

  deleteRow(event: any) {

  }


}
