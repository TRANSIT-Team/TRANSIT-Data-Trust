import {Component, Input, OnInit} from '@angular/core';
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import {Order} from "./shared/order";
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidatorFn, Validators} from "@angular/forms";
import {OrderService} from "./shared/order.service";
import {MatDialog} from "@angular/material/dialog";
import {DirectionService} from "../map/shared/direction.service";

import {Globals} from "../_core/globals";
import {SnackbarService} from "../_core/snackbar.service";

@Component({
  selector: 'app-order-dates',
  templateUrl: './order-dates.component.html',
  styleUrls: ['./order-dates.component.css']
})
export class OrderDatesComponent implements OnInit {
  @Input() order: Order;
  @Input() type: string;
  @Input() labelText: string;
  loading = false;
  editing = true;
  minDateFrom: Date = new Date((new Date().getTime()));
  minDateTo: Date = new Date((new Date().getTime()));
  maxDateTo: Date;

  minDatePickUpFrom: Date = new Date((new Date().getTime()));
  maxDatePickUpFrom: Date;
  minDatePickUpTo: Date;
  maxDatePickUpTo: Date;
  minDateDeliveryFrom: Date;
  maxDateDeliveryFrom: Date;
  maxDateFrom: Date;
  minDateDeliverFrom: Date = new Date((new Date().getTime()));
  minDateDeliverTo: Date;

  editForm: UntypedFormGroup = this.fb.group({
    fromDate: [new Date(), [Validators.required]],
    toDate: [new Date(), [Validators.required]]
  });

  valueFromDate: any;
  valueToDate: any;
  localCompleteDateFrom: Date;
  localCompleteDateTo: Date;
  isMyOrder: boolean = true;

  localCompleteDatePickUpFrom: string = "";
  localCompleteDatePickUpTo: string = "";

  localCompleteDateDeliveryFrom: string = "";
  localCompleteDateDeliveryTo: string = "";

  constructor(private fb: UntypedFormBuilder, private orderService: OrderService, public snackbarService: SnackbarService, private globals: Globals) {

  }

  ngOnInit(): void {
    this.initializeForm();
  }


  initializeForm() {
    let minDateTo = new Date();
    let fromDate: any;
    let toDate: any;
    let pickUpDateTo: any = "";

    if (this.type == 'pickUp') {
      fromDate = this.order.pickUpDate;
      toDate = this.order.pickUpDateTo;
    }

    if (this.type == 'delivery') {
      fromDate = this.order.destinationDate;
      toDate = this.order.destinationDateTo;

    }

    this.setUpMinMaxDates();

    if (fromDate != undefined) {
      this.valueFromDate = new Date(fromDate);
      this.localCompleteDateFrom = this.valueFromDate;

    }

    if (toDate != undefined) {
      this.valueFromDate = new Date(toDate);
      this.localCompleteDateTo = this.valueFromDate;
    }

    this.editForm = this.fb.group({
      fromDate: [
        new Date(fromDate),
        {disabled: !this.isMyOrder},
        [Validators.required]
      ], toDate: [
        new Date(toDate),
        {disabled: !this.isMyOrder},
        [Validators.required]
      ]
    });


    if (this.isMyOrder) {
      this.editForm.enable();
    }

    setTimeout(() => {
      this.editing = false;
    }, 500);

  }

  setUpMinMaxDates() {
    let d: Date = new Date();
    if (this.type == 'pickUp') {

      d = new Date(this.order.pickUpDate);
      this.minDateTo = new Date(d.setMinutes(d.getMinutes() - 1));

    }
    if (this.type == 'delivery') {
      d = new Date(this.order.pickUpDateTo);
      this.minDateFrom = new Date(d.setMinutes(d.getMinutes() - 1));

      d = new Date(this.order.destinationDate);
      this.minDateTo = new Date(d.setMinutes(d.getMinutes() - 1));
    }

  }

  customDateValidator(minDateTo: Date): ValidatorFn {

    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (minDateTo) {
        const selectedDate = control.value;
        if (selectedDate && selectedDate < minDateTo) {
          return {'customDateError': true};
        }
      }
      return null;
    };
  }

  onDateTimeChangeFrom(event: any) {

    if (this.editing) {
      return;
    }
    if (this.type == "pickUp") {
      this.onDateTimeChangePickupFrom(event);
    }

    if (this.type == "delivery") {
      this.onDateTimeChangeDeliveryFrom(event);
    }

    this.setUpMinMaxDates();
  }

  onDateTimeChangeTo(event: any) {
    if (this.editing) {
      return;
    }


    if (this.type == "pickUp") {
      this.onDateTimeChangePickupTo(event);
    }

    if (this.type == "delivery") {
      this.onDateTimeChangeDeliveryTo(event);
    }

    this.setUpMinMaxDates();
  }

  onDateTimeChangePickupFrom(event: any) {
    let checkDateStr: string = this.editForm.controls["toDate"].value;
    const newDate: Date = new Date(event);
    this.minDateTo = newDate;
    this.order.pickUpDate = newDate;
    this.minDatePickUpTo = newDate;
  }

  onDateTimeChangePickupTo(event: any) {

    let checkDateStr: string = this.editForm.controls["fromDate"].value;
    const newDate: Date = new Date(event);

    this.maxDatePickUpFrom = newDate;
    this.order.pickUpDateTo = newDate;

  }

  onDateTimeChangeDeliveryFrom(event: any) {

    const newDate: Date = new Date(event);
    this.minDateTo = newDate;


    this.minDatePickUpTo = newDate;
    this.order.destinationDate = newDate;
  }

  onDateTimeChangeDeliveryTo(event: any) {

    let checkDateStr: string = this.editForm.controls["fromDate"].value;
    const newDate: Date = new Date(event);

    this.order.destinationDateTo = newDate
    this.maxDateDeliveryFrom = newDate;
  }

  saveInformations() {
    console.log("save");
    if (this.editForm.valid) {
      let tmpOrder: any;

      let newFromDate: Date = new Date(this.editForm.controls["fromDate"].value);
      // Add one hour
      //newFromDate.setHours(newFromDate.getHours() + 1);
      let newToDate: Date = new Date(this.editForm.controls["toDate"].value);
      if (this.editForm.controls["toDate"].value == undefined || this.editForm.controls["toDate"].value == "") {
        newToDate = new Date(this.editForm.controls["fromDate"].value);
      }

      // Add one hour
      //newToDate.setHours(newToDate.getHours() + 1);


      if (newToDate < newFromDate) {
        this.snackbarService.openSnackBar('Datum-Bis kann nicht vor dem Datum-von liegen.', 'Ok', 'red-snackbar');
        return;
      }

      if (this.type == 'pickUp') {

        tmpOrder = {
          id: this.order.id,
          pickUpDate: newFromDate,
          pickUpDateTo: newToDate,
        };
      }

      if (this.type == 'delivery') {

        tmpOrder = {
          id: this.order.id,
          destinationDate: newFromDate,
          destinationDateTo: newToDate,
        };
      }


      this.globals.dataLoadingText = "Daten senden...";
      this.loading = true;
      this.editing = true;
      this.orderService.patchOrder(tmpOrder).subscribe((order: any) => {
        this.loading = false;
        this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');

        if (this.type == 'pickUp') {
          this.order.pickUpDateTo = order.pickUpDateTo;
          this.order.pickUpDate = order.pickUpDate;
        }
        if (this.type == 'delivery') {
          this.order.destinationDateTo = order.destinationDateTo;
          this.order.destinationDate = order.destinationDate;
        }


        this.initializeForm();

      })
    }

  }
}
