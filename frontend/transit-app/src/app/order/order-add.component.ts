import {FormControl, FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Component, ElementRef, OnInit, Output, Renderer2, ViewChild} from '@angular/core';
import {NgxMapboxGLModule} from 'ngx-mapbox-gl';
import {catchError, empty, map, mergeMap, Observable, of, Subscription, throwError} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {DirectionService} from '../map/shared/direction.service';

import {MapService} from '../map/shared/map.service';
import {Package} from 'src/app/packages/shared/package';
import {HateoasResourceService} from '@lagoshny/ngx-hateoas-client';
import {Order} from './shared/order';
import {ActivatedRoute, Router} from '@angular/router';
import {Company, Customer} from 'src/app/company/shared/company';
import {UniqueResource} from 'src/app/_core/AbstractResource';
import {SnackbarService} from 'src/app/_core/snackbar.service';
import {ViewportScroller} from '@angular/common';
import {Location} from '@angular/common';

import {KeycloakService} from 'keycloak-angular';

import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {Address} from "../address/shared/address";
import {ContactPerson} from "../contact-person/shared/contact-person";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";

import {DialogAddressComponent} from "../dialog/dialog-address.component";
import {OrderService} from "./shared/order.service";
import {Globals} from "../_core/globals";
import {CompanyService} from "../company/shared/company.service";
import {DialogCustomerComponent} from "../dialog/dialog-customer.component";
import {DialogContactpersonComponent} from "../dialog/dialog-contactperson.component";

@Component({
  selector: 'app-order-add',
  templateUrl: './order-add.component.html',
  styleUrls: ['./order-add.component.css']
})
export class AddOrderComponent implements OnInit {


