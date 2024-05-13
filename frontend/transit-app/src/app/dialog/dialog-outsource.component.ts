import {Component, Inject, OnInit} from '@angular/core';
import {Order} from "../order/shared/order";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogInputComponent} from "./dialog-input.component";
import {Company} from "../company/shared/company";

@Component({
  selector: 'app-dialog-outsource',
  templateUrl: './dialog-outsource.component.html',
  styleUrls: ['./dialog-outsource.component.css']
})
export class DialogOutsourceComponent implements OnInit {
  title: string = "Auftrag outsourcen";
  order: Order;
  outsourceMapCompanies: Company[];
  outputData={text:'',data:{}}
  constructor(@Inject(MAT_DIALOG_DATA) data: any, private dialogRef: MatDialogRef<DialogInputComponent>) {
    if (data != undefined) {
      this.order = data.order;
      this.outsourceMapCompanies = data.outsourceMapCompanies;
    }
  }

  ngOnInit(): void {
  }

  close() {
    this.outputData.text='canceled';
    this.outputData.data=this.outsourceMapCompanies;
    this.dialogRef.close(this.outputData);
  }

  chooseItem(item: any) {
    this.outputData.text=item;
    this.outputData.data=this.outsourceMapCompanies;
    this.dialogRef.close(this.outputData);
  }

  storeOutsourceMapCompanies(data:any){
    this.outsourceMapCompanies=data;
  }
}
