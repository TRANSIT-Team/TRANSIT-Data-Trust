import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatAccordion} from "@angular/material/expansion";
import {OutsourceCompany} from "./shared/outsource";
import {
  CompanyDefinedOutsource,
  CompanyDefinedOutsourceProperty,
  CompanyDefinedOutsourceSubProperty,
  EntityRight, EntityRightEntry, EntityRightProperties,
  EntityRightsGlobal,
  PreDefinedOutsource,
  PreDefinedOutsourceList,
  PreDefinedProperty,
  PropertySelection
} from "../entity-rights/shared/entity-right";
import {Order} from "../order/shared/order";
import {HttpClient} from "@angular/common/http";
import {MapService} from "../map/shared/map.service";
import {OrderService} from "../order/shared/order.service";
import {OutsourceService} from "./shared/outsource-component.service";
import {ActivatedRoute, Router} from "@angular/router";
import {DirectionService} from "../map/shared/direction.service";
import {Location} from "@angular/common";
import {CompanyService} from "../company/shared/company.service";
import {FunctionsService} from "../_core/functions.service";
import {SnackbarService} from "../_core/snackbar.service";
import {HateoasResourceService} from "@lagoshny/ngx-hateoas-client";
import {EntityRightService} from "../entity-rights/shared/entity-right.service";
import {Globals} from "../_core/globals";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {MatSlideToggleChange} from "@angular/material/slide-toggle";
import {catchError, map, mergeMap, of, throwError} from "rxjs";
import {DialogDeleteComponent} from "../dialog/dialog-delete.component";
import {Company} from "../company/shared/company";

@Component({
  selector: 'app-outsource-rights',
  templateUrl: './outsource-rights.component.html',
  styleUrls: ['./outsource-rights.component.css']
})
export class OutsourceRightsComponent implements OnInit {
  @ViewChild(MatAccordion) accordion: MatAccordion;
  @Input() order: Order;
  @Input() sendDirectly: boolean;
  @Input() outsourceCompanies: OutsourceCompany[] = [];
  @Output() forwardToMap = new EventEmitter<any>();
  @Output() closeWindow = new EventEmitter<any>();
  hideLoader: any = false;

  companyDefined: CompanyDefinedOutsource[] = [];


  preDefinedOutsource: PreDefinedOutsource[] = [];
  preDefinedPackageOutsource: PreDefinedOutsource[] = [];
  preDefinedPackagePropertiesOutsource: PreDefinedOutsource[] = [];
  preDefinedOutsourceList: PreDefinedOutsourceList[]
  preDefinedOutsourceExpanded: boolean = false;
  entityRightsGlobal: EntityRightsGlobal;

  error: boolean = false;
  alertMessage: any = "";

  sendingData: boolean = false;


  constructor(private http: HttpClient,
              private mapService: MapService,
              private orderService: OrderService,
              private outsourceService: OutsourceService,
              private route: ActivatedRoute,
              private directionService: DirectionService,
              private location: Location,
              private companyService: CompanyService,
              private functionsService: FunctionsService,
              private router: Router,
              private snackbarService: SnackbarService,
              private hateoasResourceService: HateoasResourceService,
              private entityRightService: EntityRightService,
              private globals: Globals,
              public dialog: MatDialog,
  ) {
  }


  ngOnInit(): void {
    // this.getOutsourceCompanies();
    this.getOrder();
  }

  getOutsourceCompanies() {
    this.outsourceService.currentOutsourceStategie
      .subscribe((outsourceCompanies: any) => {
        this.outsourceCompanies = outsourceCompanies;
        this.checkAndReroute();

        this.getOrder();
      });
  }

  findMatchingPackageProperties(prop: any[], id: any): any[] {
    return prop.filter(p => p.parentId == id);
  }

  routeBack(clearList: boolean = false) {
    //this.functionsService.routeBack();

    //this.router.navigate(['../routemap'], {relativeTo: this.route});
    this.forwardToMap.emit(clearList);
  }

  checkAndReroute() {
    //
    //  console.log("");
    let reroute: boolean = false;
    if (this.outsourceCompanies != undefined) {
      if (this.outsourceCompanies.length == undefined) {
        reroute = true;
      } else if (this.outsourceCompanies.length == 0) {
        reroute = true;
      }
    } else {
      reroute = true;
    }

    if (reroute) {
      this.routeBack(true);
    }
  }

