import {Component, OnInit} from '@angular/core';
import {map, mergeMap, of} from "rxjs";
import {OrderService} from "../order/shared/order.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  barchartData: any;
  cartDataDailySummary: any;

  constructor(private orderService:OrderService) {
  }

  ngOnInit(): void {

    this.getCartDataDailySummary();
    setTimeout(() => {
     this.loadBarChart();
    }, 2000);
  }


  getCartDataDailySummary(): void {
    of(0)
      .pipe(
        mergeMap(() => this.orderService.getOrdersWeekly()),
        map((data: any) => {
          this.cartDataDailySummary = data;
        })
      )
      .subscribe();
  }


  loadBarChart() {

    this.barchartData = [{
      day: 'Monday',
      oranges: 3,
    }, {
      day: 'Tuesday',
      oranges: 2,
    }, {
      day: 'Wednesday',
      oranges: 3,
    }, {
      day: 'Thursday',
      oranges: 4,
    }, {
      day: 'Friday',
      oranges: 6,
    }, {
      day: 'Saturday',
      oranges: 11,
    }, {
      day: 'Sunday',
      oranges: 4,
    }];

  }


}
