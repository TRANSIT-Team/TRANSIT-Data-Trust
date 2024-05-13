import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {catchError, forkJoin, iif, map, mergeMap, Observable, of, Subscription, throwError, timer} from "rxjs";
import {EntityRightService} from "../entity-rights/shared/entity-right.service";
import {SnackbarService} from "../_core/snackbar.service";
import {OutsourceCompany} from "../outsource/shared/outsource";
import {
  CompanyDefinedOutsource,
  CompanyDefinedOutsourceProperty, CompanyDefinedOutsourceSubProperty, EntryEntityDto,
  PropertySelection
} from "../entity-rights/shared/entity-right";
import {Order, OrderComment, OrderState, OrderStatus} from "./shared/order";
import {OrderService} from "./shared/order.service";
import {UniqueResource} from "../_core/AbstractResource";
import {Package} from "../packages/shared/package";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogDeleteComponent} from "../dialog/dialog-delete.component";
import {Company} from "../company/shared/company";
import {DialogSubordersComponent} from "../dialog/dialog-suborders.component";
import {DialogAddressComponent} from "../dialog/dialog-address.component";
import {Globals} from "../_core/globals";
import {ActivatedRoute, Router} from "@angular/router";
import {FunctionsService} from "../_core/functions.service";
import {DialogTemplateComponent} from "../dialog/dialog-template.component";
import {OutsourceService} from "../outsource/shared/outsource-component.service";

@Component({
  selector: 'app-order-suborders',
  templateUrl: './order-suborders.component.html',
  styleUrls: ['./order-suborders.component.css']
})
export class OrderSubordersComponent implements OnInit {
  @Input() order: Order;
  @Input() startTime: any;

  @Output() refreshOrder = new EventEmitter<any>();
  @Output() reloadOrder = new EventEmitter<any>();
  @Output() reloadSubOrders = new EventEmitter<any>();
  @Output() openChat = new EventEmitter<any>();

  outsourceCompanies: OutsourceCompany[] = [];
  companyDefined: CompanyDefinedOutsource[] = [];
  //subOrders: Order[] = [];
  orderStatus?: string = "";
  orderStatusTmp: string = "";
  subOrdersIds: UniqueResource[] = [];
  orderStatusSelection: string[] = [];
  outsourcedOrder = false;
  outsourcedOrderStarted = false;
  outsourcedOrderAccepted = false;
  outsourcedOrderFinished = false;
  outsourcedOrderRevoked = false;
  outsourcedOrderCanceled = false;
  subOrderSubscripton: Subscription;
  loading = false;

  constructor(private entityRightService: EntityRightService,
              public snackbarService: SnackbarService,
              private orderService: OrderService,
              private outsourceService: OutsourceService,
              public dialog: MatDialog,
              private globals: Globals,
              private router: Router,
              private route: ActivatedRoute,
              private functionsService: FunctionsService) {
  }

  ngOnInit(): void {
    this.getSubOrdersFromOrder();
  }

  ngOnDestroy() {
    this.unsubscribeSubOrderStatus();
  }

  unsubscribeSubOrderStatus() {
    if (this.subOrderSubscripton != undefined) {
      this.subOrderSubscripton.unsubscribe();
    }

  }

  changeSubOrderStatus(subOrder: Order, newStatus: string) {
    this.loading = true;

    let tmpOrders: Order[] = this.order.subOrders;
    let s: Order = tmpOrders.filter((sO: any) => sO.id == subOrder.id)[0];
    if (s != undefined) {
      s.orderStatus = newStatus;
      s.orderStatusDescription = this.orderService.getOrderStatusDescription(newStatus);
    }

    this.sortByOrderStatus();
    this.setOutsurceInfo();

    this.snackbarService.openSnackBar('Ein Sub-Auftrag wurde auf Status ' + subOrder.orderStatusDescription + ' gesetzt.', 'Ok', 'green-snackbar');
    setTimeout(() => {
      this.loading = false;
    }, 1500);

    this.refreshOrder.emit(this.getOutsourceInfo());
    this.reloadSubOrders.emit();
    this.unsubscribeSubOrderStatus();
  }

