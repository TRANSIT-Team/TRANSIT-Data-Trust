import {Component, Inject, Input, OnInit} from '@angular/core';
import {Address} from "../address/shared/address";
import {MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {HateoasResourceService} from "@lagoshny/ngx-hateoas-client";
import {SnackbarService} from "../_core/snackbar.service";
import {CompanyService} from "../company/shared/company.service";
import {Customer} from "../company/shared/company";
import {DialogAddressComponent} from "./dialog-address.component";
import {DialogInputComponent} from "./dialog-input.component";

@Component({
  selector: 'app-dialog-customer-edit',
  templateUrl: './dialog-customer-edit.component.html',
  styleUrls: ['./dialog-customer-edit.component.css']
})
export class DialogCustomerEditComponent implements OnInit {
  editForm: UntypedFormGroup;
  customer: Customer;
  customerAddress: Address;

  addNewCustomer = false;

  constructor(public dialog: MatDialog, private dialogRef: MatDialogRef<DialogCustomerEditComponent>,
              private fb: UntypedFormBuilder,
              private resourceHateoasService: HateoasResourceService, public snackbar: SnackbarService, private companyService: CompanyService, @Inject(MAT_DIALOG_DATA) data: any) {

    this.customer = data.customer;
  }


  ngOnInit(): void {
    this.initForm();
  }

  initForm() {

    this.editForm = this.fb.group({
      name: [this.customer.name, [Validators.required]],
      email: [this.customer.email, [Validators.required]],
      tel: [this.customer.tel, [Validators.required]]
    });
  }

  close() {

    this.dialogRef.close('canceled');
  }


  updateCustomer() {

    let tmpRowData: any = {
      email: this.editForm.controls["email"].value,
      name: this.editForm.controls["name"].value,
      tel: this.editForm.controls["tel"].value,
    }

    this.companyService.updateCustomer(this.customer.id, tmpRowData).subscribe((cu: any) => {
      this.snackbar.openSnackBar('Kunde aktualsiert', 'Ok', 'green-snackbar');
      this.dialogRef.close(cu);
    })
  }
}
