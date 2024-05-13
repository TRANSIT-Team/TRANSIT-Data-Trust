import {Injectable} from '@angular/core';
import {
  Company,
  CompanyAddress, CompanyFavorite,
  LocationPoint,
  OutsourceStategie,
  OutsourceStategieCompany, OutsourceStategieCompanyRight
} from '../../company/shared/company';
import {BehaviorSubject, catchError, forkJoin, iif, map, mergeMap, Observable, of, throwError} from 'rxjs';
import {HateoasResourceService, PagedResourceCollection, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {Package} from '../../packages/shared/package';
import {
  CanReadWriteCompany, CompanyDefaultSharingRight,
  CompanyDefinedOutsource,
  CompanyDefinedOutsourceProperty, CompanyDefinedOutsourceSubProperty, DefaultCompanyProperty, DefaultSharingRight,
  EntityRight, EntityRightEntry, EntityRightGlobal, EntityRightProperties, EntityRightsGlobal, EntityRightsProperty,
  EntryEntityDto,
  EntryEntityRight,
  EntryEntityRightProperty,
  PreDefinedOutsource, PreDefinedOutsourceList, PreDefinedProperty,
  PropertySelection
} from '../../entity-rights/shared/entity-right';
import {Order, OrderProperty, SubOrder} from '../../order/shared/order';
import {PropertyResource, UniqueResource} from '../../_core/AbstractResource';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {EntityRightService} from '../../entity-rights/shared/entity-right.service';
import {SnackbarService} from '../../_core/snackbar.service';
import {Address} from "../../address/shared/address";
import {AddressService} from "../../address/shared/address.service";
import {CompanyService} from "../../company/shared/company.service";
import {OutsourceCompany} from "./outsource";


@Injectable({
  providedIn: 'root'
})
export class OutsourceService {
  private outsourceStategie = new BehaviorSubject<any>({});
  currentOutsourceStategie = this.outsourceStategie.asObservable();
  data: any;

  constructor(private companyService: CompanyService,
              private http: HttpClient,
              private addressService: AddressService,
              private hateoasResourceService: HateoasResourceService,
              private entityRightService: EntityRightService,
              private snackbarService: SnackbarService,) {
  }

  updateOursourceStrategie(outsourceStrategieCompanies: OutsourceStategieCompany[]): Observable<any> {
    this.outsourceStategie.next(outsourceStrategieCompanies);
    this.data = outsourceStrategieCompanies;
    return this.outsourceStategie;
  }

  getOursourceStrategie(): Observable<any> {
    return this.data;
  }

  provideParentReadRightsForMyContactPerson(contactPersonId: any, companyId: any): Observable<any> {

    let eRight: EntityRight = new EntityRight();
    eRight.entries = [];
    let entry: EntityRightEntry = {
      companyId: companyId,
      properties: {
        readProperties: ["name", "email", "phone"],
        writeProperties: []
      }
    }

    eRight.entries.push(entry);
    return this.entityRightService.putEntityRightById(contactPersonId, eRight);
  }

  createSubOrder(order: any, companyDefinedOutsource: CompanyDefinedOutsource): Observable<any> {
    // new Order as Suborder
    let subOrder: SubOrder = new SubOrder();
    //subOrder.id = '';

    subOrder.comment = order.orderComment;
    // from address
    let newToAddress: Address = new Address();
    if (order.addressTo != undefined) {
      newToAddress = order.addressTo;
      newToAddress.id = '';
      newToAddress.lastModifiedBy = undefined;
      newToAddress.createdBy = undefined;
      newToAddress.createDate = undefined;
      newToAddress.modifyDate = undefined;
    }

    // to address
    let newFromAddress: Address = new Address();
    if (order.addressFrom != undefined) {
      newFromAddress = order.addressFrom;
      newFromAddress.id = '';
      newFromAddress.lastModifiedBy = undefined;
      newFromAddress.createdBy = undefined;
      newFromAddress.createDate = undefined;
      newFromAddress.modifyDate = undefined;
    }

    /*
        // billing address is the address from outsourced company
        let newBillingAddress: Address = new Address();
        if (companyDefinedOutsource) {
          if (companyDefinedOutsource.companyAddresse) {

            let comAdd: CompanyAddress = companyDefinedOutsource.companyAddresse;
            newBillingAddress.id = '';
            newBillingAddress.street = comAdd.street;
            newBillingAddress.zip = comAdd.zip;
            newBillingAddress.city = comAdd.city;
            newBillingAddress.state = comAdd.state;
            newBillingAddress.country = comAdd.country;
            newBillingAddress.companyName = companyDefinedOutsource.companyName;
            newBillingAddress.isoCode = 'DE';

            let newLocation: LocationPoint = new LocationPoint;
            newLocation.type = 'Point';
            newLocation.coordinates = [50, 50];
            newBillingAddress.locationPoint = newLocation;
          }
        }
        */

    let parentOrder = new UniqueResource();
    parentOrder.id = order.id;
    subOrder.parentOrderId = parentOrder;

    subOrder.pickUpDate = order.pickUpDate;
    subOrder.pickUpDateTo = order.pickUpDateTo;
    subOrder.destinationDate = order.destinationDate;
    subOrder.destinationDateTo = order.destinationDateTo;

    //   subOrder.companyId = outsourceStategieCompany.company;
    let tmCompany = new UniqueResource();

    tmCompany.id = companyDefinedOutsource.companyId!;
    subOrder.companyId = tmCompany;

    subOrder.responsibleCompanyId = companyDefinedOutsource.companyId;
    subOrder.orderStatus = 'REQUESTED';
    subOrder.suborderType = true;
    subOrder.packageItemIds = order.packageItemIds;

    let orderProperies: any = [];
    let oProp: PropertyResource = new PropertyResource();
    oProp.key = "ParentOrderCompanyName";
    oProp.value = order.companyId.name;
    oProp.type = "text";
    orderProperies.push(oProp);

    oProp = new PropertyResource();
    oProp.key = "ParentOrderCompanyId";
    oProp.value = order.companyId.id;
    oProp.type = "text";
    orderProperies.push(oProp);

    if (order.contactPerson != undefined) {

      if (order.contactPerson.name != undefined) {
        oProp = new PropertyResource();
        oProp.key = "ParentOrderContactPersonName";
        oProp.value = order.contactPerson.name;
        oProp.type = "text";
        orderProperies.push(oProp);
      }
      if (order.contactPerson.email != undefined) {
        oProp = new PropertyResource();
        oProp.key = "ParentOrderContactPersonEmail";
        oProp.value = order.contactPerson.email;
        oProp.type = "text";
        orderProperies.push(oProp);
      }

      if (order.contactPerson.phone != undefined) {
        oProp = new PropertyResource();
        oProp.key = "ParentOrderContactPersonPhone";
        oProp.value = order.contactPerson.phone;
        oProp.type = "text";
        orderProperies.push(oProp);
      }

    }

    //console.log(orderProperies);
    subOrder.orderProperties = orderProperies;

    subOrder.contactPerson = undefined;
    subOrder.contactPersonId = undefined;

    return of(0).pipe(
      /*mergeMap(() => this.addressService.createAddress(newFromAddress)),
     map((responseNewAddress: Address) => {
        let tmpUrsc = new UniqueResource();
        tmpUrsc.id = responseNewAddress.id
        subOrder.addressIdFrom = tmpUrsc;
        return null;
      }),
      mergeMap(() => this.addressService.createAddress(newToAddress)),
      map((responseNewAddress: any) => {
        let tmpUrsc = new UniqueResource();
        tmpUrsc.id = responseNewAddress.id
        subOrder.addressIdTo = tmpUrsc;
        console.log(newBillingAddress);
        return null;
      }),
      mergeMap(() => this.addressService.createAddress(newBillingAddress)),
      map((responseNewAddress: any) => {
        let tmpUrsc = new UniqueResource();
        tmpUrsc.id = responseNewAddress.id
        subOrder.addressIdBilling = tmpUrsc;
        return null;
      }),*/

      mergeMap(() => this.companyService.getOwnCompany()),
      map((company: Company) => {

        if (company.companyAddresses != undefined) {
          if (company.companyAddresses[0] != undefined) {
            subOrder.addressIdBilling = {id: company.companyAddresses[0].id};
          }
        }
        subOrder.addressIdFrom = {id: order.addressIdFrom.id};
        subOrder.addressIdTo = {id: order.addressIdTo.id};

      }),
      mergeMap(() => this.http.post<any>(environment.backend + '/orders/' + order.id + '/suborders', subOrder)),
      map((order: Order) => {
        // console.log("suborder1", order);
        return order;
      }),
      catchError(err => {
          console.log('caught mapping error and rethrowing', err);

          this.snackbarService.openSnackBar('Ein Fehler ist aufgetreten', 'Ok', 'red-snackbar');

          return throwError(err);
        }
      )
    );
  }

  transformCompanyDefinedToEntityDefined(companyDefinedOutsource: CompanyDefinedOutsource) {
  }

  getDefaultRightsForResetOutsource(): Observable<any> {

    let eGlobal: EntityRightsGlobal = this.entityRightService.getGlobalEntityRights();

    return of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      mergeMap((c: Company) => this.companyService.getCompanyDefaultSharingRights(c.id)),
      map((cSharingRights: CompanyDefaultSharingRight) => {
        if (cSharingRights.defaultSharingRights) {

          cSharingRights.defaultSharingRights.forEach((eS: DefaultSharingRight) => {
            eS.companyProperties?.forEach((coProp: DefaultCompanyProperty) => {
              coProp.read = coProp.default;
              coProp.write = false;

              //let them read their own price
              if (coProp.property == "price") {
                coProp.read = true;
              }

              //get global defined writeable status
              let entityProps: EntityRightGlobal = eGlobal.entityRights.filter((er: EntityRightGlobal) => er.entity == eS.entity)[0];
              let eGlobalProperty: EntityRightsProperty = entityProps.properties.filter((p: EntityRightsProperty) => p.property == coProp.property)[0];
              if (eGlobalProperty) {
                coProp.write = eGlobalProperty.alwaysWriteAble;
              } else {
                console.log('Property not found global: ', coProp.property);
              }
            })
          })
        }

        return cSharingRights;
      })
    )
  }


  setUpRightsAndSend(subOrder: Order, companyDefinedOutsource: CompanyDefinedOutsource): Observable<any> {

    let newCom: CompanyDefinedOutsource = new CompanyDefinedOutsource();
    newCom.companyDefinedProperties = [];
    newCom.companyId = companyDefinedOutsource.companyId;
    newCom.companyName = companyDefinedOutsource.companyName;
    newCom.companyAddresse = companyDefinedOutsource.companyAddresse;

    companyDefinedOutsource.companyDefinedProperties.forEach((e: CompanyDefinedOutsourceProperty) => {
      let newComProp: CompanyDefinedOutsourceProperty = new CompanyDefinedOutsourceProperty();
      newComProp.readProperties = [];
      newComProp.writeProperties = [];
      newComProp.entityId = e.entityId;
      newComProp.orderId = subOrder.id;

      newComProp.readProperties = e.readProperties;

      if (e.typeClazz == 'order') {
        newComProp.entityId = subOrder.id;
        let eGlobal: EntityRightsGlobal = this.entityRightService.getGlobalEntityRights();
        let orderProps: EntityRightGlobal = eGlobal.entityRights.filter((er: EntityRightGlobal) => er.entity == 'order')[0];

        //set writeable properties if the defined so in entity-rights.json
        orderProps.properties.filter((p: EntityRightsProperty) => p.alwaysWriteAble == true).forEach((p: EntityRightsProperty) => {
          let newProp: PropertySelection = new PropertySelection();
          newProp.property = p.property;
          newProp.description = p.description;
          newProp.value = "";
          newProp.selected = true;
          newComProp.readProperties.push(newProp);
          newComProp.writeProperties.push(newProp);
        });
      }

      if (e.name == 'Absender') {
        newComProp.entityId = subOrder.addressIdFrom!.id.toString();
      }
      if (e.name == 'Empfänger') {
        newComProp.entityId = subOrder.addressIdTo!.id.toString();
      }

      newComProp.name = e.name;
      newComProp.typeClazz = e.typeClazz;
      newCom.companyDefinedProperties.push(newComProp);
    });

    newCom.companyDefinedPackageProperties = companyDefinedOutsource.companyDefinedPackageProperties;

    let entryEntityDto: EntryEntityDto[] = this.entityRightService.transformCompanyRightsForRequest(newCom);
    // console.log('Sub-Order Rights Company: ' + companyDefinedOutsource.companyName, entryEntityDto);

    let entityRightOrderProperties: EntityRight[] = [];
    if (subOrder.orderProperties != undefined) {

      subOrder.orderProperties.forEach((oProp) => {
        let entityRight: EntityRight = new EntityRight();
        let eRE: EntityRightEntry = new EntityRightEntry();
        let r: EntityRightProperties = new EntityRightProperties();
        r.readProperties = ["key", "value", "type"];
        r.writeProperties = [];
        eRE.companyId = companyDefinedOutsource.companyId;
        eRE.orderId = subOrder.id;
        eRE.properties = r;
        entityRight.entries = [];
        entityRight.entries.push(eRE);
        eRE.entityId = oProp.id;
        entityRightOrderProperties.push(entityRight)
      })
    }

    return this.entityRightService.sendEntityRights(entryEntityDto).pipe(
      mergeMap((oProps: any) => this.sendEntityRightOrderProperties(entityRightOrderProperties)),
    );
  }

  sendEntityRightOrderProperties = (entityRight: EntityRight[]) => {
    let observableBatch: any = [];
    entityRight.forEach((e: EntityRight) => {

      if (e.entries != undefined) {
        e.entries!.forEach((entry) => {
            let id = entry.entityId;
            entry.entityId = entry.entityId;
            entry.orderId = entry.orderId;
            observableBatch.push(this.entityRightService.putEntityRightById(id, e))
          }
        )
      }
    });
    return forkJoin(observableBatch);
  }

  sendEntityRightPackageItems = (packages: UniqueResource[], entityRight: EntityRight) => {
    //console.log('packages', packages);
    const arrayOfObservables = packages.map((p: UniqueResource) =>
      this.entityRightService.patchEntityRightById(p.id, entityRight)
    )
    return forkJoin(arrayOfObservables);
  }

  sendEntityRightPackagePackageProperties = (packages: Package[], entityRight: EntityRight) => {
    let observableBatch: any = [];
    packages.forEach((p: Package) => {
      if (p.packagePackageProperties != undefined) {
        p.packagePackageProperties.map((packageProperty: any) =>
          observableBatch.push(this.entityRightService.patchEntityRightById(packageProperty.id, entityRight))
        )
      }
    });
    return forkJoin(observableBatch);
  }

  getOutsourceMapCompanies(): Observable<EntityRight> {
    const url = environment.backend + '/companies/overview';
    return this.http.get<EntityRight>(url).pipe(
      catchError((error: any) => {
        throw error;
      })
    );
  }

  getOutsourceCompanies(): Observable<any> {
    let companies: Company[] = [];
    let companiesLive: Company[] = [];
    let ownCompany: Company = new Company;


    return of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      map((company: Company) => {
        ownCompany = company;
      }),
      mergeMap(() => this.getOutsourceMapCompanies()),
      map((response: any) => {
        companiesLive = response._embedded.companies;
        companies = companiesLive.filter((company: Company) => company.id != ownCompany.id);
        companies = companies.filter((company: Company) => company.name != "Programmierung-TRANSIT");

        companies.forEach((company: Company) => {
          company.favorite = '';
        });
        return companies;
      }),
      mergeMap(() => this.companyService.getCompanyFavoriteList(ownCompany.id)),
      map((response: CompanyFavorite[]) => {

        response.forEach((coFa: CompanyFavorite) => {
          if (coFa.companyIds) {
            coFa.companyIds.forEach((companyId: string) => {
              let company: Company = companies.filter((company: Company) => company.id == companyId)[0];
              if (company) {

                if (company.favorite == undefined) {
                  company.favorite = '';
                }
                if (coFa.name) {
                  company.favorite += coFa.name + ', ';
                }

              }
            })
          }
        })

        companies.forEach((company: Company) => {
          company.favorite = company.favorite!.slice(0, -2);
        });

        return companies;
      }),
    );
  }
}