  order: Order;
  packages: Package[] = [];
  editForm: UntypedFormGroup;
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];

  d: Date = new Date();
  today: Date = new Date(this.d.setMinutes(this.d.getMinutes() - 1));

  minDatePickUpFrom: Date = this.today;
  maxDatePickUpFrom: Date = new Date(('31.12.2050'));

  minDatePickUpTo: Date = this.today;
  maxDatePickUpTo: Date = new Date(('31.12.2050'));

  minDateDeliveryFrom: Date = this.today;
  maxDateDeliveryFrom: Date = new Date(('31.12.2050'));

  minDateDeliveryTo: Date = this.today;
  maxDateDeliveryTo: Date = new Date(('31.12.2050'));


  selectedContactPerson = '';
  selectedCustomer = '';
  contactPersons: ContactPerson[] = [];
  customers: Customer[] = [];
  fromAddress: Address;
  toAddress: Address;
  companyId: string = '';

  localCompleteDatePickUpFrom: Date;
  localCompleteDatePickUpTo: Date;

  localCompleteDateDeliveryFrom: Date;
  localCompleteDateDeliveryTo: Date;

  sendingData = false;

  customer: Customer;

  constructor(private _snackBar: MatSnackBar, private renderer: Renderer2,
              public mapbox: NgxMapboxGLModule,
              private mapService: MapService,
              private directionService: DirectionService,
              private http: HttpClient,
              private resourceHateoasService: HateoasResourceService,
              private route: ActivatedRoute,
              private fb: UntypedFormBuilder,
              public snackbarService: SnackbarService,
              private viewportScroller: ViewportScroller,
              private location: Location,
              private keycloakService: KeycloakService,
              private _router: Router,
              public dialog: MatDialog,
              public companyService: CompanyService,
              private orderService: OrderService, private globals: Globals) {


    this.editForm = this.fb.group({
      contactPerson: ['', [Validators.required]],
      customer: ['',],
      pickUpDate: [, [Validators.required]],
      pickUpDateTo: [, [Validators.required],],
      destinationDate: [, [Validators.required]],
      destinationDateTo: [, [Validators.required]],
      addressIdFrom: [, [Validators.required]],
      addressIdTo: [, [Validators.required]],
      orderComment: ['', []],
    });

  }

  goBack() {
    this.location.back();
  }

  ngOnInit(): void {

    this.minDatePickUpFrom = this.today;
    this.minDatePickUpTo = this.today;
    // @ts-ignore
    //  this.keycloakInstance = this.keycloakService.getKeycloakInstance().profile;

    this.getContactPersons();
    this.getCustomers();

  }

  getContactPersons() {

    this.companyService.getOwnCompany().pipe(
      map((company: Company) => {
        this.companyId = company.id;
      }),
      mergeMap(() => this.companyService.getCompanyContactPersons())
    ).subscribe((collection: any) => {
      this.contactPersons = collection.resources;

    });
  }

  getCustomers() {
    this.companyService.getCustomers().subscribe((collection: any) => {
      this.customers = collection;
    });
  }


  openCustomerDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80vw";
    dialogConfig.height = "80vh";
    dialogConfig.data = {};

    const dialogRef = this.dialog.open(DialogCustomerComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result != 'canceled' && result != undefined) {
        this.customer = result;
      }
    });
  }


  openContactPersonDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80vw";
    //dialogConfig.height = "80vh";
    dialogConfig.data = {};

    const dialogRef = this.dialog.open(DialogContactpersonComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
        this.getContactPersons();
      }
    );
  }


  openAddressDialog(type: string) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80vw";
    dialogConfig.height = "80vh";
    dialogConfig.data = {
      type: type
    };

    const dialogRef = this.dialog.open(DialogAddressComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {

      // console.log(result);
      if (result != 'canceled' && result != undefined) {
        if (type == 'from') {
          this.fromAddress = result;
          this.editForm.controls["addressIdFrom"].setValue(result.id);

        }
        if (type == 'to') {
          this.toAddress = result;
          this.editForm.controls["addressIdTo"].setValue(result.id);
        }
      }

    });
  }

  customDateValidator(group: any) {
    const pickUpDate = group.get('pickUpDate').value;
    const pickUpDateTo = group.get('pickUpDateTo').value;
    const minDatePickUpFrom: Date = new Date((new Date().getTime()));
    const minDatePickUpTo: Date = new Date((new Date().getTime()));


    if (pickUpDate && pickUpDateTo && pickUpDate > pickUpDateTo) {
      return {customDateError: true};
    }

    if (pickUpDate < minDatePickUpFrom) {
      return {customDateError: true};
    }


    return null;

  }


  onDateTimeChangePickupFrom(event: any) {
    let checkDateStr: string = this.editForm.controls["pickUpDateTo"].value;
    const newDate: Date = new Date(event);
    this.minDatePickUpTo = newDate;
    if (newDate > this.localCompleteDatePickUpTo) {
      this.localCompleteDatePickUpTo = newDate;
      this.editForm.controls["pickUpDateTo"].setValue(newDate);
    }
  }

  onDateTimeChangePickupTo(event: any) {

    let checkDateStr: string = this.editForm.controls["pickUpDate"].value;
    const newDate: Date = new Date(event);
    this.minDateDeliveryFrom = newDate;

  }

  onDateTimeChangeDeliveryFrom(event: any) {
    const newDate: Date = new Date(event);
    this.minDateDeliveryTo = newDate;
  }

  onDateTimeChangeDeliveryTo(event: any) {

    let checkDateStr: string = this.editForm.controls["destinationDate"].value;
    const newDate: Date = new Date(event);

  }


  onSubmitOrder() {
    if (this.editForm.valid) {
      this.putOrder();
    }
  }


  putOrder() {
    this.sendingData = true;
    let newOrder: Order = new Order();
    newOrder.addressIdFrom = new UniqueResource();
    newOrder.addressIdFrom.id = this.fromAddress.id;

    newOrder.addressIdTo = new UniqueResource();
    newOrder.addressIdTo.id = this.toAddress.id;

    newOrder.contactPersonId = new UniqueResource();
    newOrder.contactPersonId.id = this.editForm.controls["contactPerson"].value;

    if (this.customer) {
      newOrder.customerId = new UniqueResource();
      newOrder.customerId.id = this.customer.id;
    }

    newOrder.companyId = new Company();
    newOrder.companyId.id = this.companyId;
    newOrder.orderStatus = 'CREATED';


    let newFromPickupDate: Date = new Date(this.editForm.controls["pickUpDate"].value);
    // Add one hour
    //newFromPickupDate.setHours(newFromPickupDate.getHours() + 1);

    let newToPickUpDate: Date = new Date(this.editForm.controls["pickUpDateTo"].value);
    // newToPickUpDate one hour
    //ewToPickUpDate.setHours(newToPickUpDate.getHours() + 1);


    let newFromDeliveryDate: Date = new Date(this.editForm.controls["destinationDate"].value);
    // Add one hour
    //newFromDeliveryDate.setHours(newFromDeliveryDate.getHours() + 1);

    let newToDeliveryDate: Date = new Date(this.editForm.controls["destinationDateTo"].value);
    // newToPickUpDate one hour
    //newToDeliveryDate.setHours(newToDeliveryDate.getHours() + 1);


    newOrder.pickUpDate = newFromPickupDate;
    newOrder.pickUpDateTo = newToPickUpDate;
    newOrder.destinationDate = newFromDeliveryDate;
    newOrder.destinationDateTo = newToDeliveryDate;

    let comment: string = this.editForm.controls["orderComment"].value;

    newOrder.internalComment = comment;


    this.orderService.insertOrder(newOrder).pipe(
      map((order: Order) => {
        this.snackbarService.openSnackBar('Auftrag angelegt', 'Ok', 'green-snackbar');
        this._router.navigate(['' + order.id + "/package"], {relativeTo: this.route});
        return order;
      }),
      catchError(err => {
          this.sendingData = false;
          console.log('caught mapping error and rethrowing', err);
          return throwError(err);
        }
      )
    ).subscribe();

  }
}
