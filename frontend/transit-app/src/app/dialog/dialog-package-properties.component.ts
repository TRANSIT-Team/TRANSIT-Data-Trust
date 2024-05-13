import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {UntypedFormBuilder} from "@angular/forms";

@Component({
  selector: 'app-dialog-package-properties',
  templateUrl: './dialog-package-properties.component.html',
  styleUrls: ['./dialog-package-properties.component.css']
})
export class DialogPackagePropertiesComponent implements OnInit {

  constructor(public dialog: MatDialog, private dialogRef: MatDialogRef<DialogPackagePropertiesComponent>,
              private fb: UntypedFormBuilder,
              @Inject(MAT_DIALOG_DATA) data: any) {

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
