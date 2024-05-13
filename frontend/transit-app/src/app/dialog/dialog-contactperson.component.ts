import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {UntypedFormBuilder} from "@angular/forms";
import {HateoasResourceService} from "@lagoshny/ngx-hateoas-client";
import {SnackbarService} from "../_core/snackbar.service";
import {CompanyService} from "../company/shared/company.service";

@Component({
  selector: 'app-dialog-contactperson',
  templateUrl: './dialog-contactperson.component.html',
  styleUrls: ['./dialog-contactperson.component.css']
})
export class DialogContactpersonComponent implements OnInit {
  constructor(public dialog: MatDialog, private dialogRef: MatDialogRef<DialogContactpersonComponent>,
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
