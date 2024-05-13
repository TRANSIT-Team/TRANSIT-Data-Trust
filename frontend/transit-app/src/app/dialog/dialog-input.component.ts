import {Component, Inject, OnInit} from '@angular/core';

import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {} from "@angular/material";
import {MatDialogRef} from "@angular/material/dialog";
import {UntypedFormBuilder, UntypedFormGroup} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {SnackbarService} from "../_core/snackbar.service";


@Component({
  selector: 'app-dialog-input',
  templateUrl: './dialog-input.component.html',
  styleUrls: ['./dialog-input.component.css']
})
export class DialogInputComponent implements OnInit {

  // @ts-ignore
  form: UntypedFormGroup;
  description: string;
  title: string;
  text: string;
  inputLabel: string = 'Grund';

  btnActionText: string;
  btnActionClass: string;
  dialogInput: string = '';

  constructor(
    private fb: UntypedFormBuilder,
    private dialogRef: MatDialogRef<DialogInputComponent>,
    private snackbarService: SnackbarService,
    @Inject(MAT_DIALOG_DATA) data: any) {
    this.inputLabel = data.inputLabel;
    this.description = data.description;
    this.title = data.title;
    this.text = data.text;
    this.btnActionText = data.btnActionText;
    this.btnActionClass = data.btnActionClass;

    this.dialogInput = data.dialogInput;
  }

  ngOnInit() {
    this.form = this.fb.group({
      description: [this.description, []],
    });
  }

  action() {

    if (this.dialogInput == "") {

      this.snackbarService.openSnackBar('Keinen Grund angegeben', 'Ok', 'red-snackbar');
    } else {

      this.dialogRef.close(this.dialogInput);
    }

  }

  close() {
    this.dialogRef.close('canceled');
  }

}
