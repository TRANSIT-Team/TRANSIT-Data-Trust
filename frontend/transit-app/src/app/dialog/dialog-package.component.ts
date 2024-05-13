import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogInputComponent} from "./dialog-input.component";
import {Package} from "../packages/shared/package";
import {Order} from "../order/shared/order";

@Component({
  selector: 'app-dialog-package',
  templateUrl: './dialog-package.component.html',
  styleUrls: ['./dialog-package.component.css']
})
export class DialogPackageComponent implements OnInit {
  newPackage: boolean = false;
  packageItem: Package;
  order: Order;

  constructor(private dialogRef: MatDialogRef<DialogInputComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
    if (data.packageItem != "newPackage") {
      this.newPackage = false;
    } else {
      this.newPackage = true;
    }
    this.packageItem = data.packageItem;
    this.order = data.order;




  }

  ngOnInit(): void {
  }

  close() {
    this.dialogRef.close('canceled');
  }

  chooseItem(item: any) {
    this.dialogRef.close(item);
  }


}
