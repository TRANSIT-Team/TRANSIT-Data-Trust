import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogDataSharingComponent} from './dialog-data-sharing.component';
import {Order} from "../order/shared/order";

@Component({
  selector: 'app-dialog-cmr',
  templateUrl: './dialog-cmr.component.html',
  styleUrls: ['./dialog-cmr.component.css']
})
export class DialogCmrComponent implements OnInit {
  order: Order;

  constructor(private dialogRef: MatDialogRef<DialogDataSharingComponent>,
              @Inject(MAT_DIALOG_DATA) data: any) {
    this.order = data.order;
  }

  ngOnInit(): void {
  }

  close() {
    this.dialogRef.close('canceled');
  }
}
