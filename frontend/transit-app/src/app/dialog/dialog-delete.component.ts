import {Component, Inject, OnInit} from '@angular/core';

import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {} from "@angular/material";
import {MatDialogRef} from "@angular/material/dialog";
import {UntypedFormBuilder, UntypedFormGroup} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog-delete.component.html',
  styleUrls: ['./dialog-delete.component.css']
})
export class DialogDeleteComponent implements OnInit {
  form: UntypedFormGroup;
  description: string;
  id: string = "";
  title: string = "";
  text: string;
  buttonConfirmText: string = "Löschen";

  constructor(
    private fb: UntypedFormBuilder,
    private dialogRef: MatDialogRef<DialogDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) data: any) {

    this.description = data.description;
    this.id = data.id;
    this.title = data.title;
    this.text = data.text;

    if (data.buttonConfirmText != undefined && data.buttonConfirmText != "") {
      this.buttonConfirmText = data.buttonConfirmText;
    }

  }

  ngOnInit() {
    this.form = this.fb.group({
      description: [this.description, []],
    });
  }

  delete() {
    this.dialogRef.close("deleted");
  }

  close() {
    this.dialogRef.close('canceled');
  }

}
