import {Component, Input, OnInit} from '@angular/core';
import {Order, OrderProperty} from "./shared/order";

@Component({
  selector: 'app-order-parentorder-info',
  templateUrl: './order-parentorder-info.component.html',
  styleUrls: ['./order-parentorder-info.component.css']
})
export class OrderParentorderInfoComponent implements OnInit {
  @Input() order: Order;
  loading: boolean = false;
  parentOrderInformations: string = "";
  ParentOrderCompanyName:any="";
  ParentOrderContactPersonName:any="";
  ParentOrderContactPersonEmail:any="";
  ParentOrderContactPersonPhone:any="";
  constructor() {
  }

  ngOnInit(): void {
    this.mapParentOrderInformations();
  }




  mapParentOrderInformations(){
    if (this.order.orderProperties != undefined && this.order.suborderType) {

      let s: any = this.order.orderProperties.filter((a: OrderProperty) => a.key == "ParentOrderCompanyName")[0];

      if (s != undefined && s.value != "") {
        // console.log(s);
        this.parentOrderInformations += '<span>' + s.value + '</span><br/>';
        this.ParentOrderCompanyName = s.value;
      }
      s = this.order.orderProperties.filter((a: OrderProperty) => a.key == "ParentOrderContactPersonName")[0];
      if (s != undefined && s.value != "") {
        this.parentOrderInformations += '<span>' + s.value + '</span><br/>';
        this.ParentOrderContactPersonName = s.value;
      }
      s = this.order.orderProperties.filter((a: OrderProperty) => a.key == "ParentOrderContactPersonEmail")[0];
      if (s != undefined && s.value != "") {
        this.parentOrderInformations += '<span>' + s.value + '</span><br/>';
        this.ParentOrderContactPersonEmail = s.value;
      }
      s = this.order.orderProperties.filter((a: OrderProperty) => a.key == "ParentOrderContactPersonPhone")[0];
      if (s != undefined && s.value != "") {
        this.parentOrderInformations += '<span>' + s.value + '</span>';
        this.ParentOrderContactPersonPhone = s.value;
      }
    }
  }

}
