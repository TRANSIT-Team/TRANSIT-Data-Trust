import { Component, Input, OnInit } from '@angular/core';

import {
  BrowserModule,
  BrowserTransferStateModule,
} from '@angular/platform-browser';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { DxChartModule } from 'devextreme-angular';
import { OrderService } from '../../order/shared/order.service';
import { map, mergeMap, of } from 'rxjs';
import { OrdersWeekly } from '../../order/shared/order';

@Component({
  selector: 'app-barchart',
  templateUrl: './barchart.component.html',
  styleUrls: ['./barchart.component.css'],
})
export class BarchartComponent implements OnInit {
  @Input() chartData: any = [];
  @Input() chartTitle: any = "";
  @Input() chartName: any = "";
  @Input() argumentField: any = "";
  @Input() valueField: any = "";
  @Input() color: any = "#ffaa66";

  constructor(private orderService: OrderService) {

  }

  ngOnInit(): void {

  }
  customizeTooltip = (info:any) => {
    return { text: `${info.seriesName}: ${info.valueText}` };
  };

}
