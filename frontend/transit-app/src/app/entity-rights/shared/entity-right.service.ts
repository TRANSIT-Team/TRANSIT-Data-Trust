import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, forkJoin, iif, map, mergeMap, Observable, of} from 'rxjs';
import {environment} from 'src/environments/environment';
import {
  CompanyDefinedOutsource,
  CompanyDefinedOutsourceProperty,
  CompanyDefinedOutsourceSubProperty,
  EntityRight,
  EntityRightEntry,
  EntityRightGlobal, EntityRightProperties,
  EntityRightsGlobal,
  EntityRightsProperty, EntityRightsPropertyCompanyDefault,
  EntryEntityDto,
  EntryEntityRight,
  EntryEntityRightProperty,
  PreDefinedOutsource,
  PreDefinedOutsourceList,
  PreDefinedProperty,
  PropertySelection
} from './entity-right';
//@ts-ignore
import entityRightsGlobal from './entity-rights.json';
import {Package, PackageProperty} from "../../packages/shared/package";
import {Order} from "../../order/shared/order";
import {OutsourceCompany} from "../../outsource/shared/outsource";


@Injectable({
  providedIn: 'root'
})
export class EntityRightService {

  constructor(private http: HttpClient) {


  }

  getGlobalEntityRights(): any {
    let eR: EntityRightsGlobal = entityRightsGlobal;
    eR.entityRights.forEach((entityRight: EntityRightGlobal) => {
      entityRight.valuesExpanded = true;
      entityRight.properties!.forEach((entityRightProperty: EntityRightsProperty) => {
        entityRightProperty.default = true;
      });
    });
    return eR;
  }

  basicEntityRight(id: any, companyId: any): EntityRight {
    // Return an empty object if 404 is encountered
    let er: EntityRight = new EntityRight()
    let erE: EntityRightEntry = new EntityRightEntry()
    let erEP: EntityRightProperties = new EntityRightProperties()
    erE.entityId = id;
    erE.companyId = companyId;
    erEP.readProperties = [];
    erEP.writeProperties = [];
    erE.properties = erEP;
    er.entries = [];
    er.entries.push(erE);
    return er;
  }

  getEntityRightById(id: any, forCompanyid: any): Observable<EntityRight> {
    const url = environment.backend + '/entityrights/' + id.toString() + '?companyid=' + forCompanyid.toString();
    return this.http.get<EntityRight>(url).pipe(
      catchError((error: any) => {
        // Check if the error status is 404 (Not Found)
        if (error.status === 404) {
          // Return an empty object if 404 is encountered
          return of(this.basicEntityRight(id, forCompanyid));
        }
        throw error;
      })
    );
  }

  acceptSubOrderAndRevokeOthers(orderId: any, subOrderId: any, o: CompanyDefinedOutsource): Observable<any> {
    console.log(o);
    let entryEntityDto: EntryEntityDto[] = this.transformCompanyRightsForRequest(o);
    console.log(entryEntityDto);
    let entryEntityRight: EntryEntityRight[] = [];
    entryEntityDto.forEach((eR: EntryEntityDto) => {
      entryEntityRight.push(...eR.entries);
    })

    return this.http.post<any>(environment.backend + '/entityrights/orders/' + orderId + '/suborders/' + subOrderId, {
      entries: entryEntityRight
    });
  }

  patchEntityRightById(id: any, entityRight: any): Observable<any> {
    const url = environment.backend + '/entityrights/' + id.toString()
    return this.http.patch<any>(url, entityRight).pipe(
      catchError((error: any) => {
        // Check if the error status is 404 (Not Found)
        return of(this.basicEntityRight(id, entityRight.companyId));

        throw error;
      })
    );
  }

  putEntityRightById(id: any, entityRight: any): Observable<any> {
    return this.http.put<any>(environment.backend + '/entityrights/' + id.toString(), entityRight)
  }

