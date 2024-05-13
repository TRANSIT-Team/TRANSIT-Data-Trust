import {Component, Input, OnInit} from '@angular/core';
import CustomStore from "devextreme/data/custom_store";
import {HttpParams} from "@angular/common/http";
import {Customer} from "../company/shared/company";
import {OrderService} from "../order/shared/order.service";
import {FunctionsService} from "../_core/functions.service";
import {Order, OrderState} from "../order/shared/order";

@Component({
  selector: 'app-orders-table',
  templateUrl: './orders-table.component.html',
  styleUrls: ['./orders-table.component.css']
})
export class OrdersTableComponent implements OnInit {

  @Input() orderStatusFilter: string = "";
  dataSource: any = [];
  currentPage: number = 0;
  currentPageSize: number = 20;
  events: Array<string> = [];
  orderStatusFilterData: any = [];
  currentFilter: any;
  defaultFilter: string = "deleted==false;newOrderId=isnull=''";
  readonly allowedPageSizes = [10, 20, 50];

  constructor(private orderService: OrderService, private functionsService: FunctionsService) {


    this.prepareFilter();


  }


  ngOnInit(): void {

    this.setDataSource();
  }

  isNotEmpty(value: any): boolean {
    return value !== undefined && value !== null && value !== '';
  }

  setDataSource() {


    let p: any = {};
    if (this.orderStatusFilter != "") {
      this.defaultFilter += ';orderStatus==' + this.orderStatusFilter;
    } else {
      this.defaultFilter += ';orderStatus=out=(REJECTED,REVOKED)';
    }


    this.dataSource = new CustomStore({
        key: 'id',
        load: (loadOptions: any) => {
          let transformed: boolean = false;
          const params: HttpParams = new HttpParams();
          p['filter'] = this.defaultFilter;
          ['filter',
            'skip',
            'take',
            'searchExpr',
            'searchOperation',
            'searchValue',
            'requireTotalCount',
            'requireGroupCount',
            'sort',
            'select',
            'totalElements',
            'group',
            'groupSummary',
          ].forEach((i) => {
            // console.log('filter-devExpress', p['filter']);

            if (p['searchOperation'] == 'contains' && (p['filter'] != this.defaultFilter)) {
              if (!transformed) {
                console.log('filter-devExpress', p['filter']);
                let filterNew: string = this.defaultFilter + this.functionsService.transformDevExtremeHeaderRowFilter(p['filter']);
                p['filter'] = filterNew;
                console.log('filter-transformed', filterNew);
                transformed = true;


              }
            }

            if (i in loadOptions && this.isNotEmpty(loadOptions[i])) {
              params.set(i, JSON.stringify(loadOptions[i]));

              if (i === 'sort' && loadOptions[i][0]) {
                let {selector, desc} = loadOptions[i][0];
                //p[i] = `${selector}&${selector}.dir=${desc ? 'desc' : 'asc'}`;
                p[i] = `${selector},${desc ? 'desc' : 'asc'}`;
              } else {
                p[i] = loadOptions[i].toString();
              }
            }
          });

          return this.orderService.getMyOrders(p, true)
            .toPromise()
            .then((data: any) => ({
              data: data ? data.resources : [], // If data is undefined, use an empty array
              totalCount: data ? data.totalElements : 0, // If data is undefined, use 0
              // summary, groupCount, etc.
            }))
            .catch((error: any) => {


              console.log(error);
              //  throw '';
              // Display a custom error message in your application UI
              //this.showCustomErrorMessage("Data source error: " + error.message);
              return Promise.reject(error.message);
            });
        },
      }
    );


  }

  changeDefaultFilter() {


  }

  prepareFilter() {
    let tmpOrder: Order = new Order();
    let statusOrders = tmpOrder.orderStatusSelection
      .filter((s: any) => s.name !== "ALL" && s.name !== "REVOKED" && s.name !== "REJECTED")
      .sort((a: any, b: any) => a.description.localeCompare(b.description));

    statusOrders.forEach((oS: OrderState) => {
      let tmpFilterData: any = {
        text: oS.description,
        value: oS.name,
      };
      this.orderStatusFilterData.push(tmpFilterData);
    });
  }
}
