import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
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
import {MatDialog} from "@angular/material/dialog";
import {Order} from "../order/shared/order";
import {OutsourceCompany} from "./shared/outsource";
import {Company} from "../company/shared/company";
import {iif, map, mergeMap, of} from "rxjs";


@Component({
  selector: 'app-outsource',
  templateUrl: './outsource.component.html',
  styleUrls: ['./outsource.component.css']
})
export class OutsourceComponent implements OnInit {
  @Input() order: Order;
  @Input() dialogComponent: boolean = false;
  @Output() closeWindow = new EventEmitter<any>();
  @Input() outsourceMapCompanies: Company[];
  @Output() storeOutsourceMapCompanies = new EventEmitter<any>();
  showRights: boolean = false;
  sendDirectly: boolean = false;
  outsourceCompanies: OutsourceCompany[] = [];
  outsourceStategieCompanies: OutsourceCompany[] = [];
  companiesSelected: Company[] = [];

  constructor(public dialog: MatDialog, private companyService: CompanyService,) {
  }


  ngOnInit(): void {
    this.loadDefaultRights();
  }

  loadDefaultRights() {

    let companyId: any = 0;
    of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      map((c: any) => {
        companyId = c.id;
        return c;
      }),
      mergeMap((c: Company) => this.companyService.getCompanyDefaultSharingRights(c.id)),
      mergeMap((c: any) => iif(() =>  c.defaultSharingRights == undefined, this.companyService.postDefaultCompanyDefaultSharingRights(companyId), of(c)))
    ).subscribe();
  }

  goToMap(clearList: boolean) {
    if (clearList) {
      this.outsourceCompanies = [];
      this.outsourceStategieCompanies = [];
      this.companiesSelected = [];
    }
    this.showRights = false;
  }


  goToRights(r: any) {
    this.outsourceCompanies = r;
    this.showRights = true;
  }

  storeData(data: any) {
    this.storeOutsourceMapCompanies.emit(data);
  }

  setDirectly(r: any) {
    this.sendDirectly = r;
  }
}
