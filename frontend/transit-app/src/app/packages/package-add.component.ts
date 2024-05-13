import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Order} from "../order/shared/order";
import {catchError, iif, map, mergeMap, of, throwError} from "rxjs";
import {OrderService} from "../order/shared/order.service";
import {SnackbarService} from "../_core/snackbar.service";

@Component({
  selector: 'app-package-add',
  templateUrl: './package-add.component.html',
  styleUrls: ['./package-add.component.css']
})
export class PackageAddComponent implements OnInit {
  order: any = undefined;
  loading = true;

  constructor(private route: ActivatedRoute, private router: Router, private orderService: OrderService, private snackbarService: SnackbarService) {
  }

  ngOnInit(): void {
    this.getOrder();
  }

  cancel() {

    this.router.navigate(['/orders/order/' + this.route.snapshot.paramMap.get('orderId')!.toString()], {relativeTo: this.route});
  }

  getOrder() {
    let orderId: any = this.route.snapshot.paramMap.get('orderId')!;

    this.orderService.getOrder(orderId).pipe(
      catchError((err, caught) => {
        let et: string = 'Es ist ein Fehler aufgetreten';
        //console.log(err);
        if (err.error.message != undefined) {
          et = err.error.message.toString();
        }

        return throwError(err);
      })
    )
      .subscribe((order: Order) => {
        this.loading = false;
        if (order.deleted) {
          this.snackbarService.openSnackBar('Sie haben keinen Zugriff auf diesen Auftrag', 'Ok', 'red-snackbar');
          this.router.navigate(['/orders/'], {relativeTo: this.route});
        }

        let orderRightType: any = "";
        orderRightType = "MainOrder_without_subOrders";
        let subOrders: any = [];
        order.orderRightsGlobal = this.orderService.getOrderRights(orderRightType, order.orderStatus!, subOrders)
        this.order = order;
      });
  }

}
