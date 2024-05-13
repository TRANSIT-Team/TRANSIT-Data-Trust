import {Component, Input, OnInit} from '@angular/core';
import {Order} from "./shared/order";
import {SnackbarService} from "../_core/snackbar.service";
import {OrderService} from "./shared/order.service";

@Component({
  selector: 'app-order-comment',
  templateUrl: './order-comment.component.html',
  styleUrls: ['./order-comment.component.css']
})
export class OrderCommentComponent implements OnInit {
  @Input() order: Order;
  loading: boolean = false;

  constructor(private orderService: OrderService, private snackbarService: SnackbarService) {
  }

  ngOnInit(): void {
  }

  saveComment() {


    let tmpOrder: any;

    tmpOrder = {
      id: this.order.id,
      comment: this.order.internalComment
    };
    this.loading = true;
    this.orderService.patchOrder(tmpOrder).subscribe((order: any) => {
      this.loading = false;
      this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');
    });


  }
}
