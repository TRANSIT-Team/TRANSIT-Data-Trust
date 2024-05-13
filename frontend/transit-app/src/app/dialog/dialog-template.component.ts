import {Component, Inject, OnInit} from '@angular/core';

import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {} from "@angular/material";
import {MatDialogRef} from "@angular/material/dialog";
import {UntypedFormBuilder, UntypedFormGroup} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";


@Component({
  selector: 'app-dialog-template',
  templateUrl: './dialog-template.component.html',
  styleUrls: ['./dialog-template.component.css']
})
export class DialogTemplateComponent implements OnInit {

     // @ts-ignore
     form: UntypedFormGroup;
     description:string;
     title: string;
     text: string;
     btnActionText: string;
     btnActionClass: string;

     constructor(
       private fb: UntypedFormBuilder,
       private dialogRef: MatDialogRef<DialogTemplateComponent>,
       @Inject(MAT_DIALOG_DATA) data:any) {

       this.description = data.description;
       this.title = data.title;
       this.text = data.text;
       this.btnActionText = data.btnActionText;
       this.btnActionClass= data.btnActionClass;
     }

     ngOnInit() {
       this.form = this.fb.group({
         description: [this.description, []],
       });
     }

     action() {
       this.dialogRef.close("action");
     }

     close() {
       this.dialogRef.close('canceled');
     }

   }
