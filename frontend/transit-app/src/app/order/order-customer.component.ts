import {Component, Input, OnInit} from '@angular/core';
import {Order} from "./shared/order";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {OrderService} from "./shared/order.service";
import {ContactPerson} from "../contact-person/shared/contact-person";
import {Customer} from "../company/shared/company";
import {FunctionsService} from "../_core/functions.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {SnackbarService} from "../_core/snackbar.service";
import {CompanyService} from "../company/shared/company.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Globals} from "../_core/globals";
import {OutsourceService} from "../outsource/shared/outsource-component.service";
import {DialogAddressComponent} from "../dialog/dialog-address.component";
import {DialogCustomerComponent} from "../dialog/dialog-customer.component";

@Component({
  selector: 'app-order-customer',
  templateUrl: './order-customer.component.html',
  styleUrls: ['./order-customer.component.css']
})
export class OrderCustomerComponent implements OnInit {
  @Input() order: Order;
  loading: boolean = false;

  selectedCustomer = '';
  customers: Customer[] = [];
  customer: Customer;

  deliveryTimestamp: any = new Date();
  isMyOrder: boolean = true;


  constructor(public orderService: OrderService,
              private functionsService: FunctionsService,
              public dialog: MatDialog,
              public snackbarService: SnackbarService,
              private companyService: CompanyService
  ) {
  }

  ngOnInit(): void {
    this.getCustomers();
  }

  getCustomers() {
    this.companyService.getCustomers().subscribe((collection: any) => {
      this.customers = collection;
      if (this.order.customerId != undefined) {
        this.selectedCustomer = this.order.customerId.id;
        this.customer = this.customers.filter((c) => c.id == this.order.customerId!.id)[0]
      }

    });
  }

  saveInformations() {
    let tmpOrder: any;

    tmpOrder = {
      id: this.order.id,
      customerId: {id: this.customer.id}
    };

    let noUpdate: boolean = false;

    if (this.order.customerId != undefined) {
      if (this.customer.id == this.order.customerId.id) {
        noUpdate = true;
      }
    }

    if (noUpdate) {
      this.snackbarService.openSnackBar('Keine Änderung vorgenommen.', 'Ok', 'green-snackbar');
    }
    else {
      this.loading = true;
      this.orderService.patchOrder(tmpOrder).subscribe((order: any) => {
        this.loading = false;
        this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');
      });
    }
  }


  openDialog() {
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
}
