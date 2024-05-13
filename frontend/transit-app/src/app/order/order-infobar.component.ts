import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Order, OrderProperty, OrderStatus} from "./shared/order";
import {OrderService} from "./shared/order.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogInputComponent} from "../dialog/dialog-input.component";
import {iif, map, mergeMap, Observable, of, Subscription, timer} from "rxjs";
import {SnackbarService} from "../_core/snackbar.service";
import {CompanyService} from "../company/shared/company.service";
import {ContactPerson} from "../contact-person/shared/contact-person";
import {ActivatedRoute, Router} from "@angular/router";
import {Globals} from "../_core/globals";
import {OutsourceService} from "../outsource/shared/outsource-component.service";
import {FunctionsService} from "../_core/functions.service";

@Component({
  selector: 'app-order-infobar',
  templateUrl: './order-infobar.component.html',
  styleUrls: ['./order-infobar.component.css']
})
export class OrderInfobarComponent implements OnInit {
  @Input() order: Order;
  @Input() orderStatusTmp: string = "";
  @Output() refreshOrder = new EventEmitter<any>();
  orderStatusDescription: string;
  selectedContactPerson = '';
  contactPersons: ContactPerson[] = [];
  parentOrderCompanyId: string = '';
  orderSubscripton: Subscription;
  loading: boolean = false;
  completeDisabledTooltip = 'Der Auftrag kann nur über das Eintragen der Lieferung abgeschlossen werden.';
  showFinish: boolean = false;

  constructor(public orderService: OrderService,
              private functionsService: FunctionsService,
              public dialog: MatDialog,
              public snackbarService: SnackbarService,
              private companyService: CompanyService,
              private router: Router, private globals: Globals,
              private route: ActivatedRoute,
              private outsourceService: OutsourceService) {
  }

  ngOnInit(): void {
    if (this.order.orderStatus != undefined) {
      this.order.orderStatusSelectionFlow = this.orderService.getOrderStatusFlow(this.order.orderStatus);
    }
    this.mapParentOrderInformations();
    this.getInformations();
  }

  ngOnDestroy() {
    if (this.orderSubscripton !== undefined) {
      this.orderSubscripton.unsubscribe();
    }
  }

  checkOrdersStatus() {
    let oldStat = this.order.orderStatus;
    this.orderSubscripton = timer(0, 10000).pipe(
      mergeMap(() => this.getOrderStatus()),
      map((tmpResponse: any) => {
        if (tmpResponse.orderStatus == "REJECTED" || tmpResponse.orderStatus == "REVOKED") {
          this.router.navigate(['../' + this.order.id], {relativeTo: this.route});
        }
        this.refreshOrder.emit();
      })
    ).subscribe();
  }

  getOrderStatus(): Observable<any> {
    return this.orderService.getOrderStatus(this.order.id).pipe(
      map((tmpResponse: any) => {
        if (tmpResponse != undefined) {
          if (this.order.orderStatus != tmpResponse.orderStatus) {
            this.order.orderStatus = tmpResponse.orderStatus;
            if (this.order.orderStatus != undefined) {
              this.orderStatusTmp = this.order.orderStatus.toString();
              this.order.orderStatusDescription = this.orderService.getOrderStatusDescription(this.order.orderStatus);
              this.order.orderStatusSelectionFlow = this.orderService.getOrderStatusFlow(this.order.orderStatus);
            }
            this.snackbarService.openSnackBar('Der Auftragsstatus hat sich geändert.', 'Ok', 'green-snackbar');
          }
        }

        return tmpResponse;
      }))
  }

  getInformations() {
    this.companyService.getCompanyContactPersons().pipe(
      map((collection: any) => {
        //console.log(collection);
        this.contactPersons = collection.resources;
        if (this.order.contactPersonId != undefined) {
          this.selectedContactPerson = this.order.contactPersonId.id;
        }
      })).subscribe(() => {

      this.checkOrdersStatus();
    });
  }