  getPreDefinedGlobalProperties(entity: any, entityName: string, allSelected: boolean, description: string = ""): PreDefinedProperty[] {
    let globalProperties: EntityRightGlobal = this.getGlobalEntityRights().entityRights.filter((entryRight: any) => entryRight.entity == entityName)[0];
    let preDefinedProperties: PreDefinedProperty[] = [];

    if (globalProperties.properties != undefined) {
      globalProperties.properties.forEach((prop: EntityRightsProperty) => {
        let propDef: PreDefinedProperty = new PreDefinedProperty();
        propDef.name = prop.property;
        propDef.description = prop.description;
        propDef.displayShareText = prop.displayShareText;
        propDef.selected = allSelected;
        propDef.neverShared = prop.neverShared;
        propDef.alwaysShared = prop.alwaysShared;
        propDef.hideSelector = prop.hideSelector;
        if (prop.property == "packageClass") {
          propDef.value = entity.packageClass.name;
        } else {
          // @ts-ignore
          propDef.value = entity[prop.property];
        }
        preDefinedProperties.push(propDef);
      });
    }
    return preDefinedProperties;
  }

  getPreDefinedPropertiesForCompany(): EntityRightsGlobal {

    let entityRightsGlobal: EntityRightsGlobal;
    entityRightsGlobal = this.getGlobalEntityRights();


    return entityRightsGlobal;

  }

  getPreDefinedPropertiesForOrder(order: Order, allSelected: boolean): PreDefinedOutsourceList[] {

    let preDefinedOutsourceList: PreDefinedOutsourceList[] = [];
    let preDefinedOutsource: PreDefinedOutsource[] = [];
    let preDefinedPackageOutsource: PreDefinedOutsource[] = [];
    let preDefinedPackagePropertiesOutsource: PreDefinedOutsource[] = [];
    let entityRightsGlobal: EntityRightsGlobal;
    entityRightsGlobal = this.getGlobalEntityRights();
    let preDefinedProperty: PreDefinedOutsource;
    let entityName: string = "";
    let entities: any = [];

    if (order.addressTo == undefined || order.addressFrom == undefined) {
      entities = [
        {entityName: "order", entity: order, entityDescription: "Auftrag"},
        {entityName: "contactPerson", entity: order.contactPerson, entityDescription: "Ansprechpartner"},
        {entityName: "address", entity: order.addressIdFrom!, entityDescription: "Absender"},
        {entityName: "address", entity: order.addressIdTo!, entityDescription: "Empfänger"}
      ]
    } else {
      entities = [
        {entityName: "order", entity: order, entityDescription: "Auftrag"},
        {entityName: "contactPerson", entity: order.contactPerson, entityDescription: "Ansprechpartner"},
        {entityName: "address", entity: order.addressFrom!, entityDescription: "Absender"},
        {entityName: "address", entity: order.addressTo!, entityDescription: "Empfänger"}
      ]
    }

    entities.forEach((ent: any) => {
        if (ent.entity != undefined) {
          preDefinedProperty = new PreDefinedOutsource();
          preDefinedProperty.entityId = ent.entity.id;
          preDefinedProperty.orderId = order.id;
          preDefinedProperty.name = ent.entityDescription;
          preDefinedProperty.typeClazz = ent.entityName;
          preDefinedProperty.readProperties = [];
          preDefinedProperty.readProperties = this.getPreDefinedGlobalProperties(ent.entity, ent.entityName, allSelected, ent.entityDescription);
          preDefinedOutsource.push(preDefinedProperty);
        }
      }
    );


    if (order.packageItems != undefined) {
      order.packageItems.forEach((p: Package) => {
        let preDefinedProperty: PreDefinedOutsource = new PreDefinedOutsource();
        entityName = "package";
        preDefinedProperty = new PreDefinedOutsource();
        preDefinedProperty.entityId = p.id;
        preDefinedProperty.name = "Sendung #" + p.id.toString();
        preDefinedProperty.typeClazz = entityName;
        preDefinedProperty.readProperties = this.getPreDefinedGlobalProperties(p, entityName, allSelected);
        preDefinedPackageOutsource.push(preDefinedProperty);

        if (p.packagePackageProperties != undefined) {
          p.packagePackageProperties.forEach((pP: PackageProperty) => {
            if (pP.key != undefined) {
              let preDefinedPackageProperty: PreDefinedOutsource = new PreDefinedOutsource();
              preDefinedPackageProperty.readProperties = [];
              preDefinedPackageProperty.parentId = p.id;
              preDefinedPackageProperty.entityId = pP.id;
              preDefinedPackageProperty.name = pP.key.toString();
              preDefinedPackageProperty.typeClazz = "packageProperty";
              preDefinedPackageProperty.readProperties.push({
                name: pP.key.toString(),
                description: pP.key.toString(),
                value: pP.value,
                selected: allSelected,
                hideSelector: false,
                alwaysShared: false,
                neverShared: false,
                displayShareText: "",
              });
              preDefinedPackagePropertiesOutsource.push(preDefinedPackageProperty);
            }
          });
        }
      });
    }


    preDefinedOutsourceList.push({name: "preDefinedOutsource", preDefinedOutsources: preDefinedOutsource})
    preDefinedOutsourceList.push({
      name: "preDefinedPackageOutsource",
      preDefinedOutsources: preDefinedPackageOutsource
    })
    preDefinedOutsourceList.push({
      name: "preDefinedPackagePropertiesOutsource",
      preDefinedOutsources: preDefinedPackagePropertiesOutsource
    })

    //console.log("preDefinedOutsourceList", preDefinedOutsourceList);
    return preDefinedOutsourceList;

  }

