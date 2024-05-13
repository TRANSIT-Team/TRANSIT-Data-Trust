import { Component, OnInit, ViewChild } from '@angular/core';
import { map, mergeMap, Subscription, timer } from 'rxjs';
import { Company } from '../company/shared/company';
import { PagedResourceCollection } from '@lagoshny/ngx-hateoas-client';
import { Order, OrderSummary } from '../order/shared/order';
import { OrderService } from '../order/shared/order.service';
import { CompanyService } from '../company/shared/company.service';
import { OrderMapComponent } from '../order/order-map.component';
import { OrdersInfoCardComponent } from './orders-info-card.component';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  @ViewChild(OrdersInfoCardComponent, { static: false })
  ordersInfoCardComponent: OrdersInfoCardComponent;
  orders: Order[] = [];
  ownCompany: Company;
  loadingOrder: boolean = true;
  orderSummary: any = {
    accepted: 0,
    canceled: 0,
    complete: 0,
    created: 0,
    open: 0,
    processing: 0,
    rejected: 0,
    requested: 0,
    revoked: 0,
  };
  selectedExtraFilter: any;
  extraFilter: string = '';
  showTable = true;
  orderOverviewSubscripton: Subscription;

  constructor(
    public orderService: OrderService,
    private companyService: CompanyService
  ) {}

  ngOnInit(): void {
    this.getOrderData();
  }

  ngOnDestroy() {
    if (this.orderOverviewSubscripton != undefined) {
      this.orderOverviewSubscripton.unsubscribe();
    }
  }

  checkOrderOverview() {
    this.orderOverviewSubscripton = timer(0, 10000)
      .pipe(mergeMap(() => this.orderService.getOrdersSummary()))
      .subscribe((orderSummary: any) => {
        this.mapOrderData(orderSummary);
      });
  }

  getOrderData() {
    this.orderService.getOrdersSummary().subscribe((orderSummary: any) => {
      this.loadingOrder = false;
      if (orderSummary != undefined) {
        // this.orderSummary = orderSummary.ownOrders;
        this.mapOrderData(orderSummary);
        this.checkOrderOverview();
      }
    });
  }

  mapOrderData(orderSummary: any) {
    this.orderSummary.accepted =
      orderSummary.ownOrders.accepted + orderSummary.acceptedSubOrdes.accepted;
    this.orderSummary.canceled =
      orderSummary.ownOrders.canceled + orderSummary.acceptedSubOrdes.canceled;
    this.orderSummary.created =
      orderSummary.ownOrders.created + orderSummary.acceptedSubOrdes.created;
    this.orderSummary.open =
      orderSummary.ownOrders.open + orderSummary.acceptedSubOrdes.open;
    this.orderSummary.processing =
      orderSummary.ownOrders.processing +
      orderSummary.acceptedSubOrdes.processing;
    this.orderSummary.rejected =
      orderSummary.ownOrders.rejected + orderSummary.acceptedSubOrdes.rejected;
    this.orderSummary.requested =
      orderSummary.ownOrders.requested +
      orderSummary.acceptedSubOrdes.requested;
    this.orderSummary.revoked =
      orderSummary.ownOrders.revoked + orderSummary.acceptedSubOrdes.revoked;
    this.orderSummary.complete =
      orderSummary.ownOrders.complete + orderSummary.acceptedSubOrdes.complete;
  }

  filterOverview(stat: string) {
    if (this.extraFilter == stat) {
      this.selectedExtraFilter = false;
      this.extraFilter = '';
    } else {
      this.selectedExtraFilter = true;
      this.extraFilter = stat;
    }

    this.loadingOrder = true;
    this.showTable = false;

    setTimeout(() => {
      this.showTable = true;
      this.loadingOrder = false;
    }, 50);
  }
}
