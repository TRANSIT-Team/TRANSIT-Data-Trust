import { Component, Input, OnInit, ElementRef } from '@angular/core';
import {
  HateoasResourceService,
  PagedResourceCollection,
} from '@lagoshny/ngx-hateoas-client';
import * as echarts from 'echarts';

// @ts-ignore
import $ from 'jquery';
import { map, mergeMap, Observable, of, Subject } from 'rxjs';
import { CompanyService } from 'src/app/company/shared/company.service';
import { Order } from 'src/app/order/shared/order';
import { OrderService } from 'src/app/order/shared/order.service';
import { Company } from 'src/app/company/shared/company';
import { ChartData, OrderSummaryEntry } from '../shared/chart-data';
import { EChartsOption } from 'echarts';

@Component({
  selector: 'app-sunburst',
  templateUrl: './sunburst.component.html',
  styleUrls: ['./sunburst.component.css'],
})
export class SunburstComponent implements OnInit {
  hasData: boolean = true;
  chartData: ChartData = new ChartData();
  data: any = [];
  loadingChart: boolean = true;

  constructor(
    private elm: ElementRef,
    private hateoasResourceService: HateoasResourceService,
    private orderService: OrderService,
    private companyService: CompanyService
  ) {
    this.chartData.colors = [];
    this.chartData.values = [];
  }

  ngOnInit() {
    this.getOrderDataSunburst();
  }

  loadChart() {
    console.log('Loading chart');
    let chart = echarts.init($(this.elm.nativeElement).find('#chart')[0]);
    let option: EChartsOption;
    if (this.data === undefined || this.data.length === 0) {
      this.getOrderDataSunburst();
    }
    option = {
      visualMap: {
        type: 'continuous',
        min: 0,
        max: 10,
        inRange: {
          color: ['#2F93C8', '#AEC48F', '#FFDB5C', '#F98862'],
        },
      },
      series: {
        type: 'sunburst',
        data: this.data,
        radius: [0, '90%'],
        sort: undefined,
        emphasis: {
          focus: 'ancestor',
        },
        label: {},
      },
    };

    option && chart.setOption(option);
    this.loadingChart = false;
  }

  getOrderDataSunburst(): void {
    console.log('getOrderDataSunburst');
    of(0)
      .pipe(
        mergeMap(() => this.orderService.getOrdersSummary()),
        map((orderSummary: any) => {
          let summary: any[] = [];
          let accepted = new OrderSummaryEntry();
          accepted.name = 'Akzeptiert';
          accepted.value =
            orderSummary.ownOrders.accepted +
            orderSummary.acceptedSubOrdes.accepted;
          if (accepted.value != undefined && accepted.value > 0) {
            summary.push(accepted);
          }
          let created = new OrderSummaryEntry();
          created.name = 'Erstellt';
          created.value =
            orderSummary.ownOrders.created +
            orderSummary.acceptedSubOrdes.created;
          this.getSuborderSummary('CREATED').subscribe();
          if (created.value != undefined && created.value > 0) {
            summary.push(created);
          }
          let canceled = new OrderSummaryEntry();
          canceled.name = 'Abgebrochen';
          canceled.value =
            orderSummary.ownOrders.canceled +
            orderSummary.acceptedSubOrdes.canceled;
          this.getSuborderSummary('CANCELED').subscribe();
          if (canceled.value != undefined && canceled.value > 0) {
            summary.push(canceled);
          }
          let open = new OrderSummaryEntry();
          open.name = 'Offen';
          open.value =
            orderSummary.ownOrders.open + orderSummary.acceptedSubOrdes.open;
          this.getSuborderSummary('OPEN').subscribe();
          if (open.value != undefined && open.value > 0) {
            summary.push(open);
          }
          let processing = new OrderSummaryEntry();
          processing.name = 'In Bearbeitung';
          processing.value =
            orderSummary.ownOrders.processing +
            orderSummary.acceptedSubOrdes.processing;
          this.getSuborderSummary('PROCESSING').subscribe();
          if (processing.value != undefined && processing.value > 0) {
            summary.push(processing);
          }
          let requested = new OrderSummaryEntry();
          requested.name = 'Angefragt';
          requested.value =
            orderSummary.ownOrders.requested +
            orderSummary.acceptedSubOrdes.requested;
          this.getSuborderSummary('REQUESTED').subscribe();
          if (requested.value != undefined && requested.value > 0) {
            summary.push(requested);
          }
          let complete = new OrderSummaryEntry();
          complete.name = 'Abgeschlossen';
          complete.value =
            orderSummary.ownOrders.complete +
            orderSummary.acceptedSubOrdes.complete;
          this.getSuborderSummary('COMPLETE').subscribe();
          if (complete.value != undefined && complete.value > 0) {
            summary.push(complete);
          }
          if (summary.length == 0) {
            this.hasData = false;
          } else {
            this.hasData = true;
            this.data = summary;
          }
        })
      )
      .subscribe(() => {
        if (this.hasData) {
          this.loadChart();
          this.loadingChart = false;
        }
      });
  }