  getCompanyDefinedProperties(outsourceCompanies: OutsourceCompany[], preDefinedOutsourceList: PreDefinedOutsourceList[], parent: any = ""): CompanyDefinedOutsource[] {
    let allFalse = true;
    let allTrue = true;

    let companyDefinedList: CompanyDefinedOutsource[] = [];
    let preDefinedOutsource = preDefinedOutsourceList.filter((e) =>
      e.name == "preDefinedOutsource"
    )[0].preDefinedOutsources;
    let preDefinedPackageOutsource = preDefinedOutsourceList.filter((e) =>
      e.name == "preDefinedPackageOutsource"
    )[0].preDefinedOutsources;
    let preDefinedPackagePropertiesOutsource = preDefinedOutsourceList.filter((e) =>
      e.name == "preDefinedPackagePropertiesOutsource"
    )[0].preDefinedOutsources;

    if (outsourceCompanies != undefined) {


      outsourceCompanies.forEach((oC: OutsourceCompany) => {

        let companyDefined: CompanyDefinedOutsource = new CompanyDefinedOutsource();
        companyDefined.companyId = oC.company.id;
        if (parent != "") {
          companyDefined.parentId = parent.id;
          companyDefined.orderStatus = parent.orderStatus;
        } else {
          companyDefined.parentId = "";
          companyDefined.orderStatus = "";
        }


        companyDefined.companyName = oC.company.name;
        companyDefined.companyDefinedProperties = [];
        companyDefined.companyDefinedPackageProperties = [];

//companyDefinedProperties
        preDefinedOutsource.forEach((pP: PreDefinedOutsource) => {
          let companyDefinedProperty: CompanyDefinedOutsourceProperty = new CompanyDefinedOutsourceProperty();
          companyDefinedProperty = new CompanyDefinedOutsourceProperty();
          companyDefinedProperty.name = pP.name;
          companyDefinedProperty.entityId = pP.entityId;
          companyDefinedProperty.orderId = pP.orderId;
          companyDefinedProperty.typeClazz = pP.typeClazz;
          companyDefinedProperty.readProperties = [];

          pP.readProperties.forEach((pPP: PreDefinedProperty) => {
            let pSelection: PropertySelection = new PropertySelection();
            pSelection.property = pPP.name;
            pSelection.description = pPP.description;
            pSelection.displayShareText = pPP.displayShareText;
            pSelection.value = pPP.value!;
            pSelection.selected = pPP.selected!;
            pSelection.neverShared = pPP.neverShared;
            pSelection.alwaysShared = pPP.alwaysShared;
            pSelection.hideSelector = pPP.hideSelector;

            if (pPP.selected == true) {
              allFalse = false;
            } else {
              allTrue = false;
            }
            companyDefinedProperty.readProperties.push(pSelection);
          });
          companyDefined.companyDefinedProperties.push(companyDefinedProperty);
        });

//companyDefinedPackages
        preDefinedPackageOutsource.forEach((pP: PreDefinedOutsource) => {
          let companyDefinedProperty = new CompanyDefinedOutsourceSubProperty();
          companyDefinedProperty.name = pP.name;
          companyDefinedProperty.entityId = pP.entityId;
          companyDefinedProperty.typeClazz = pP.typeClazz;
          companyDefinedProperty.readProperties = [];

          companyDefinedProperty.subDefinedOutsourceProperties = [];

          pP.readProperties.forEach((pPP: PreDefinedProperty) => {
            let pSelection: PropertySelection = new PropertySelection();
            pSelection.property = pPP.name;
            pSelection.description = pPP.description;
            pSelection.displayShareText = pPP.displayShareText;
            pSelection.value = pPP.value!;
            pSelection.selected = pPP.selected!;
            pSelection.neverShared = pPP.neverShared;
            pSelection.alwaysShared = pPP.alwaysShared;
            pSelection.hideSelector = pPP.hideSelector;
            if (pPP.selected == true) {
              allFalse = false;
            } else {
              allTrue = false;
            }
            companyDefinedProperty.readProperties.push(pSelection);
          });


//companyDefinedPackagesProperties
          let poeOutsource: PreDefinedOutsource[] = preDefinedPackagePropertiesOutsource.filter((prop: any) => prop.parentId == pP.entityId);
          poeOutsource.forEach((pP: PreDefinedOutsource) => {
            let companyDefinedSubProperty = new CompanyDefinedOutsourceProperty();

            companyDefinedSubProperty.name = pP.name;
            companyDefinedSubProperty.entityId = pP.entityId;
            companyDefinedSubProperty.typeClazz = pP.typeClazz;

            pP.readProperties.forEach((pPP: PreDefinedProperty) => {

              let pSelection: PropertySelection = new PropertySelection();
              pSelection.property = pPP.name;
              pSelection.description = pPP.description;
              pSelection.displayShareText = pPP.displayShareText;
              pSelection.value = pPP.value!;
              pSelection.selected = pPP.selected!;
              pSelection.neverShared = pPP.neverShared;
              pSelection.alwaysShared = pPP.alwaysShared;
              pSelection.hideSelector = pPP.hideSelector;
              if (pPP.selected == true) {
                allFalse = false;
              } else {
                allTrue = false;
              }
              companyDefinedSubProperty.readProperties = [];
              companyDefinedSubProperty.readProperties.push(pSelection);
              companyDefinedProperty.subDefinedOutsourceProperties.push(companyDefinedSubProperty);
            });


          });

          companyDefined.companyDefinedPackageProperties.push(companyDefinedProperty);
        });

        companyDefined.valuesAllFalse = allFalse;
        companyDefined.valuesAllTrue = allTrue;
        companyDefinedList.push(companyDefined);

      });
    }


    return companyDefinedList;

  }


