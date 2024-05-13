import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Order} from "./shared/order";
import {OrderService} from "./shared/order.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogAddressComponent} from "../dialog/dialog-address.component";
import {DirectionService} from "../map/shared/direction.service";
import {Address} from "../address/shared/address";
import {SnackbarService} from "../_core/snackbar.service";

@Component({
  selector: 'app-order-address',
  templateUrl: './order-address.component.html',
  styleUrls: ['./order-address.component.css']
})
export class OrderAddressComponent implements OnInit {
  @Input() order: Order;
  @Output() refreshOrder = new EventEmitter<any>();

  distanceOrder: any = "...";
  travelTimeOrder: any = "...";

  addressFrom: Address = new Address();
  addressTo: Address = new Address();

  constructor(private orderService: OrderService, public dialog: MatDialog, private directionService: DirectionService, public snackbarService: SnackbarService) {
  }

  ngOnInit(): void {

    this.setUpAddresses();
    this.getDistance();
  }

  setUpAddresses() {
    if (this.order.addressFrom != undefined) {
      this.addressFrom = this.order.addressFrom;
    }

    if (this.order.addressTo != undefined) {
      this.addressTo = this.order.addressTo;
    }

  }

  getDistance() {

    let points: any = this.orderService.getAddressPointsFromOrder(this.order);

    if (points.length > 0) {
      let apiUrl = this.directionService.getDirectionApiUrl(points, false, false);
      this.directionService.getDirectionRoute(apiUrl).subscribe((data: any) => {
          if (data.routes != undefined) {
            if (data.routes[0] != undefined) {
              //meters to kilometers
              this.distanceOrder = parseFloat((data.routes[0].distance / 1000).toFixed(2));
              // seconds to hours
              this.travelTimeOrder = parseFloat((data.routes[0].duration / 3600).toFixed(2));
            }
          }
        }
      );
    }

  }


  openAddressDialog(type: string) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80vw";
    dialogConfig.height = "80vh";
    dialogConfig.data = {
      type: type
    };

    const dialogRef = this.dialog.open(DialogAddressComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result != 'canceled') {
        if (type == 'from') {
          this.addressFrom = result;
        }
        if (type == 'to') {
          this.addressTo = result;
        }
      }
    });
  }

  updateOrderAddress(type: string) {

    let tmpOrder: any;
    tmpOrder = {
      id: this.order.id,
      addressIdFrom: {id: this.addressFrom!.id!},
      addressIdTo: {id: this.addressTo!.id!},
    };

    if (type == 'from') {
      tmpOrder = {
        id: this.order.id,
        addressIdFrom: {id: this.addressFrom!.id!}
      };
    }

    if (type == 'to') {
      tmpOrder = {
        id: this.order.id,
        addressIdTo: {id: this.addressTo!.id!},
      };
    }

    this.orderService.patchOrder(tmpOrder).subscribe((order: any) => {
      if (type == 'from') {
        this.order.addressFrom = this.addressFrom;
      }
      if (type == 'to') {
        this.order.addressTo = this.addressTo;
      }
      this.getDistance();
      this.refreshOrder.emit();
      this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');
    })
  }


}