  checkSubOrdersStatus() {

    this.subOrderSubscripton = timer(0, 10000).pipe(
      mergeMap(() => this.getSubOrdersStatus()),
    ).subscribe((responseSubOrders) => {
      this.order.subOrders.forEach((subOrder: any) => {
        let tmpResponse: any = responseSubOrders.filter((sO: any) => sO.id == subOrder.id)[0];
        if (tmpResponse != undefined) {
          if (subOrder.orderStatus != tmpResponse.orderStatus) {
            this.changeSubOrderStatus(subOrder, tmpResponse.orderStatus);
          }
        }
      });
    });
  }


  getSubOrdersStatus(): Observable<any> {
    let subOrdersTmp: any = this.order.subOrders.filter((o: Order) => o.orderStatus != "REJECTED" && o.orderStatus != "REVOKED")

    return this.orderService.getOrdersStatus(subOrdersTmp!).pipe(
    );
  }


  // Function to sort orders by orderStatus
  sortByOrderStatus() {
    this.loading = true;
    this.order.subOrders.sort(this.orderService.sortOrderStatus);
    setTimeout(() => {
      this.loading = false;
    }, 700);
  }

  getSubOrdersFromOrder() {

    if (this.order.suborderIds != undefined && this.order.suborderIds.length > 0) {
      this.subOrdersIds = this.order.suborderIds;
      this.loading = true;


      of(0).pipe(
        mergeMap(() => iif(() => this.order.suborderIds!.length > 0, this.orderService.getSubOrdersFromOrder(this.order.suborderIds!, this.order), of(this.order.suborderIds))),
        map((subOrders: any) => {
          // this.order.subOrders = subOrders;
          this.order.subOrders = subOrders;
          return subOrders;
        }),
        mergeMap((subOrders: any) => iif(() => subOrders.length > 0, this.orderService.getOrdersCompany(subOrders), of(0))),
        map((companies: any) => {
          if (companies != 0) {
            companies.forEach((com: Company, index: any) => {
              this.outsourceCompanies.push({company: com, sort: index});
              this.order.subOrders.filter((sO: any) => sO.companyId!.id == com.id)[0].companyId = com;
            })
          }
        }),
        //mergeMap(() => this.getSubOrdersEntityRights())
      ).subscribe(() => {
        this.loading = false;
        this.sortByOrderStatus();
        const endTime = new Date();
        // Calculate the time difference
        const timeDifference = endTime.getTime() - this.startTime.getTime();
        const timeDifferenceInSeconds = timeDifference / 1000;
        console.log('Loadingtime:', timeDifferenceInSeconds);
        this.setOutsurceInfo();
        this.refreshOrder.emit(this.getOutsourceInfo());
      });
    } else {
      this.refreshOrder.emit(this.getOutsourceInfo());
    }


    this.checkSubOrdersStatus();
  }

  setOutsurceInfo() {

    let outsourceOrderOneIsActive: boolean = false;
    this.order.subOrders.forEach((subOrder: Order) => {

      if (subOrder.orderStatus != "REVOKED" && subOrder.orderStatus != "REJECTED") {
        this.outsourcedOrderStarted = true;
      }

      if (subOrder.orderStatus == "ACCEPTED") {
        this.outsourcedOrderAccepted = true;
      }

      if (subOrder.orderStatus == "OPEN") {
        this.outsourcedOrder = true;
        this.order.isOutsourced = true;
      }

      if (subOrder.orderStatus == "COMPLETE") {
        this.outsourcedOrderFinished = true;
        this.order.isOutsourced = true;
      }

      if (subOrder.orderStatus == "REVOKED") {
        this.outsourcedOrderRevoked = true;
      }

      if (subOrder.orderStatus == "CANCELED") {
        this.outsourcedOrderCanceled = true;
      }

      if (subOrder.orderStatus == "OPEN" || subOrder.orderStatus == "PROCESSING" || subOrder.orderStatus == "COMPLETE") {
        outsourceOrderOneIsActive = true;
      }
    });


    if (this.outsourcedOrderCanceled && !outsourceOrderOneIsActive) {
      this.outsourcedOrder = false;
      this.order.isOutsourced = false;
    }

    if (outsourceOrderOneIsActive) {
      this.outsourcedOrder = true;
      this.order.isOutsourced = true;
    }


  }