  openInputDialog(id: string, text: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: 'Auftragsstatus ändern',
      text: text,
      btnActionClass: '',
      btnActionText: 'Status speichern'
    };
    return this.dialog.open(DialogInputComponent,
      dialogConfig);
  }

  saveInformations() {

    if (this.order.orderStatus != this.orderStatusTmp) {
      this.updateOrderStatus(this.orderStatusTmp);
    } else {
      if (this.selectedContactPerson != "") {
        this.updateContactPerson().subscribe(() => {
          //this.globals.dataLoading=false;
          this.loading = false;
          this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');
        });
      } else {
        this.snackbarService.openSnackBar('Keine Änderung vorgenommen.', 'Ok', 'green-snackbar');
      }
    }
  }

  updateContactPerson(): Observable<any> {

    if (this.selectedContactPerson == '' || this.selectedContactPerson == "") {
      return of(0);
    }
    if (this.order.contactPersonId != undefined) {
      if (this.order.contactPersonId.id == this.selectedContactPerson) {
        return of(0);
      }
    }

    let tmpOrder: any;
    tmpOrder = {
      id: this.order.id,
      contactPersonId: {id: this.selectedContactPerson},
    };
    this.loading = true;

    return this.orderService.patchOrder(tmpOrder).pipe(
      mergeMap(() => iif(() => (this.order.isMyOrder! && this.order.suborderType! && this.parentOrderCompanyId != ''), this.outsourceService.provideParentReadRightsForMyContactPerson(this.selectedContactPerson, this.parentOrderCompanyId), of(null))),
      map((order: any) => {
        if (order != undefined) {
          this.order.contactPersonId = order.contactPersonId;
        }
      })
    );
  }

  updateOrderStatus(newStatus: string) {
    if (this.order.orderStatus != newStatus) {
      if (this.checkOrderNewStatusNeedsComment(newStatus)) {

        if (newStatus == "CANCELED") {
          const dialogRef = this.openInputDialog(this.order.id, 'Den Hauptauftrag endgültig abbrechen? Der Auftrag kann danach nicht mehr geändert werden.');
          dialogRef.afterClosed().subscribe((result: any) => {
            if (result != 'canceled') {
              this.order.reasonForCancel = result;
              this.sentOrderStatus(newStatus);
            }
          });
        }
      } else {
        this.sentOrderStatus(newStatus);
      }
    }
  }


  sentOrderStatus(newStatus: string) {
    let order: any = {id: this.order.id, orderStatus: newStatus, patchOrderStatus: newStatus};
    let subOrderisComplete: boolean = false;
    if (this.order.orderStatus != newStatus) {
      this.globals.dataLoadingText = "Daten senden";
      this.globals.dataLoading = false;
      this.loading = true;
      of(0).pipe(
        mergeMap(() => iif(() => (this.checkOrderNewStatusNeedsComment(newStatus)), this.orderService.patchOrderReasonForCancel(this.order), of(0))),
        mergeMap(() => this.orderService.postOrderStatusById(this.order.id, newStatus)),
        map(() => {
          let descriptionStatus: string = this.orderService.getOrderStatusDescription(newStatus);
          this.snackbarService.openSnackBar('Orderstatus geändert auf: ' + descriptionStatus, 'Ok', '');
          this.order.orderStatus = newStatus;
          this.order.orderStatusDescription = this.orderService.getOrderStatusDescription(newStatus);
          this.order.orderStatusSelectionFlow = this.orderService.getOrderStatusFlow(newStatus);
          this.orderStatusDescription = descriptionStatus;
        }),
        mergeMap(() => this.updateContactPerson())
      ).subscribe((response: any) => {
        this.globals.dataLoading = false;

        this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');
        if (newStatus == "REJECTED") {
          this.router.navigate(['/orders'], {relativeTo: this.route});
        } else {
          this.refreshOrder.emit();
        }

        setTimeout(() => {
          this.loading = false;
        }, 700);


        if (newStatus == "COMPLETE") {
          this.showFinish = true;
          this.snackbarService.openSnackBar('Der Auftrag wurde erledigt.', 'Ok', 'green-snackbar');
          setTimeout(() => {
            this.showFinish = false;
          }, 2500);
        }

      })
    } else {
      this.snackbarService.openSnackBar('Orderstatus nicht geändert', 'Ok', '');
    }
  }


  checkOrderNewStatusNeedsComment(stat: string): boolean {
    if (stat == "CANCELED") {
      return true;
    }
    return false;
  }

  mapParentOrderInformations() {
    if (this.order.orderProperties != undefined && this.order.suborderType) {
      let s: any = this.order.orderProperties.filter((a: OrderProperty) => a.key == "ParentOrderCompanyId")[0];
      if (s != undefined && s.value != "") {
        this.parentOrderCompanyId = s.value;
      }
    }
  }

  getCompleteDisabledTooltip(orderStatus: string): any {
    if (orderStatus == 'COMPLETE' && (this.order.orderStatus != 'COMPLETE' && this.order.suborderType && !this.order.isOutsourced)) {
      return this.completeDisabledTooltip;
    }
    return null;
  }
}
