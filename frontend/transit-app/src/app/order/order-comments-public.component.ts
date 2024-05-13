import {Component, Input, OnInit} from '@angular/core';
import {map, mergeMap} from "rxjs";
import {Order, OrderComment} from "./shared/order";
import {OrderService} from "./shared/order.service";
import {SnackbarService} from "../_core/snackbar.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogInputComponent} from "../dialog/dialog-input.component";
import {cloneDeep} from "lodash";
import {DialogDeleteComponent} from "../dialog/dialog-delete.component";
import {Company} from "../company/shared/company";

@Component({
  selector: 'app-order-comments-public',
  templateUrl: './order-comments-public.component.html',
  styleUrls: ['./order-comments-public.component.css']
})
export class OrderCommentsPublicComponent implements OnInit {
  @Input() order: Order;
  loading: boolean = true;
  postParent: boolean = false;
  postChild: boolean = false;
  messageValue = '';
  orderComments: OrderComment[] = [];

  constructor(private orderService: OrderService, private snackbarService: SnackbarService, public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.getComments();
  }

  getComments() {
    this.orderService.getOrderComments(this.order.id).subscribe((c: any) => {
      this.orderComments = this.sortOrderCommentsByCreateDate(c);
      this.loading = false;
    })
  }

  postMessage() {
    if (this.messageValue != '') {
      if (this.order.id != undefined) {

        let m: any = this.messageValue;
        this.messageValue = '';
        let c: OrderComment = new OrderComment();
        c.postParent = this.postParent;
        c.postChild = this.postChild;
        c.comment = m;
        this.orderService.postOrderComment(this.order.id, c).pipe(
          mergeMap(() => this.orderService.getOrderComments(this.order.id))
        ).subscribe((c: any) => {
          this.orderComments = this.sortOrderCommentsByCreateDate(c);
          this.loading = false;
          this.snackbarService.openSnackBar('Kommentar gespeichert', 'Ok', 'green-snackbar');
        })
      }
    }
  }


  sortOrderCommentsByCreateDate(orderComments: OrderComment[]): OrderComment[] {
    orderComments.sort((a, b) => {
      const dateA = new Date(a.createDate!);
      const dateB = new Date(b.createDate!);
      return dateA.getTime() - dateB.getTime();
    });

    return orderComments;
  }

  isWithinLast10Minutes(d: any): boolean {

    if (d) {
      const now = new Date();
      const createDate = new Date(d);
      const diffInMilliseconds = now.getTime() - createDate.getTime();
      const diffInMinutes = diffInMilliseconds / (1000 * 60);
      return diffInMinutes <= 10;
    }
    return false;
  }


  openEditDialog(id: any) {

    let c: OrderComment = this.orderComments.filter((com: OrderComment) => com.id == id)[0];
    if (c) {
      const dialogRef = this.openInputDialog(id, c.comment);
      dialogRef.afterClosed().subscribe((result: any) => {
        if (result != 'canceled' && result != undefined) {
          this.updateComment(id, result);
        }
      });
    }

  }


  openInputDialog(id: string, comment: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: 'Kommentar bearbeiten',
      text: '',
      btnActionClass: '',
      btnActionText: 'Kommentar speichern',
      dialogInput: comment,
      inputLabel: 'Kommentar'
    };
    return this.dialog.open(DialogInputComponent,
      dialogConfig);
  }


  deleteComment(id: any) {

    let c: OrderComment = this.orderComments.filter((com: OrderComment) => com.id == id)[0];
    if (c) {

      const dialogRef = this.openDeleteDialog('', 'Kommentar löschen?', 'Diesen Kommentar löschen?');
      dialogRef.afterClosed().subscribe((result: any) => {
        if (result == 'deleted' && result != undefined) {
          this.deleteCommentById(c.id);
        }
      });

    }
  }


  openDeleteDialog(id: string, title: string, text: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: title,
      text: text,
    };
    return this.dialog.open(DialogDeleteComponent,
      dialogConfig);
  }


  updateComment(id: string, comment: string) {

    let c: OrderComment = this.orderComments.filter((com: OrderComment) => com.id == id)[0];

    let cPost: any = {
      id: c.id,
      comment: comment
    }

    if (c) {
      this.orderService.patchOrderComment(this.order.id, c.id, cPost).pipe(
        mergeMap(() => this.orderService.getOrderComments(this.order.id))
      ).subscribe((cresponse: any) => {
        c.comment = comment;
        this.loading = false;
        this.snackbarService.openSnackBar('Kommentar aktualisiert', 'Ok', 'green-snackbar');
      })
    }

  }


  deleteCommentById(id: string) {
    let c: OrderComment = this.orderComments.filter((com: OrderComment) => com.id == id)[0];
    if (c) {
      this.orderService.deleteOrderComment(this.order.id, id).pipe(
        mergeMap(() => this.orderService.getOrderComments(this.order.id))
      ).subscribe((cresponse: any) => {

        let indexOfList: number = this.orderComments.findIndex((c: OrderComment) => c.id === id);
        if (indexOfList !== -1) {
          this.orderComments.splice(indexOfList, 1);
        }

        this.loading = false;
        this.snackbarService.openSnackBar('Kommentar gelöscht', 'Ok', 'green-snackbar');
      })
    }

  }

}
