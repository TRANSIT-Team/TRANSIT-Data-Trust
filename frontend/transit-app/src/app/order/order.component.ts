import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {OrderService} from "./shared/order.service";
import {Order} from "./shared/order";
import {catchError, iif, map, mergeMap, of, throwError} from "rxjs";
import {Feature} from "geojson";
import {DirectionService} from "../map/shared/direction.service";
import {Globals} from "../_core/globals";
import {GlobalService} from "../_core/shared/global.service";
import {Title} from "@angular/platform-browser";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogDeleteComponent} from "../dialog/dialog-delete.component";
import {SnackbarService} from "../_core/snackbar.service";
import {OrderMapComponent} from "./order-map.component";
import {DialogSubordersComponent} from "../dialog/dialog-suborders.component";
import {DialogOutsourceComponent} from "../dialog/dialog-outsource.component";
import {FunctionsService} from "../_core/functions.service";
import {DialogCmrComponent} from "../dialog/dialog-cmr.component";
import {Company} from "../company/shared/company";
import {DialogPriceComponent} from "../dialog/dialog-price.component";

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnInit {
  @ViewChild(OrderMapComponent, {static: false}) orderMapComponent: OrderMapComponent;

  order: any = undefined;
  orderLoaded = false;
  orderIsRefreshing = false;
  outsourceMapCompanies: Company[] = [];
  loadSubOrders = true;
  showComments = true;
  canOutsourceOrder = false;
  canDeleteOrder = false;
  canAcceptOrDenyOrder = false;
  startTime: any;
  orderStatusTmp: string = "";
  orderRightType: string = "MainOrder_without_subOrders";
  outsourceTooltip: string = "Outsourcen nur bei Status OFFEN möglich";
  showSubChat: any = false;
  subChatOrder: any;
  orderIsDelayed: boolean = false;

  constructor(
    public orderService: OrderService,
    private functionsService: FunctionsService,
    public globals: Globals,
    private globalService: GlobalService,
    public dialog: MatDialog,
    public snackbarService: SnackbarService,
    private router: Router,
    private route: ActivatedRoute,) {
    this.globals.dataLoadingText = "Auftrag laden";
  }

  ngOnInit(): void {
    this.getOrder();
  }

  openChat(item: any) {
    this.subChatOrder = item;
    this.showSubChat = true;
  }

  closeSubChat() {
    this.subChatOrder = null;
    this.showSubChat = false;
  }

  ngAfterViewInit() {
    let orderId: any = this.route.snapshot.paramMap.get('orderId')!;
    this.globalService.setGlobalPageTitel("Auftrag #" + orderId);
  }

  ngOnDestroy() {
    this.globalService.setGlobalPageTitel(undefined);
  }

  getOrder() {
    let orderId: any = this.route.snapshot.paramMap.get('orderId')!;
    this.startTime = new Date();
    this.orderService.getOrderStatus(orderId).pipe(
      map((stat: any) => {
        if (stat.orderStatus == "REJECTED" || stat.orderStatus == "REVOKED") {
          this.snackbarService.openSnackBar('Sie haben keinen Zugriff auf diesen Auftrag', 'Ok', 'red-snackbar');
          this.router.navigate(['/orders/'], {relativeTo: this.route});
        }
        return stat
      }),
      mergeMap((stat: any) => iif(() => stat.orderStatus != "REJECTED" && stat.orderStatus != "REVOKED", this.orderService.getOrder(orderId), of(0))),
      catchError((err, caught) => {
        let et: string = 'Es ist ein Fehler aufgetreten';
        //console.log(err);
        if (err.error.message != undefined) {
          et = err.error.message.toString();
        }
        this.snackbarService.openSnackBar(et, 'Ok', 'red-snackbar');
        this.globals.dataLoading = false;
        return throwError(err);
      })
    )
      .subscribe((order: Order) => {

        if (order.deleted) {
          this.snackbarService.openSnackBar('Sie haben keinen Zugriff auf diesen Auftrag', 'Ok', 'red-snackbar');
          this.router.navigate(['/orders/'], {relativeTo: this.route});
        }
        //console.log(order);
        this.orderStatusTmp = order.orderStatus!;
        this.orderLoaded = true;
        this.order = order;
        this.setUpOrderActionsRights();
        this.globals.dataLoading = false;
        //this.openOutsourceDialog();
      });
  }

  acceptOrderRequest() {


    const dialogRef = this.acceptOrderRequestPriceDialog(this.order.id, 'Angebotspreis');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result != 'canceled' && result != undefined) {
        this.savePriceInformations(result, "ACCEPTED");

      }
    });


  }

  acceptOrderRequestPriceDialog(id: string, text: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: 'Endgültig löschen?',
      text: text,
    };
    return this.dialog.open(DialogPriceComponent,
      dialogConfig);
  }


  savePriceInformations(price: any, newStatus: any) {

    let orderPrice: any = {id: this.order.id, price: price};
    let order: any = {id: this.order.id, orderStatus: "ACCEPTED", patchOrderStatus: "ACCEPTED"};
    this.globals.dataLoading = true;
    this.globals.dataLoadingText = "Anfrage verarbeiten";
    this.orderService.patchOrder(orderPrice).pipe(
      map(() => {
        this.order.price = price;
      }),
      mergeMap(() => this.orderService.postOrderStatusById(this.order.id, newStatus).pipe())
    ).subscribe((response: any) => {

      let descriptionStatus: string = this.orderService.getOrderStatusDescription(newStatus);
      this.snackbarService.openSnackBar('Orderstatus geändert auf: ' + descriptionStatus, 'Ok', '');
      this.order.orderStatus = newStatus;
      this.orderStatusTmp = newStatus;
      this.order.orderStatusDescription = this.orderService.getOrderStatusDescription(newStatus);
      this.order.orderStatusSelectionFlow = this.orderService.getOrderStatusFlow(this.order.orderStatus);
      this.globals.dataLoading = false;
      this.canAcceptOrDenyOrder = false;
      this.setUpOrderActionsRights();

    });

  }


  denyOrderRequest() {
    this.sentOrderStatus("REJECTED");
  }

  sentOrderStatus(newStatus: string) {
    let order: any = {id: this.order.id, orderStatus: newStatus, patchOrderStatus: newStatus};
    this.globals.dataLoading = true;
    this.globals.dataLoadingText = "Anfrage verarbeiten";
    this.orderService.postOrderStatusById(this.order.id, newStatus).pipe(
      map(() => {
      }),
    ).subscribe((response: any) => {

      let descriptionStatus: string = this.orderService.getOrderStatusDescription(newStatus);
      this.snackbarService.openSnackBar('Orderstatus geändert auf: ' + descriptionStatus, 'Ok', '');
      this.order.orderStatus = newStatus;
      this.orderStatusTmp = newStatus;
      this.order.orderStatusDescription = this.orderService.getOrderStatusDescription(newStatus);
      this.order.orderStatusSelectionFlow = this.orderService.getOrderStatusFlow(this.order.orderStatus);
      this.globals.dataLoading = false;
      this.canAcceptOrDenyOrder = false;
      this.setUpOrderActionsRights();

      if (newStatus == "REJECTED") {
        this.router.navigate(['/orders/'], {relativeTo: this.route});
      }
    });
  }

  refreshOrderMap() {
    this.orderMapComponent.initializeMap();
  }

  reloadOrder() {
    this.globals.dataLoadingText = "Auftrag neuladen";
    this.globals.dataLoading = true;
    this.order = null;
    this.getOrder();
  }

  openDeleteDialog(id: string, text: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: 'Endgültig löschen?',
      text: text,
    };
    return this.dialog.open(DialogDeleteComponent,
      dialogConfig);
  }


  deleteOrder() {
    let subOrderActive: boolean = false;
    if (this.order.subOrders.length > 0) {
      this.order.subOrders.forEach((subO: any) => {
        if (subO.orderStatus != "REJECTED" && subO.orderStatus != "REVOKED") {
          subOrderActive = true;
        }
      });
    }

    if (subOrderActive) {
      this.snackbarService.openSnackBar('Es müssen erst alle Sub-Aufträge auf dem Status WIDERRUFEN oder ABGELEHNT sein.', 'Ok', 'red-snackbar');
    } else {
      this.orderService.deleteOrder(this.order.id).subscribe(
        (response: any) => {
          this.snackbarService.openSnackBar('Auftrag gelöscht!', 'Ok', 'red-snackbar');
          this.router.navigate(['/orders/'], {relativeTo: this.route});
        });
    }
  }


  openOutsourceDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "96vw";
    dialogConfig.height = "96vh";
    dialogConfig.maxWidth = "100vw";
    dialogConfig.data = {
      order: this.order,
      outsourceMapCompanies: this.outsourceMapCompanies
    };

    const dialogRef = this.dialog.open(DialogOutsourceComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {


      if(result==undefined){
        return;
      }
      if (result.text == 'reloadSuborders') {
        this.order = undefined;
        this.getOrder();
      }
      if (result.text != 'canceled') {

      }

      if (result.data != undefined) {
        this.outsourceMapCompanies = result.data;
      }
    });
  }


  openCmrDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "95vw";
    dialogConfig.height = "95vh";
    dialogConfig.maxWidth = "100vw";
    dialogConfig.data = {
      order: this.order
    };

    const dialogRef = this.dialog.open(DialogCmrComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'reloadSuborders') {
      }
      if (result != 'canceled') {
      }
    });
  }

  refreshOrder() {
    this.orderStatusTmp = this.order.orderStatus;
    this.orderIsRefreshing = true;
    this.setUpOrderActionsRights();

    setTimeout(() => {
      this.orderIsRefreshing = false;
    }, 700);

  }

  setUpOrderActionsRights() {

    this.order.orderStatusDescription = this.orderService.getOrderStatusDescription(this.order.orderStatus);
    this.outsourceTooltip = "Outsourcen nur bei Status OFFEN möglich";

    this.orderRightType = "MainOrder_without_subOrders";
    let subOrders: any = [];
    if (this.order.suborderType) {
      this.orderRightType = "MainSubOrder_without_subOrders";
    }

    if (this.order.subOrders.length > 0) {
      this.orderRightType = "MainOrder_with_subOrders";
      subOrders = this.order.subOrders;
    }
    this.order.orderRightsGlobal = this.orderService.getOrderRights(this.orderRightType, this.order.orderStatus, subOrders)
    if (this.order.orderRightsGlobal!.can.outsource) {
      this.outsourceTooltip = "Auftrag outsourcen";
    } else {
      this.outsourceTooltip = "Outsourcen nur bei Status OFFEN möglich";
    }

    let now: Date = new Date((new Date().getTime()));
    let check: Date = new Date((this.order.pickUpDateTo));
    if (check <= now) {
      this.orderIsDelayed = true;
      this.outsourceTooltip = "Die aktuelle Zeit liegt nach der Abholzeit. Pünktliche Abholung nicht mehr möglich!"
    } else {
      this.orderIsDelayed = false;
    }

    if (this.order.orderStatus == 'CANCELED') {
      this.outsourceTooltip = "Auftrag outsourcen"
    }


  }

  goBack() {
    if (this.order.suborderType && !this.order.isMyOrder) {
      this.router.navigate(['../' + this.order.parentOrderId.id], {relativeTo: this.route});
    } else {

      this.router.navigate(['/orders'], {relativeTo: this.route});
    }
  }

  deleteOrderDialog() {
    const dialogRef = this.openDeleteDialog(this.order.id, 'Den Hauptauftrag endgültig löschen?');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted') {
        this.deleteOrder();
      }
    });
  }

  reloadSubOrders() {
    this.loadSubOrders = false;
    this.showComments = false;
    setTimeout(() => {
      this.loadSubOrders = true;
      this.showComments = true;
    }, 700);
  }


  copyOrder() {
    this.globals.dataLoading = true;
    this.globals.dataLoadingText = "Auftrag kopieren...";

    this.orderService.postCopyOrderById(this.order.id).subscribe((response: any) => {
      this.snackbarService.openSnackBar('Auftrag wurde neu angelegt.', 'Ok', 'green-snackbar');
      this.router.navigate(['/orders/order/' + response.id], {relativeTo: this.route});
      this.globals.dataLoading = false;
    });

  }
}
