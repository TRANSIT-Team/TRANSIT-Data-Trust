import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {
  timeOut = 3000;
  timeOutMultiple = 1500;

  constructor(public snackBar: MatSnackBar) { }

  openSnackBar(message:any, action: string, className: string) {


    if ( message instanceof Array) {

      message.forEach( (message, index) => {

        setTimeout(() => {

          this.snackBar.open(message.text, action, {
            duration: this.timeOutMultiple,
            verticalPosition: 'top', // 'top' | 'bottom'
            horizontalPosition: 'end', //'start' | 'center' | 'end' | 'left' | 'right'
            panelClass: className,
          });


        }, index * (this.timeOut+500)); // 500 - timeout between two messages

      });


    } else {

      this.snackBar.open(message, action, {
        duration: this.timeOut,
        verticalPosition: 'top', // 'top' | 'bottom'
        horizontalPosition: 'center', //'start' | 'center' | 'end' | 'left' | 'right';
        panelClass: className,
      });

    }


  }



}