  getOutsourceInfo(): any {
    let oInfo: any = {
      outsourcedOrder: this.outsourcedOrder,
      outsourcedOrderStarted: this.outsourcedOrderStarted,
      outsourcedOrderAccepted: this.outsourcedOrderAccepted,
      outsourcedOrderFinished: this.outsourcedOrderFinished
    }
    return oInfo;
  }

  showSubOrder(item: any) {
    this.router.navigate(['../' + item.id], {relativeTo: this.route});
  }

  openChatBox(item: any) {
    this.openChat.emit(item);
  }

  acceptRequestClick(item: any) {
    this.acceptParentSubOrderWithRovoke(item);
  }

  acceptParentSubOrderWithRovoke(subOrder: any) {
    this.globals.dataLoadingText = "Rechte werden aktualisiert";
    this.globals.dataLoading = true;
    this.loading = true;
    this.unsubscribeSubOrderStatus();
    let orderPrice: any = {id: this.order.id, outsourceCost: subOrder.price};
    of(0).pipe(
      mergeMap(() => this.orderService.patchOrder(orderPrice)),
      map(() => {
        this.order.outsourceCost = subOrder.price;
      }),
      mergeMap(() => this.entityRightService.getSubOrderEntityRights(subOrder, true)),
      map((coDef: CompanyDefinedOutsource[]) => {


        // remove contactPerson from outsourcing rights

        coDef.forEach((cDefProp: CompanyDefinedOutsource) => {

          let indexOfList: number = cDefProp.companyDefinedProperties.findIndex((a: CompanyDefinedOutsourceProperty) => a.typeClazz == "contactPerson");

          // removing from companyDefinedProperties
          if (indexOfList !== -1) {
            cDefProp.companyDefinedProperties.splice(indexOfList, 1);
          }
        });


        //setup writeRights for suborders owner
        let comDef: CompanyDefinedOutsource = coDef.filter((a) => a.companyId == subOrder.companyId.id)[0];


        console.log("comDef", comDef);
        comDef.companyDefinedProperties.forEach((cDefProp: CompanyDefinedOutsourceProperty) => {

          cDefProp.writeProperties = cDefProp.readProperties;
        });
        this.globals.dataLoadingText = "Sub-Auftrag wird aktualisiert";
        return comDef;
      }),

      mergeMap((comDef: any) => this.entityRightService.acceptSubOrderAndRevokeOthers(this.order.id, subOrder.id, comDef)),
      catchError((err, caught) => {
        this.snackbarService.openSnackBar('Es ist ein Fehler aufgetreten.', 'Ok', 'red-snackbar');
        this.globals.dataLoading = false;
        return throwError(err);
      })
    ).subscribe((response: any) => {

      this.checkSubOrdersStatus();

      this.globals.dataLoading = false;
      this.snackbarService.openSnackBar('Sub-Auftrag zugewiesen.', 'Ok', 'green-snackbar');

    })

  }

