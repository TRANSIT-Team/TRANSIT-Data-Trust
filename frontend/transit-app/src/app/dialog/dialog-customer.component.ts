import { Component, OnInit } from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {DialogInputComponent} from "./dialog-input.component";

@Component({
  selector: 'app-dialog-customer',
  templateUrl: './dialog-customer.component.html',
  styleUrls: ['./dialog-customer.component.css']
})
export class DialogCustomerComponent implements OnInit {
  addItem: boolean = false;

  constructor(private dialogRef: MatDialogRef<DialogInputComponent>,) {

  }


  ngOnInit(): void {
  }
  chooseItem(item: any) {
    this.dialogRef.close(item);
  }

  close() {
    this.dialogRef.close('canceled');
  }
}
