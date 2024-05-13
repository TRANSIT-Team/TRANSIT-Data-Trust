import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogInputComponent} from './dialog-input.component';

@Component({
  selector: 'app-dialog-favorites',
  templateUrl: './dialog-favorites.component.html',
  styleUrls: ['./dialog-favorites.component.css']
})
export class DialogFavoritesComponent implements OnInit {
  companiesIds: string[];

  constructor(private dialogRef: MatDialogRef<DialogInputComponent>,
              @Inject(MAT_DIALOG_DATA) data: any) {
    this.companiesIds = data.companiesIds;
  }

  ngOnInit(): void {
  }


  close() {

    this.dialogRef.close('canceled');
  }

  chooseItem(item: any) {
    this.dialogRef.close('refresh');
  }


  submitData(data: any) {
    this.dialogRef.close(data);
  }
}
