import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import { SnackbarService } from '../_core/snackbar.service';
import {DialogInputComponent} from "./dialog-input.component";

@Component({
  selector: 'app-dialog-price',
  templateUrl: './dialog-price.component.html',
  styleUrls: ['./dialog-price.component.css']
})
export class DialogPriceComponent implements OnInit {
  editForm: UntypedFormGroup = this.fb.group({
    orderPrice: ['', [Validators.required]]
  });

  constructor(private fb: UntypedFormBuilder, private dialogRef: MatDialogRef<DialogPriceComponent>, private snackbarService: SnackbarService) {
    this.editForm = this.fb.group({
      orderPrice: ['', [Validators.required, Validators.min(0), Validators.pattern('[0-9]*(\\.[0-9]+)?')]]
    });

  }

  ngOnInit(): void {

  }

  action() {
    if (this.editForm.valid) {
      let orderPrice: any = this.editForm.controls["orderPrice"].value;
      this.dialogRef.close(orderPrice);
    } else {
     // this.snackbarService.openSnackBar('Geben Sie einen Preis ein.', 'Ok', 'red-snackbar');
    }
  }

  close() {
    this.dialogRef.close('canceled');
  }


  preventNonNumericalInput(event: KeyboardEvent): void {
    const charCode = event.which || event.keyCode;
    const charStr = String.fromCharCode(charCode);

    // Allow digits, commas, periods, backspace, and an empty input
    if (!charStr.match(/^[0-9\.,]*$/) && charCode !== 8) {
      event.preventDefault();
    }
  }


}