  expandPreDefinedOutsource(e: any) {
    this.preDefinedOutsourceExpanded = e;
    this.preDefinedOutsource.forEach((c) => {
      c.valuesExpanded = e;
    })
  }

  expandCompanyDefinedOutsource(companyId: any, e: any) {
    let coDef: CompanyDefinedOutsource = this.companyDefined.filter((c) => c.companyId == companyId)[0];
    //force changes for triggering webview

    coDef.companyDefinedPackageProperties.forEach((c: any) => {
      c.valuesExpanded = e;
    })

    setTimeout(() => {
      coDef.companyDefinedProperties.forEach((c: any) => {
        c.valuesExpanded = e;
      })
      coDef.valuesExpanded = e;
    }, 50);
  }

  changePreDefinedOutsourceSelection(event: MatSlideToggleChange) {

    //   console.log(this.preDefinedOutsource);
    this.preDefinedOutsource.forEach((preDefinedOutsource: PreDefinedOutsource) => {
      preDefinedOutsource.readProperties.forEach((preDefinedProperty: PreDefinedProperty) => {
        preDefinedProperty.selected = event.checked;
      });
    });

    this.preDefinedPackageOutsource.forEach((preDefinedOutsource: PreDefinedOutsource) => {
      preDefinedOutsource.readProperties.forEach((preDefinedProperty: PreDefinedProperty) => {
        preDefinedProperty.selected = event.checked;
      });
    });

    this.preDefinedPackagePropertiesOutsource.forEach((preDefinedOutsource: PreDefinedOutsource) => {
      preDefinedOutsource.readProperties.forEach((preDefinedProperty: PreDefinedProperty) => {
        preDefinedProperty.selected = event.checked;
      });
    });

    //   console.log(this.preDefinedOutsource);
  }