  getSuborderSummary(orderParentStatus: string): Observable<any[]> {
    return this.orderService.getOrdersSuborderSummary(orderParentStatus).pipe(
      map((orderSummary: any) => {
        let summary: any[] = [];
        let accepted = new OrderSummaryEntry();
        accepted.name = 'Akzeptiert';
        accepted.value = orderSummary.ownOrders.accepted;
        if (accepted.value != undefined && accepted.value > 0) {
          summary.push(accepted);
        }
        let created = new OrderSummaryEntry();
        created.name = 'Erstellt';
        created.value = orderSummary.ownOrders.created;
        if (created.value != undefined && created.value > 0) {
          summary.push(created);
        }
        let canceled = new OrderSummaryEntry();
        canceled.name = 'Abgebrochen';
        canceled.value = orderSummary.ownOrders.canceled;
        if (canceled.value != undefined && canceled.value > 0) {
          summary.push(canceled);
        }
        let open = new OrderSummaryEntry();
        open.name = 'Offen';
        open.value = orderSummary.ownOrders.open;
        if (open.value != undefined && open.value > 0) {
          summary.push(open);
        }
        let processing = new OrderSummaryEntry();
        processing.name = 'In Bearbeitung';
        processing.value = orderSummary.ownOrders.processing;
        if (processing.value != undefined && processing.value > 0) {
          summary.push(processing);
        }
        let requested = new OrderSummaryEntry();
        requested.name = 'Angefragt';
        requested.value = orderSummary.ownOrders.requested;
        if (requested.value != undefined && requested.value > 0) {
          summary.push(requested);
        }
        let complete = new OrderSummaryEntry();
        complete.name = 'Abgeschlossen';
        complete.value = orderSummary.ownOrders.complete;
        if (complete.value != undefined && complete.value > 0) {
          summary.push(complete);
        }
        if (summary.length > 0) {
          this.data.forEach(function (value: OrderSummaryEntry) {
            if (
              value.name === 'Akzeptiert' &&
              orderSummary.parentOrderStatus === 'ACCEPTED'
            ) {
              value.value = undefined;
              value.children = summary;
            }
            if (
              value.name === 'Offen' &&
              orderSummary.parentOrderStatus === 'OPEN'
            ) {
              value.value = undefined;
              value.children = summary;
            }
            if (
              value.name === 'In Bearbeitung' &&
              orderSummary.parentOrderStatus === 'PROCESSING'
            ) {
              value.value = undefined;
              value.children = summary;
            }
            if (
              value.name === 'Abgebrochen' &&
              orderSummary.parentOrderStatus === 'CANCELED'
            ) {
              value.value = undefined;
              value.children = summary;
            }
            if (
              value.name === 'Angefragt' &&
              orderSummary.parentOrderStatus === 'REQUESTED'
            ) {
              value.children = summary;
            }
            if (
              value.name === 'Abgeschlossen' &&
              orderSummary.parentOrderStatus === 'COMPLETE'
            ) {
              value.value = undefined;
              value.children = summary;
            }
            if (
              value.name === 'Erstellt' &&
              orderSummary.parentOrderStatus === 'CREATED'
            ) {
              value.value = undefined;
              value.children = summary;
            }
          });
          this.loadChart();
        }
        return summary;
      })
    );
  }
}
