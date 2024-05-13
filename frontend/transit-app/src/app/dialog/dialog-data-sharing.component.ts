import {Component, Inject, OnInit} from '@angular/core';

import { MAT_DIALOG_DATA } from "@angular/material/dialog";
import {} from "@angular/material";
import {MatDialogRef} from "@angular/material/dialog";
import {UntypedFormBuilder, UntypedFormGroup} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-dialog-data-sharing',
  templateUrl: './dialog-data-sharing.component.html',
  styleUrls: ['./dialog-data-sharing.component.css']
})
export class DialogDataSharingComponent implements OnInit {
  form: UntypedFormGroup;
  description:string;
  id:string;
  title: string;
  text: string;

  constructor(
    private fb: UntypedFormBuilder,
    private dialogRef: MatDialogRef<DialogDataSharingComponent>,
    @Inject(MAT_DIALOG_DATA) data:any) {

    this.description = data.description;
    this.id = data.id;
    this.title = data.title;
    this.text = data.text;
  }


  ngOnInit(): void {
  }
  ok() {
    this.dialogRef.close("ok");
  }

  close() {
    this.dialogRef.close('canceled');
  }
}
