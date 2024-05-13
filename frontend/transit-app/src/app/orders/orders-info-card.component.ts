import {Component, Input, OnInit} from '@angular/core';
import {map, mergeMap} from "rxjs";
import {Company} from "../company/shared/company";
import {PagedResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {Order, OrderSummary} from "../order/shared/order";
import {OrderService} from "../order/shared/order.service";
import {CompanyService} from "../company/shared/company.service";

@Component({
  selector: 'app-orders-info-card',
  templateUrl: './orders-info-card.component.html',
  styleUrls: ['./orders-info-card.component.css']
})
export class OrdersInfoCardComponent implements OnInit {
  @Input() orderSummary: number;
  @Input() orderStatusFilter: string = "";
  @Input() extraFilter: string = "";
  @Input() loading: boolean = true;
  orderStatusDescription: string = "";
  orderStatusCount: number = 0;

  constructor(public orderService: OrderService) {
  }

  ngOnInit(): void {
    if (this.orderStatusFilter == 'REQUESTED') {
      this.orderStatusDescription = "ANFRAGEN AN MICH";
    } else  if (this.orderStatusFilter == 'ACCEPTED')  {
      this.orderStatusDescription = "ANGENOMMEN VON MIR";
    } else {
      this.orderStatusDescription = this.orderService.getOrderStatusDescription(this.orderStatusFilter);
    }
    //  this.orderStatusCount = this.orders.filter((o) => o.orderStatus == this.orderStatusFilter).length;*/
  }

  /*
    getCount():any{
      if (this.orderStatusDescription=='ALL'){
        return this.orders.length;
      }
      return this.orders.filter((o) => o.orderStatus == this.orderStatusFilter).length;
    }
  */


}

