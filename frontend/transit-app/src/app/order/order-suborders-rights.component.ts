import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {catchError, iif, map, mergeMap, of, throwError} from "rxjs";
import {
  CompanyDefinedOutsource,
  CompanyDefinedOutsourceProperty, CompanyDefinedOutsourceSubProperty,
  PropertySelection
} from "../entity-rights/shared/entity-right";
import {OutsourceCompany} from "../outsource/shared/outsource";
import {EntityRightService} from "../entity-rights/shared/entity-right.service";
import {SnackbarService} from "../_core/snackbar.service";
import {OrderService} from "./shared/order.service";
import {MatDialog} from "@angular/material/dialog";
import {Order, OrderStatus} from "./shared/order";

@Component({
  selector: 'app-order-suborders-rights',
  templateUrl: './order-suborders-rights.component.html',
  styleUrls: ['./order-suborders-rights.component.css']
})
export class OrderSubordersRightsComponent implements OnInit {
  @Input() order: Order;
  @Input() dialogComponent: boolean = false;
  @Input() editable: boolean = false;
  @Output() externFunction = new EventEmitter<any>();
  @Output() updateExternCompanyDefined = new EventEmitter<any>();

  companyDefined: CompanyDefinedOutsource[] = [];
  loading: boolean = true;
  loadingText: string = "Rechte werden abgerufen";

  constructor(private entityRightService: EntityRightService, public snackbarService: SnackbarService, private orderService: OrderService,) {
  }

  ngOnInit(): void {
    this.getSubOrderRights();
  }


  getSubOrderRights() {
    of(0).pipe(
      map(() => {
        let company: OutsourceCompany[] = [];
        company.push({company: this.order.companyId!, sort: 1});
        let eR: any = this.entityRightService.getPreDefinedPropertiesForOrder(this.order, false);
        console.log(eR);

        this.companyDefined.push(...this.entityRightService.getCompanyDefinedProperties(company, eR, this.order));
      }),
      mergeMap(() => iif(() => this.companyDefined.length > 0, this.entityRightService.getCompanyDefinedEntityRights(this.companyDefined), of(0))),
      map((entityRights: any) => {
        if (entityRights != 0) {
          this.companyDefined = this.entityRightService.matchCompanyDefinedEntityRights(this.companyDefined, entityRights);
        }
      }),
      catchError((err, caught) => {
        this.snackbarService.openSnackBar('Es ist ein Fehler aufgetreten.', 'Ok', 'red-snackbar');
        this.loading = false;
        return throwError(err);
      })
    ).subscribe(() => {

      if (this.dialogComponent) {
        this.updateExternCompanyDefined.emit(this.companyDefined);
      }
      this.loading = false;



    });
  }

  changeCompanyDefinedOutsourceSelection(checked: boolean, parentId: any) {

    let companyDefined: CompanyDefinedOutsource = this.companyDefined.filter((c) => c.parentId == parentId)[0];

    companyDefined.companyDefinedProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceProperty) => {
      companyDefinedOutsourceProperty.readProperties.forEach((propertySelection: PropertySelection) => {
        propertySelection.selected = checked;
      });
    });

    companyDefined.companyDefinedPackageProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceSubProperty) => {
      companyDefinedOutsourceProperty.readProperties.forEach((propertySelection: PropertySelection) => {
        propertySelection.selected = checked;
      });

      companyDefinedOutsourceProperty.subDefinedOutsourceProperties.forEach((companyDefinedSubOutsourceProperty: CompanyDefinedOutsourceProperty) => {
        companyDefinedSubOutsourceProperty.readProperties.forEach((propertySelection: PropertySelection) => {
          propertySelection.selected = checked;
        });
      });
    });
  }

  updateEntityRights() {
    this.loading = true;
    this.loadingText = "Rechte werden gespeichert";

    this.entityRightService.sentUpdateCompaniesEntityRights(this.companyDefined).pipe(
      catchError((err, caught) => {
        this.snackbarService.openSnackBar('Es ist ein Fehler aufgetreten.', 'Ok', 'red-snackbar');
        this.loading = false;
        return throwError(err);
      })
    ).subscribe(() => {
      this.snackbarService.openSnackBar('Sub-Aufträge Rechte wurden aktualisiert.', 'Ok', 'green-snackbar');
      if (this.dialogComponent) {
        this.externFunction.emit('updated');
        this.updateExternCompanyDefined.emit(this.companyDefined);
      }
      this.loading = false;
    });
  }

  changeValuesSection(event: any, item: any, cDP: any, packageProp: boolean = false) {
    let i: CompanyDefinedOutsource = this.companyDefined.filter((i: any) => i == item)[0];

    if (packageProp) {
      i.companyDefinedPackageProperties.filter((cp: any) => cp == cDP).forEach((c: CompanyDefinedOutsourceSubProperty) => {
        c.readProperties.forEach((r: any) => {
          r.selected = event;
        })
      });
    } else {
      i.companyDefinedProperties.filter((cp: any) => cp == cDP).forEach((c: CompanyDefinedOutsourceProperty) => {
        c.readProperties.forEach((r: any) => {
          r.selected = event;
        })
      });
    }
  }

  acceptParentSubOrder(orderId: any, companyId: any) {

    let newOrder: Order = new Order();
    newOrder.id = orderId;
    let newStatus: OrderStatus = new OrderStatus();
    newStatus.orderStatus = "OPEN"
    newOrder.patchOrderStatus = newStatus;

    //setup writeRights for suborders owner
    let comDefList: CompanyDefinedOutsource[] = [];
    let comDef: CompanyDefinedOutsource = this.companyDefined.filter((a) => a.companyId == companyId)[0];
    comDef.companyDefinedProperties.forEach((cDefProp: CompanyDefinedOutsourceProperty) => {
      cDefProp.writeProperties = cDefProp.readProperties;
      if (cDefProp.typeClazz == "order") {

      }
    });

    comDefList.push(comDef);
    //  console.log(comDefList);
    of(0).pipe(
      mergeMap(() => this.orderService.postOrderStatus(newOrder)),
      map(() => {
        this.snackbarService.openSnackBar('Sub-Auftrag vergeben.', 'Ok', '');
      }),
      //  mergeMap(() => this.getSubOrdersStatus()),
      //  mergeMap(() => this.getOrderStatus()),
      mergeMap(() => this.entityRightService.sentUpdateCompaniesEntityRights(comDefList)),
    ).subscribe((response: any) => {
      this.snackbarService.openSnackBar('Sub-Auftrag Rechte wurden aktualisiert.', 'Ok', 'green-snackbar');
    })
  }


  expandCompanyDefinedOutsource(parentId: any, e: any) {

    let coDef: CompanyDefinedOutsource = this.companyDefined.filter((c) => c.parentId == parentId)[0];
    //this.changeValuesExpanded(!e, coDef, 1);
    this.changeValuesExpanded(e, coDef, 50);

  }

  changeValuesExpanded(e: any, coDef: CompanyDefinedOutsource, timer: any) {

    coDef.companyDefinedPackageProperties.forEach((c) => {
      c.valuesExpanded = e;
    })


    setTimeout(() => {
      coDef.companyDefinedProperties.forEach((c) => {
        c.valuesExpanded = e;
      })
      coDef.valuesExpanded = e;

    }, timer * 3);

  }

  isObject(v: any): boolean {
    return (typeof v !== 'object');
  }

}
