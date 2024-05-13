import {Component, Input, OnInit} from '@angular/core';
import {Order, OrderStatus} from "./shared/order";
import {ContactPerson} from "../contact-person/shared/contact-person";
import {OrderService} from "./shared/order.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {SnackbarService} from "../_core/snackbar.service";
import {CompanyService} from "../company/shared/company.service";
import {iif, map, mergeMap, of} from "rxjs";
import {DialogInputComponent} from "../dialog/dialog-input.component";
import {Globals} from "../_core/globals";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-order-price',
  templateUrl: './order-price.component.html',
  styleUrls: ['./order-price.component.css']
})
export class OrderPriceComponent implements OnInit {
  @Input() order: Order;
  loading = false;
  orderPrice: any = 'Kein Preis festgelegt';
  editForm: UntypedFormGroup = this.fb.group({
    orderPrice: ['', [Validators.required]],
    packagesPrice: ['',],
    orderAltPrice: ['',],
    //  outsourceCost: ['',]
  });

  constructor(public orderService: OrderService, public dialog: MatDialog, public snackbarService: SnackbarService, private globals: Globals, private fb: UntypedFormBuilder) {
  }

  preventNonNumericalInput(event: KeyboardEvent): void {
    const charCode = event.which || event.keyCode;
    const charStr = String.fromCharCode(charCode);

    // Allow digits, commas, periods, backspace, and an empty input
    if (!charStr.match(/^[0-9\.,]*$/) && charCode !== 8) {
      event.preventDefault();
    }
  }


  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm() {

    if (this.order.price != undefined) {
      this.orderPrice = parseFloat(this.order.price.toString()).toFixed(2).toString() + '€';
    } else {
      this.order.price = 0;
    }

    if (this.order.orderAltPrice == undefined) {
      this.order.orderAltPrice = 0;
    }

    let packagesPrice:any;
    if (this.order.packagesPrice != undefined) {
      packagesPrice = parseFloat(this.order.packagesPrice!.toString()).toFixed(2);
    }

    this.editForm = this.fb.group({
      orderPrice: [parseFloat(this.order.price!.toString()).toFixed(2), [Validators.min(0), Validators.pattern('[0-9]*(\\.[0-9]+)?')]],
      packagesPrice: [packagesPrice, [Validators.min(0), Validators.pattern('[0-9]*(\\.[0-9]+)?')]],
      orderAltPrice: [parseFloat(this.order.orderAltPrice!.toString()).toFixed(2), [Validators.min(0), Validators.pattern('[0-9]*(\\.[0-9]+)?')]],
      //   outsourceCost: [this.order.outsourceCost, [Validators.min(0), Validators.pattern('[0-9]*(\\.[0-9]+)?')]]
    });
  }


  saveInformations() {
    if (this.editForm.valid) {

      let tmpOrder: any;
      let price: any = this.editForm.controls["orderPrice"].value;
      let packagesPrice: any = this.editForm.controls["packagesPrice"].value;
      let orderAltPrice: any = this.editForm.controls["orderAltPrice"].value;
      // let outsourceCost: any = this.editForm.controls["outsourceCost"].value;


      if (this.order.orderRightsGlobal!.edit.price) {
        tmpOrder = {
          id: this.order.id,
          price: price,
          packagesPrice: packagesPrice,
          //   outsourceCost: outsourceCost,
          orderAltPrice: orderAltPrice,
        };
      } else {

        tmpOrder = {
          id: this.order.id,
          packagesPrice: packagesPrice,
          orderAltPrice: orderAltPrice,
        };
      }


      //this.globals.dataLoadingText = "Daten senden...";
      // this.globals.dataLoading = true;

      this.loading = true;
      this.orderService.patchOrder(tmpOrder).subscribe((order: any) => {
        this.loading = false;
        this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');
      })

    }
  }
}