  acceptParentSubOrder(subOrder: any) {


    this.globals.dataLoadingText = "Rechte werden aktualisiert";
    this.globals.dataLoading = true;
    this.loading = true;

    let newOrder: Order = new Order();
    newOrder.id = subOrder.id;

    let newStatus: OrderStatus = new OrderStatus();
    newStatus.orderStatus = "OPEN"
    newOrder.patchOrderStatus = newStatus;

    let comDefList: CompanyDefinedOutsource[] = [];
    let companyDefined: CompanyDefinedOutsource[]
    //  console.log(comDefList);
    of(0).pipe(
      mergeMap(() => this.entityRightService.getSubOrderEntityRights(subOrder)),
      map((coDef: CompanyDefinedOutsource[]) => {
        companyDefined = coDef;
        //setup writeRights for suborders owner
        let comDef: CompanyDefinedOutsource = companyDefined.filter((a) => a.companyId == subOrder.companyId.id)[0];
        comDef.companyDefinedProperties.forEach((cDefProp: CompanyDefinedOutsourceProperty) => {
          cDefProp.writeProperties = cDefProp.readProperties;
        });
        comDefList.push(comDef);
        this.globals.dataLoadingText = "Sub-Auftrag wird aktualisiert";
      }),
      mergeMap(() => this.orderService.postOrderStatus(newOrder)),
      map(() => {
        this.snackbarService.openSnackBar('Sub-Auftrag vergeben', 'Ok', 'green-snackbar');
      }),
      mergeMap(() => this.entityRightService.sentUpdateCompaniesEntityRights(comDefList)),
      catchError((err, caught) => {
        this.snackbarService.openSnackBar('Es ist ein Fehler aufgetreten.', 'Ok', 'red-snackbar');
        this.globals.dataLoading = false;
        return throwError(err);
      })
    ).subscribe((response: any) => {
      this.globals.dataLoading = false;
      this.revokeRequests(subOrder.id);
      this.snackbarService.openSnackBar('Sub-Auftrag Rechte wurden aktualisiert.', 'Ok', 'green-snackbar');
    })
  }

