import {Component, Input, OnInit, ElementRef} from '@angular/core';
import {HateoasResourceService, PagedResourceCollection} from '@lagoshny/ngx-hateoas-client';
import * as echarts from "echarts";

// @ts-ignore
import $ from 'jquery';
import {map, mergeMap} from 'rxjs';
import {CompanyService} from 'src/app/company/shared/company.service';
import {Order} from 'src/app/order/shared/order';
import {OrderService} from 'src/app/order/shared/order.service';
import {Company} from 'src/app/company/shared/company';
import {ChartData, ChartDataValue} from '../shared/chart-data';

@Component({
  selector: 'app-piechart',
  templateUrl: './piechart.component.html',
  styleUrls: ['./piechart.component.css']
})


export class PiechartComponent implements OnInit {
  hasData: boolean = true;
  chartData: ChartData = new ChartData();
  colorList: string[] = ['#08b932', '#ffeb3b', '#304ffe', '#FF3213',
    '#d03783', '#1b7fc0', '#bb7166']
  maxCount: number = 0;
  loadingChart:boolean=true;

  constructor(private elm: ElementRef,
              private hateoasResourceService: HateoasResourceService,
              private orderService: OrderService,
              private companyService: CompanyService) {

    this.chartData.colors = [];
    this.chartData.values = [];
  }

  ngOnInit() {
    this.getOrderData();
  }

  loadChart() {

    let chart = echarts.init($(this.elm.nativeElement).find('#chart')[0]);
    chart.setOption({
      legend: {
        top: 'bottom'
      },
      toolbox: {
        show: true,
        feature: {
          mark: {show: true},
          dataView: {show: true, readOnly: true},
          restore: {show: false},
          saveAsImage: {show: true}
        }
      },
      //color: ['#08b932', '#ffeb3b', '#304ffe', '#FF3213'],
      color: this.chartData.colors,
      tooltip: {
        trigger: 'item'
      },

      visualMap: {
        show: false,
        min: -0,
        max: this.maxCount,
        inRange: {
          colorLightness: [1, 0]
        }
      },
      series: [
        {
          name: 'Auftragsübersicht',
          type: 'pie',
          radius: '55%',
          center: ['50%', '50%'],
          roseType: 'radius',
          colorBy: 'data',
          label: {
            color: '#666666'
          },
          labelLine: {
            lineStyle: {
              color: 'rgba(0, 0, 0, 0.3)'
            },
            smooth: 0.2,
            length: 10,
            length2: 20
          },
          itemStyle: {
            color: '#0d6efd',
            shadowBlur: 20,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          },
          animationType: 'scale',
          animationEasing: 'elasticOut',
          data: this.chartData.values.sort(function (a, b) {
            return a.value - b.value;
          }),
        }
      ]
    })

  }

  getOrderData() {

    let ownCompany: any;
    this.companyService.getOwnCompany().pipe(
      map((company: Company) => {
        ownCompany = company;
      }),
      //get ParentOrders
      mergeMap(() => this.orderService.getMyOrders()),
      map((orders: PagedResourceCollection<Order>) => {

        let showOrders: Order[] = [];

        //own orders
        showOrders.push(...orders.resources.filter((order: Order) => order.suborderType == false && order.companyId?.id == ownCompany.id));

        //orders accept from other companies
        showOrders.push(...orders.resources.filter((order: Order) => order.suborderType == true && order.companyId?.id == ownCompany.id));

        if (showOrders.length == 0) {
          this.hasData = false;
        }

        let tmpOrder: Order = new Order();


        let orderStatusSelection: string[] = ["OPEN", "PROCESSING", "COMPLETE", "REJECTED", "REQUESTED", "CANCELED"];

        this.maxCount = orders.resources.length;
        orderStatusSelection.forEach((stat: string, index) => {
          let tmpOrders: Order[] = [];


          tmpOrders = showOrders.filter((order: Order) => order.orderStatus == stat);


          let color: string = "";
          if (stat == "OPEN") {
            color = "#304ffe";
          }
          if (stat == "COMPLETE") {
            color = "#08b932";
          }
          if (stat == "CANCELED") {
            color = "#FF3213";
          }
          if (stat == "REQUESTED") {
            color = "#ffeb3b";
          }
          if (stat == "REJECTED") {
            color = "#bb7166";
          }
          if (stat == "PROECESSING") {
            color = "#d03783";
          }

          if (tmpOrders.length > 0) {
            this.addChartData(tmpOrders.length, stat, color)
          }

        })


        //console.log(this.chartData);

      })
    ).subscribe(() => {
      this.loadingChart=false;
      if (this.hasData) {
        this.loadChart();

      }

    });


  }

  addChartData(value: number, name: string, color: string) {
    let tmpChartDataValue: ChartDataValue = new ChartDataValue();
    tmpChartDataValue.value = value;
    tmpChartDataValue.name = name;
    this.chartData.values.push(tmpChartDataValue);
    this.chartData.colors.push(color);
  }

}
