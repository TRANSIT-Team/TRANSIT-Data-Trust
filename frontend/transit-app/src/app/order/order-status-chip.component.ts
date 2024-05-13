import {Component, Input, OnInit} from '@angular/core';
import {Order} from "./shared/order";
import {OrderService} from "./shared/order.service";

@Component({
  selector: 'app-order-status-chip',
  templateUrl: './order-status-chip.component.html',
  styleUrls: ['./order-status-chip.component.css']
})
export class OrderStatusChipComponent implements OnInit {
  @Input() orderStatus?: string = "";
  @Input() small: boolean = false;
  @Input() orderStatusDescription?: string = "";


  constructor(private orderService: OrderService) {
  }

  ngOnInit(): void {
    if(this.orderStatusDescription==""){
      this.orderStatusDescription = this.orderService.getOrderStatusDescription(this.orderStatus!);
    }
  }

}