  revokeRequestsClick() {
    const dialogRef = this.openDeleteDialog("", 'Alle Sub-Aufträge widerrufen und Rechte löschen?', 'Alle widerrufen');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted') {
        this.revokeRequests();
      }
    });
  }

  revokeRequests(exceptSubOrderId: any = "") {

    this.loading = true;
    let subOrdersTmp: any = this.order.subOrders;
    this.globals.dataLoadingText = "Rechte werden entzogen";
    if (exceptSubOrderId != "") {
      this.globals.dataLoadingText = "Rechte anderer Sub-Aufträge werden entzogen";
      subOrdersTmp = this.order.subOrders.filter((o: any) => o.id != exceptSubOrderId);
    }

    let subOrders: any = subOrdersTmp.filter((o: any) => o.orderStatus == "REQUESTED" || o.orderStatus == "ACCEPTED");


    if (subOrders.length > 0) {
      this.globals.dataLoading = true;
      this.subOrdersRevoke(subOrders).subscribe(() => {
        this.globals.dataLoading = false;
        this.sortByOrderStatus();
        this.setOutsurceInfo();
        this.checkSubOrdersStatus();
        this.refreshOrder.emit(this.getOutsourceInfo());

        setTimeout(() => {
          this.loading = false;
        }, 700);


        this.router.navigate(['../' + this.order.id], {relativeTo: this.route});
      })
    } else {
      setTimeout(() => {
        this.loading = false;
      }, 700);
      this.globals.dataLoading = false;
      this.router.navigate(['../' + this.order.id], {relativeTo: this.route});
    }
  }

  subOrdersRevoke = (orders: any) => {
    let observableBatch: any = [];
    orders.forEach((order: Order) => {
      if (order.orderStatus == "REQUESTED" || order.orderStatus == "ACCEPTED") {
        observableBatch.push(this.revokeSubOrder(order));
      }
    })
    return forkJoin(observableBatch);
  }

  revokeRequestClick(item: any) {
    const dialogRef = this.openDeleteDialog(item.id, 'Den Sub-Auftrag widerrufen und Rechte löschen?');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted') {
        this.revokeRequest(item);
      }
    });
  }

  revokeRequest(order: any) {
    this.globals.dataLoading = true;
    this.loading = true;
    this.revokeSubOrder(order).subscribe(() => {
      this.globals.dataLoading = false;
      //this.sortByOrderStatus();
      this.setOutsurceInfo();
      this.checkSubOrdersStatus();
      this.refreshOrder.emit(this.getOutsourceInfo());
      this.snackbarService.openSnackBar('Sub-Auftrag Anfrage widerrufen', 'Ok', 'green-snackbar');

      setTimeout(() => {
        this.loading = false;
      }, 700);
    });
  }

  revokeSubOrder(order: any): Observable<any> {
    this.unsubscribeSubOrderStatus();

    let cUpdate: any = this.companyDefined.filter((coDef: CompanyDefinedOutsource) => coDef.companyId == order.companyId.id);
    this.loading = true;

    return of(0).pipe(
      map(() => {
        this.globals.dataLoadingText = "Sub-Auftrag widerrufen";
      }),
      mergeMap(() => this.orderService.revokeSubOrder(order.id)),
      map(() => {
        let sO: any = this.order.subOrders.filter((o: Order) => o.id = order.id)[0];
        sO.orderStatus = "REVOKED";
        sO.orderStatusDescription = this.orderService.getOrderStatusDescription("REVOKED");
        return order;
      }),
      catchError((err, caught) => {
        this.snackbarService.openSnackBar('Es ist ein Fehler aufgetreten.', 'Ok', 'red-snackbar');
        this.globals.dataLoading = false;
        this.loading = false;
        return throwError(err);
      })
    )
  }

  resetOutsourceClick() {
    const dialogRef = this.openConfirmDialog('Alle Sub-Aufträge mit Status "Widerrufen" erneut anfragen und Auftragsdaten teilen?', 'Erneut Anfragen?');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'action') {
        this.resetOutsource();
      }
    });
  }

  resetOutsource() {


    this.globals.dataLoading = true;


    this.outsourceService.getDefaultRightsForResetOutsource().pipe(
      map((response: EntryEntityDto[]) => {
        this.globals.dataLoadingText = "Sub-Aufträge erneut anfragen";
        return response;
      }),
      mergeMap((response: EntryEntityDto[]) => this.orderService.resetOutsource(this.order.id, response)),
      catchError((err, caught) => {
        this.snackbarService.openSnackBar('Es ist ein Fehler aufgetreten.', 'Ok', 'red-snackbar');
        this.globals.dataLoading = false;
        this.loading = false;
        return throwError(err);
      })
    ).subscribe(() => {

      this.globals.dataLoading = false;
      this.sortByOrderStatus();
      this.setOutsurceInfo();
      this.checkSubOrdersStatus();
      this.refreshOrder.emit(this.getOutsourceInfo());
      this.snackbarService.openSnackBar('Sub-Aufträge erneut angefragt', 'Ok', 'green-snackbar');

      setTimeout(() => {
        this.loading = false;
      }, 700);

      this.router.navigate(['../' + this.order.id], {relativeTo: this.route});

    });
  }

  getOrderDescription(s: string): string {
    return this.orderService.getOrderStatusDescription(s);
  }

  openConfirmDialog(text: string, title: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      text: text,
      title: title,
      btnActionText: 'Bestätigen',
      btnActionClass: 'primary',
    };
    return this.dialog.open(DialogTemplateComponent,
      dialogConfig);
  }

  openDeleteDialog(id: string, text: string, title: string = "Endgültig löschen"): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: title,
      text: text,
    };
    return this.dialog.open(DialogDeleteComponent,
      dialogConfig);
  }

  openEntityRightsDialog(item: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80vw";
    dialogConfig.height = "80vh";

    dialogConfig.data = {
      order: item,
      editable: this.order.orderRightsGlobal!.edit.rights
    };

    const dialogRef = this.dialog.open(DialogSubordersComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result != 'canceled') {
        this.updateCompanyDefined(result);
      }
    });
  }

  updateCompanyDefined(cD: any) {
    //  console.log(this.companyDefined);
    //console.log(cD);
    if (this.companyDefined == []) {
      this.companyDefined = cD;
    } else {
      let c: any = cD[0];

      if (c != undefined) {
        let cUpdateIndex: number = this.companyDefined.findIndex((coDef: CompanyDefinedOutsource) => coDef.companyId == c.companyId);

        if (cUpdateIndex === -1) {
          this.companyDefined.push(c);
        } else {
          // Delete cUpdate from the list
          this.companyDefined.splice(cUpdateIndex, 1);

          // Add c to the list
          this.companyDefined.push(c);
        }

      }
    }
  }


}