  changeCompanyDefinedOutsourceSelection(event: MatSlideToggleChange, companyId: any) {
    let companyDefined: CompanyDefinedOutsource = this.companyDefined.filter((c) => c.companyId == companyId)[0];


    companyDefined.companyDefinedProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceProperty) => {
      companyDefinedOutsourceProperty.readProperties.forEach((propertySelection: PropertySelection) => {
        propertySelection.selected = event.checked;
      });
    });

    companyDefined.companyDefinedPackageProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceSubProperty) => {
      companyDefinedOutsourceProperty.readProperties.forEach((propertySelection: PropertySelection) => {
        propertySelection.selected = event.checked;
      });

      companyDefinedOutsourceProperty.subDefinedOutsourceProperties.forEach((companyDefinedSubOutsourceProperty: CompanyDefinedOutsourceProperty) => {
        companyDefinedSubOutsourceProperty.readProperties.forEach((propertySelection: PropertySelection) => {
          propertySelection.selected = event.checked;
        });
      });

    });
  }


  getOrder() {
    of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      map((c: Company) => {
        this.order.companyId = c;
        return c;
      }),
      mergeMap((c: Company) => this.companyService.getCompanyDefaultSharingRights(c.id)),
      map((response: any) => {
        this.setUpPreDefinedProperties();
        this.preDefinedOutsourceList.forEach((preDefinedOutsourceList: PreDefinedOutsourceList) => {
          preDefinedOutsourceList.preDefinedOutsources.forEach((preDefinedOutsources: PreDefinedOutsource) => {
            preDefinedOutsources.readProperties.forEach((prop: PreDefinedProperty) => {
              response.defaultSharingRights.forEach((item: any) => {
                let tmp: any = item.companyProperties.filter((entityRightCompany: any) => entityRightCompany.property == prop.name)[0];
                if (tmp != undefined) {
                  prop.selected = tmp.default;
                }
              })
            });
          });
        });
        this.setUpCompanyDefinedProperties();
      }))
      .subscribe(() => {
        this.hideLoader = true;


        if (this.sendDirectly) {
          this.sentOrder();
        }
      });
  }


  setUpPreDefinedProperties() {
    this.preDefinedOutsourceList = this.entityRightService.getPreDefinedPropertiesForOrder(this.order, true);
    this.preDefinedOutsource = this.preDefinedOutsourceList.filter((e) => e.name == "preDefinedOutsource")[0].preDefinedOutsources;
    this.preDefinedPackageOutsource = this.preDefinedOutsourceList.filter((e) => e.name == "preDefinedPackageOutsource")[0].preDefinedOutsources;
    this.preDefinedPackagePropertiesOutsource = this.preDefinedOutsourceList.filter((e) => e.name == "preDefinedPackagePropertiesOutsource")[0].preDefinedOutsources;
    // console.log(this.preDefinedPackagePropertiesOutsource);
  }

  overwriteCompanyDefinedProperties() {
    this.companyDefined = [];
    this.setUpCompanyDefinedProperties();
    this.snackbarService.openSnackBar('Einstellungen für alle Unternehmen übernommen.', 'Ok', 'green-snackbar');

  }

  setUpCompanyDefinedProperties() {
    this.companyDefined = this.entityRightService.getCompanyDefinedProperties(this.outsourceCompanies, this.preDefinedOutsourceList);

    /* this.companyDefined.forEach((comDef: CompanyDefinedOutsource) => {
       comDef.companyDefinedProperties.forEach((cDefProp: CompanyDefinedOutsourceProperty) => {
         if (cDefProp.writeProperties == undefined) {
           cDefProp.writeProperties = [];
         }
       });
     });*/

  }

  openDeleteDialog(id: string, text: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: 'Firma entfernen?',
      text: text,
    };
    return this.dialog.open(DialogDeleteComponent,
      dialogConfig);
  }

  revokeCompany(companyId: any) {


    const dialogRef = this.openDeleteDialog(companyId, 'Firma aus der Liste entfernen?');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted') {
        this.companyDefined = this.companyDefined.filter(item => item.companyId !== companyId);
        this.outsourceCompanies = this.outsourceCompanies.filter(item => item.company.id !== companyId);
        this.checkAndReroute();
      }
    });


    //this.companyDefined = this.companyDefined.filter(item => item.companyId !== companyId);
  }

  sentOrder() {

    let i: number = 0;
    this.sendingData = true;

    if (this.companyDefined != undefined) {

      this.companyDefined.forEach((companyDefinedOutsource: CompanyDefinedOutsource) => {
        i++;

        // company address is billing-address for sub-order for parentorder-company
        let oC: OutsourceCompany = this.outsourceCompanies.filter((c: OutsourceCompany) => c.company.id === companyDefinedOutsource.companyId)[0];
        if (oC.company.companyAddresses != undefined) {
          companyDefinedOutsource.companyAddresse = oC.company.companyAddresses[0];
        }

        let newSubOrder: Order = new Order();
        // create a Sub-Order for eacht company which is requested
        this.outsourceService.createSubOrder(this.order, companyDefinedOutsource).pipe(
          map((subO: Order) => {
            //console.log("Sub-Order Company: " + oC.company.name, subO);
            this.snackbarService.openSnackBar('Sub-Auftrag angelegt.', 'Ok', 'green-snackbar');
            newSubOrder = subO;
            return subO;
          }),
          mergeMap((subOrder: Order) => this.outsourceService.setUpRightsAndSend(subOrder, companyDefinedOutsource)),
          catchError((err, caught) => {

            this.snackbarService.openSnackBar('Ein Fehler ist aufgetreten', 'Ok', 'red-snackbar');
            this.sendingData = false;
            return throwError(err);
          }),
        ).subscribe(() => {

          if (i >= this.companyDefined.length) {
            this.snackbarService.openSnackBar('Sub-Auftrag Rechte wurden wie eingestellt zugewiesen', 'Ok', 'green-snackbar');
            this.routeToOrderAndScroll();
            this.sendingData = false;
          }

        });
      });
    }
  }


  changeValuesSection(event: any, item: any, cDP: any, packageProp:boolean=false) {
    let i: CompanyDefinedOutsource = this.companyDefined.filter((i: any) => i == item)[0];

    if(packageProp){
      i.companyDefinedPackageProperties.filter((cp: any) => cp == cDP).forEach((c: CompanyDefinedOutsourceSubProperty) => {
        c.readProperties.forEach((r: any) => {
          r.selected = event;
        })
      });
    }else{
      i.companyDefinedProperties.filter((cp: any) => cp == cDP).forEach((c: CompanyDefinedOutsourceProperty) => {
        c.readProperties.forEach((r: any) => {
          r.selected = event;
        })
      });
    }




  }

  routeToOrderAndScroll() {


    this.closeWindow.emit();
  }

  isObject(v: any): boolean {
    return (typeof v !== 'object');
  }

}
