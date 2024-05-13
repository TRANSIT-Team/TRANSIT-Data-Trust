import {Component, OnInit} from '@angular/core';
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Address} from "../address/shared/address";
import {catchError, map, mergeMap, throwError} from "rxjs";
import {MatDialogRef} from "@angular/material/dialog";
import {DialogInputComponent} from "./dialog-input.component";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {LocationPoint} from "../company/shared/company";
import {SnackbarService} from "../_core/snackbar.service";

@Component({
  selector: 'app-dialog-address',
  templateUrl: './dialog-address.component.html',
  styleUrls: ['./dialog-address.component.css']
})
export class DialogAddressComponent implements OnInit {
  addAddress: boolean = false;

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
