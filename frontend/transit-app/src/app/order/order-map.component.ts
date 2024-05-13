import {Component, Input, OnInit} from '@angular/core';
import {MapService} from "../map/shared/map.service";
import {Feature} from "geojson";
import {Order} from "./shared/order";
import {OrderService} from "./shared/order.service";

@Component({
  selector: 'app-order-map',
  templateUrl: './order-map.component.html',
  styleUrls: ['./order-map.component.css']
})
export class OrderMapComponent implements OnInit {
  @Input() order: Order;

  constructor(private mapService: MapService, private orderService: OrderService) {
  }

  ngOnInit(): void {
    this.initializeMap();
  }

  public initializeMap() {
    let points: Feature[] = [];
    points = this.orderService.getAddressPointsFromOrder(this.order);
    if (points.length > 0) {
      this.mapService.buildMap("order-route-map", points, false);
    }
  }
}