  getCompanyDefinedEntityRights(companyDefinedOutsources: CompanyDefinedOutsource[]): Observable<any> {

    let observableBatch: any = [];

    companyDefinedOutsources.forEach((companyDefinedOutsource: CompanyDefinedOutsource) => {
      companyDefinedOutsource.companyDefinedProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceProperty) => {
        observableBatch.push(this.getEntityRightById(companyDefinedOutsourceProperty.entityId, companyDefinedOutsource.companyId))
      });

      companyDefinedOutsource.companyDefinedPackageProperties.forEach((companyDefinedOutsourceSubProperty: CompanyDefinedOutsourceSubProperty) => {
        observableBatch.push(this.getEntityRightById(companyDefinedOutsourceSubProperty.entityId, companyDefinedOutsource.companyId))

        companyDefinedOutsourceSubProperty.subDefinedOutsourceProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceProperty) => {
          observableBatch.push(this.getEntityRightById(companyDefinedOutsourceProperty.entityId, companyDefinedOutsource.companyId))
        });
      });
    });

    return forkJoin(observableBatch);
  }


  matchCompanyDefinedEntityRights(companyDefinedOutsources: CompanyDefinedOutsource[], entityRights: EntityRight[]): CompanyDefinedOutsource[] {


    companyDefinedOutsources.forEach((companyDefinedOutsource: CompanyDefinedOutsource) => {
      companyDefinedOutsource.companyDefinedProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceProperty) => {

        let newCompanyDefinedOutsourceProperty: any = this.getEntityRightForCompanyDefinedRights(entityRights, companyDefinedOutsourceProperty);
        companyDefinedOutsourceProperty.readProperties = newCompanyDefinedOutsourceProperty.readProperties;


      });

      companyDefinedOutsource.companyDefinedPackageProperties.forEach((companyDefinedOutsourceSubProperty: CompanyDefinedOutsourceSubProperty) => {

        let newCompanyDefinedOutsourceProperty: any = this.getEntityRightForCompanyDefinedRights(entityRights, companyDefinedOutsourceSubProperty);
        companyDefinedOutsourceSubProperty.readProperties = newCompanyDefinedOutsourceProperty.readProperties;

        companyDefinedOutsourceSubProperty.subDefinedOutsourceProperties.forEach((companyDefinedOutsourceProperty: CompanyDefinedOutsourceProperty) => {

          let newCompanyDefinedOutsourceProperty: any = this.getEntityRightForCompanyDefinedRights(entityRights, companyDefinedOutsourceProperty, true);
          companyDefinedOutsourceProperty.readProperties = newCompanyDefinedOutsourceProperty.readProperties;

        });
      });
    });

    return companyDefinedOutsources;
  }


  getEntityRightForCompanyDefinedRights(entityRights: EntityRight[], companyDefinedOutsourceProperty: any, isPackagePackageProperty: boolean = false): any {

    entityRights.forEach((eR: EntityRight) => {
      let eRight: EntityRightEntry = eR.entries!.filter((e) => e.entityId == companyDefinedOutsourceProperty.entityId)[0];

      if (eRight != undefined) {
        if (eRight.properties != undefined) {
          if (eRight.properties.readProperties != undefined) {
            eRight.properties.readProperties.forEach((readProp) => {

              if (isPackagePackageProperty) {

                companyDefinedOutsourceProperty.readProperties.forEach((propSel: PropertySelection) => {
                  if (readProp == "key" || readProp == "value") {
                    propSel.selected = true;
                  }
                });

              } else {
                companyDefinedOutsourceProperty.readProperties.forEach((propSel: PropertySelection) => {
                  if (readProp == propSel.property) {
                    propSel.selected = true;
                  }
                });

              }


            });
          }
        }
      }


    });
    return companyDefinedOutsourceProperty;
  }

  getSubOrderEntityRights(order: Order, allSelected: boolean = false): Observable<any> {
    let companyDefined: CompanyDefinedOutsource[] = [];
    let company: OutsourceCompany[] = [];
    company.push({company: order.companyId!, sort: 1});
    let eR: any = this.getPreDefinedPropertiesForOrder(order, allSelected);
    companyDefined.push(...this.getCompanyDefinedProperties(company, eR, order));

    return this.getCompanyDefinedEntityRights(companyDefined).pipe(
      map((entityRights: any) => {
        if (entityRights != 0) {
          companyDefined = this.matchCompanyDefinedEntityRights(companyDefined, entityRights);
        }
        // console.log("getSubOrderEntityRights", companyDefined);
        return companyDefined;
      })
    )
  }

  revokeEntityRights(order: Order): CompanyDefinedOutsource[] {
    let companyDefined: CompanyDefinedOutsource[] = [];
    let company: OutsourceCompany[] = [];
    company.push({company: order.companyId!, sort: 1});
    let eR: any = this.getPreDefinedPropertiesForOrder(order, false);
    companyDefined.push(...this.getCompanyDefinedProperties(company, eR, order));
    let cO: any = this.changeCompanyDefinedOutsourceSelection(companyDefined, false);
    cO.forEach((cD: any) => {
      cD.companyDefinedProperties.forEach((a: any) => {
        if (a.typeClazz == 'order') {
          a.readProperties.forEach((r: any) => {
            if (r.property == "orderStatus" || r.property == "id") {
              r.selected = true;
            }
          })
        }
      });
    });
    return cO;
  }

  changeCompanyDefinedOutsourceSelection(companiesDefined: any, checked: boolean): any {

    companiesDefined.forEach((companyDefined: any) => {
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

    })

    return companiesDefined;
  }

  sentUpdateCompaniesEntityRights(companiesDefinedOutsource: CompanyDefinedOutsource[]): Observable<any> {
    return this.updateCompaniesEntityRights(companiesDefinedOutsource);
  }

  updateCompaniesEntityRights = (companiesDefinedOutsource: CompanyDefinedOutsource[]) => {
    const arrayOfObservables = companiesDefinedOutsource.map((c: CompanyDefinedOutsource) =>
      this.sentUpdateEntityRights(c)
    )
    return forkJoin(arrayOfObservables);
  }


  sentUpdateEntityRights(companyDefinedOutsource: CompanyDefinedOutsource): Observable<any> {
    let entryEntityDto: EntryEntityDto[] = this.transformCompanyRightsForRequest(companyDefinedOutsource);
    //console.log('Sub-Order Rights Company: ' + companyDefinedOutsource.companyName, entryEntityDto);
    return this.updateEntityRights(entryEntityDto);

  }

  sendEntityRights = (entryEntityDto: EntryEntityDto[]) => {
    const arrayOfObservables = entryEntityDto.map((eeDto: EntryEntityDto) =>
      this.putEntityRightById(eeDto.entityId, {entries: eeDto.entries})
    )
    return forkJoin(arrayOfObservables);
  }
  updateEntityRights = (entryEntityDto: EntryEntityDto[]) => {
    const arrayOfObservables = entryEntityDto.map((eeDto: EntryEntityDto) =>
      this.patchEntityRightById(eeDto.entityId, {entries: eeDto.entries})
    )
    return forkJoin(arrayOfObservables);
  }

  transformCompanyRightsForRequest(companyDefinedOutsource: CompanyDefinedOutsource): EntryEntityDto[] {


    let entityRights: EntryEntityDto[] = [];

    companyDefinedOutsource.companyDefinedProperties.forEach((cDoP: CompanyDefinedOutsourceProperty) => {
      let entityRight: EntryEntityDto = new EntryEntityDto();
      entityRight.entityId = cDoP.entityId!;
      entityRight.entries = [];
      let entry: EntryEntityRight = new EntryEntityRight();
      entry.companyId = companyDefinedOutsource.companyId!;
      entry.entityId = cDoP.entityId!;

      if (cDoP.orderId != undefined) {
        entry.orderId = cDoP.orderId;
      }

      let entryEntityRightProperty: EntryEntityRightProperty = new EntryEntityRightProperty();
      entryEntityRightProperty.readProperties = [];
      entryEntityRightProperty.readProperties.push("id");
      entryEntityRightProperty.writeProperties = [];

      cDoP.readProperties.forEach((propSelection: PropertySelection) => {

        if (propSelection.selected) {
          entryEntityRightProperty.readProperties.push(propSelection.property);
        }


      });

      if (cDoP.writeProperties == undefined) {
        cDoP.writeProperties = [];
      }

      cDoP.writeProperties.forEach((propSelection: PropertySelection) => {
        if (propSelection.selected) {
          entryEntityRightProperty.writeProperties.push(propSelection.property);
        }
      });

      entry.properties = entryEntityRightProperty;
      entityRight.entries.push(entry);
      entityRights.push(entityRight);
    });

    companyDefinedOutsource.companyDefinedPackageProperties.forEach((cDoP: CompanyDefinedOutsourceSubProperty) => {
      let entityRight: EntryEntityDto = new EntryEntityDto();
      entityRight.entityId = cDoP.entityId!;
      entityRight.entries = [];

      let entry: EntryEntityRight = new EntryEntityRight();
      entry.companyId = companyDefinedOutsource.companyId!;
      entry.entityId = cDoP.entityId!;

      let entryEntityRightProperty: EntryEntityRightProperty = new EntryEntityRightProperty();
      entryEntityRightProperty.readProperties = [];

      // always empty (for now)
      entryEntityRightProperty.writeProperties = [];

      cDoP.readProperties.forEach((propSelection: PropertySelection) => {
        if (propSelection.selected) {
          entryEntityRightProperty.readProperties.push(propSelection.property);
        }
      });

      entry.properties = entryEntityRightProperty;
      entityRight.entries.push(entry);
      entityRights.push(entityRight);
      cDoP.subDefinedOutsourceProperties.forEach((cDoP: CompanyDefinedOutsourceProperty) => {
        let entityRight: EntryEntityDto = new EntryEntityDto();
        entityRight.entityId = cDoP.entityId!;

        entityRight.entries = [];

        let entry: EntryEntityRight = new EntryEntityRight();
        entry.companyId = companyDefinedOutsource.companyId!;
        entry.entityId = cDoP.entityId!;

        if (cDoP.orderId != undefined) {
          entry.orderId = cDoP.orderId;
        }
        let entryEntityRightProperty: EntryEntityRightProperty = new EntryEntityRightProperty();
        entryEntityRightProperty.readProperties = [];

        cDoP.readProperties.forEach((propSelection: PropertySelection) => {
          if (propSelection.selected) {
            // entryEntityRightProperty.readProperties.push(propSelection.property);
            // Backend wants 'value' not the property itself
            entryEntityRightProperty.readProperties.push('value');
            entryEntityRightProperty.readProperties.push('key');
          }
        });

        // always empty writeProperties (for now)
        entryEntityRightProperty.writeProperties = [];

        entry.properties = entryEntityRightProperty;
        entityRight.entries.push(entry);
        entityRights.push(entityRight);
      });
    });


    return entityRights;
  }


}
