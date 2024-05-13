import {Component, Inject, OnInit} from '@angular/core';
import {Order} from "../order/shared/order";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogInputComponent} from "./dialog-input.component";
import {CompanyDefinedOutsource} from "../entity-rights/shared/entity-right";

@Component({
  selector: 'app-dialog-suborders',
  templateUrl: './dialog-suborders.component.html',
  styleUrls: ['./dialog-suborders.component.css']
})
export class DialogSubordersComponent implements OnInit {
  order: Order;
  companyDefined: CompanyDefinedOutsource[] = [];
  editable: boolean = true;

  constructor(private dialogRef: MatDialogRef<DialogInputComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
    this.order = data.order;

    if (data.editable != undefined) {
      this.editable = data.editable;
    }
  }

  ngOnInit(): void {

  }

  close() {
    if (this.companyDefined != []) {
      this.dialogRef.close(this.companyDefined);
    } else {
      this.dialogRef.close('canceled');
    }


  }

  chooseItem(item: any) {
    this.dialogRef.close(this.companyDefined);
  }

  updateExternCompanyDefined(cD: any) {
    this.companyDefined = [];
    this.companyDefined = cD;
  }
}
