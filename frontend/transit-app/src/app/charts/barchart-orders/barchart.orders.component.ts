import { Component, ElementRef, Input, OnInit } from '@angular/core';

import { OrderService } from 'src/app/order/shared/order.service';
import { HateoasResourceService } from '@lagoshny/ngx-hateoas-client';
import { CompanyService } from '../../company/shared/company.service';
import { OrderSummary } from '../../order/shared/order';
import { map, mergeMap, of } from 'rxjs';

@Component({
  selector: 'app-barchart-orders',
  templateUrl: './barchart.orders.component.html',
  styleUrls: ['./barchart.orders.component.css'],
})
export class BarchartOrdersComponent implements OnInit {
  dataOrders: any = [];

  constructor(private orderService: OrderService) {
    this.getData();
  }

  ngOnInit(): void {
    this.getData();
  }

  getData(): void {
    of(0)
      .pipe(
        mergeMap(() => this.orderService.getOrdersSummary()),
        map((summary: any) => {
       //   console.log(summary);
          let datOwn = new DataOrderChart();
          datOwn.category = 'Aufträge';
          datOwn.revoked =
            summary.ownOrders.revoked + summary.acceptedSubOrdes.revoked;
          datOwn.processing =
            summary.ownOrders.processing + summary.acceptedSubOrdes.processing;
          datOwn.rejected =
            summary.ownOrders.rejected + summary.acceptedSubOrdes.rejected;
          datOwn.open = summary.ownOrders.open + summary.acceptedSubOrdes.open;
          datOwn.accepted =
            summary.ownOrders.accepted + summary.acceptedSubOrdes.accepted;
          datOwn.complete =
            summary.ownOrders.complete + summary.acceptedSubOrdes.complete;
          datOwn.requested =
            summary.ownOrders.requested + summary.acceptedSubOrdes.requested;
          datOwn.canceled =
            summary.ownOrders.canceled + summary.acceptedSubOrdes.canceled;
          datOwn.created =
            summary.ownOrders.created + summary.acceptedSubOrdes.created;

          this.dataOrders.push(datOwn);

          let datOwnSub = new DataOrderChart();
          datOwnSub.category = 'Subaufträge';
          datOwnSub.revoked = summary.ownSuborders.revoked;
          datOwnSub.processing = summary.ownSuborders.processing;
          datOwnSub.rejected = summary.ownSuborders.rejected;
          datOwnSub.open = summary.ownSuborders.open;
          datOwnSub.accepted = summary.ownSuborders.accepted;
          datOwnSub.complete = summary.ownSuborders.complete;
          datOwnSub.requested = summary.ownSuborders.requested;
          datOwnSub.canceled = summary.ownSuborders.canceled;
          datOwnSub.created = summary.ownSuborders.created;
          this.dataOrders.push(datOwnSub);
         // console.log(this.dataOrders);
        })
      )
      .subscribe();
  }
}

export class DataOrderChart {
  category: string;
  revoked: number;
  processing: number;
  rejected: number;
  open: number;
  accepted: number;
  complete: number;
  requested: number;
  canceled: number;
  created: number;
}
