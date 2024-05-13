import {Component, Inject, OnInit} from '@angular/core';
import {UntypedFormBuilder} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {SnackbarService} from "../_core/snackbar.service";
import {DialogInputComponent} from "./dialog-input.component";

@Component({
  selector: 'app-dialog-info',
  templateUrl: './dialog-info.component.html',
  styleUrls: ['./dialog-info.component.css']
})
export class DialogInfoComponent implements OnInit {
  type: string = '';

  constructor(
    private dialogRef: MatDialogRef<DialogInputComponent>,
    @Inject(MAT_DIALOG_DATA) data: any) {

    this.type = data.type;
  }

  ngOnInit(): void {
  }

}
