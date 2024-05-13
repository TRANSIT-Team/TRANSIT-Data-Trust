import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {HateoasResourceService} from "@lagoshny/ngx-hateoas-client";
import {SnackbarService} from "../_core/snackbar.service";
import {AddressService} from "../address/shared/address.service";
import {CompanyService} from "../company/shared/company.service";
import {Customer} from "../company/shared/company";
import {Address} from "../address/shared/address";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogAddressComponent} from "../dialog/dialog-address.component";

@Component({
  selector: 'app-customers-add',
  templateUrl: './customers-add.component.html',
  styleUrls: ['./customers-add.component.css']
})
export class CustomersAddComponent implements OnInit {
  editForm: UntypedFormGroup;
  @Input() dialogComponent: boolean = false;
  @Output() externButtonClick = new EventEmitter<any>();

  customerAddress: Address;

  addNewCustomer = false;

  constructor(public dialog: MatDialog, private fb: UntypedFormBuilder, private resourceHateoasService: HateoasResourceService, public snackbar: SnackbarService, private companyService: CompanyService) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.editForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required]],
      tel: ['', [Validators.required]],
      addressId: ['', [Validators.required]],
    });
  }


  putCustomer() {
    let c: Customer = new Customer();

    this.editForm.controls["email"].value;
    c.email = this.editForm.controls["email"].value;
    c.name = this.editForm.controls["name"].value;
    c.tel = this.editForm.controls["tel"].value;

    c.addressId = this.customerAddress.id;


    this.companyService.insertCustomer(c).subscribe((cu: any) => {
      this.snackbar.openSnackBar('Kunde angelegt', 'Ok', 'green-snackbar');
      this.addNewCustomer = false;
      this.externButtonClick.emit(cu);
    });
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
      console.log(result);
      if (result != 'canceled') {
        this.customerAddress = result;
        this.editForm.controls["addressId"].setValue(result.id);
      }
    });
  }


}
