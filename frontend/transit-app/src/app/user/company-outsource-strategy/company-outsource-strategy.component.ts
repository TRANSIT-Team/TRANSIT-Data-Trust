import {Component, OnInit} from '@angular/core';
import {EntityRightService} from "../../entity-rights/shared/entity-right.service";
import {CompanyService} from "../../company/shared/company.service";
import {mergeMap, of, map} from "rxjs";
import {Company} from "../../company/shared/company";
import DevExpress from "devextreme";
import {
  EntityRightGlobal,
  EntityRightsGlobal,
  EntityRightsProperty, EntityRightsPropertyCompanyDefault,
  PreDefinedOutsource
} from "../../entity-rights/shared/entity-right";
import {SnackbarService} from "../../_core/snackbar.service";
import {MatSlideToggleChange} from "@angular/material/slide-toggle";

@Component({
  selector: 'app-company-outsource-strategy',
  templateUrl: './company-outsource-strategy.component.html',
  styleUrls: ['./company-outsource-strategy.component.css']
})
export class CompanyOutsourceStrategyComponent implements OnInit {
  preDefinedOutsourceExpanded: boolean = true;
  preDefinedOutsource: PreDefinedOutsource[] = [];
  preDefinedPackageOutsource: PreDefinedOutsource[] = [];
  preDefinedPackagePropertiesOutsource: PreDefinedOutsource[] = [];
  entityRightsGlobal: EntityRightGlobal[] = [];

  constructor(private entityRightService: EntityRightService, private companyService: CompanyService, private snackbarService: SnackbarService) {
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      mergeMap((c: Company) => this.companyService.getCompanyDefaultSharingRights(c.id)),
      map((response: any) => {

        let eR: any = this.entityRightService.getGlobalEntityRights();
        this.entityRightsGlobal = eR.entityRights.filter((item: any) => item.entity !== 'orderProperty');
        this.entityRightsGlobal.forEach((entityRight: EntityRightGlobal) => {
          entityRight.properties!.forEach((entityRightProperty: EntityRightsProperty) => {
            response.defaultSharingRights.forEach((item: any) => {
              let tmp: any = item.companyProperties.filter((entityRightCompany: any) => entityRightCompany.property == entityRightProperty.property)[0];
              if (tmp != undefined) {
                entityRightProperty.default = tmp.default;
              }
            })
          });
        });


      })
    ).subscribe();
  }


  saveData() {

    let postRights: any = [];

    this.entityRightsGlobal.forEach((entityRight: EntityRightGlobal) => {

      let postRightsProperties: any = [];
      entityRight.properties!.forEach((entityRightProperty: EntityRightsProperty) => {


        let defaultValue: boolean = entityRightProperty.default;

        if (entityRightProperty.alwaysShared) {
          defaultValue = true;
        }
        if (entityRightProperty.neverShared) {
          defaultValue = false;
        }

        postRightsProperties.push(
          {
            property: entityRightProperty.property,
            default: defaultValue
          }
        )
      });

      postRights.push({
        entity: entityRight.entity,
        companyProperties: postRightsProperties
      })
    });

    of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      mergeMap((c: Company) => this.companyService.postCompanyDefaultSharingRights(c.id, postRights))
    ).subscribe((result: any) => {
      this.snackbarService.openSnackBar('Aktualisiert', 'Ok', 'green-snackbar');
    });
  }

  findMatchingPackageProperties(prop: any[], id: any): any[] {
    return prop.filter(p => p.parentId == id);
  }

  isObject(v: any): boolean {
    return (typeof v !== 'object');
  }


  selectAll(event: MatSlideToggleChange) {
    this.entityRightsGlobal.forEach((entityRight: EntityRightGlobal) => {
      entityRight.properties!.forEach((entityRightProperty: EntityRightsProperty) => {
        entityRightProperty.default = event.checked;
      });
    });
  }

  expandCollapsAll(e: any) {

    this.entityRightsGlobal.forEach((entityRight: EntityRightGlobal) => {

      entityRight.valuesExpanded = e;

    });


  }
}
