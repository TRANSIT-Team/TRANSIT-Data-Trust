import {Injectable} from '@angular/core';
import {HateoasResourceService, PagedResourceCollection, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {KeycloakService} from 'keycloak-angular';
import {KeycloakProfile} from 'keycloak-js';
import {catchError, forkJoin, map, mergeMap, iif, Observable, of, retry, Subscription, throwError} from 'rxjs';
import {Company, CompanyAddress, GlobalCompanyProperty, Customer} from 'src/app/company/shared/company';
import {User, UserGetId} from 'src/app/user/shared/user';
import {AppResource} from 'src/app/_core/AbstractResource';
import {Address} from "../../address/shared/address";
import {Globals} from "../../_core/globals";
import {GlobalService} from "../../_core/shared/global.service";
import {AuthService} from "../../_core/auth.service";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ContactPerson} from "../../contact-person/shared/contact-person";
import {EntityRightService} from "../../entity-rights/shared/entity-right.service";
import {EntityRightGlobal, EntityRightsProperty} from "../../entity-rights/shared/entity-right";
import {AddressService} from "../../address/shared/address.service";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  keycloakInstance: KeycloakProfile;
  g: Globals = new Globals();

  globalMyCompany: Company;
  private subscription: Subscription;


  globalContactPersons: any;
  private subscriptionContactPersons: Subscription;

  constructor(private http: HttpClient, private entityRightService: EntityRightService, private keycloakService: KeycloakService, private resourceHateoasService: HateoasResourceService,
              public globals: Globals, private globalService: GlobalService, private authService: AuthService, private addressService: AddressService) {
    this.subscription = this.globalService.globalData$.subscribe((data: any) => {
      this.globalMyCompany = data;
    });
    this.subscriptionContactPersons = this.globalService.globalContactPersonsSubject$.subscribe((data: any) => {
      this.globalContactPersons = data;
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  getCompaniesWithAddresses(): Observable<any> {

    let tmpCompanies: Company[] = [];

    return this.resourceHateoasService.getPage(Company, {
      pageParams: {
        page: 0,
        size: 50
      },
    }).pipe(
      map((companyResponse: PagedResourceCollection<Company>) => {
        tmpCompanies = companyResponse.resources;
        return companyResponse;
      }),
      mergeMap((companyResponse: PagedResourceCollection<Company>) => this.getAddresses(companyResponse.resources)),
      map((companyAddresses: ResourceCollection<CompanyAddress>[]) => {
        tmpCompanies.forEach((company: Company) => {
          company.companyAddresses = [];
          companyAddresses.forEach((companyAddressResourceCollection: ResourceCollection<CompanyAddress>) => {

            let add: any = companyAddressResourceCollection.resources.filter((companyAddress: any) => companyAddress.companyId == company.id)[0];
            if (company.companyAddresses != undefined && add != undefined) {
              add.companyName = company.name;
              company.companyAddresses.push(add);
            }
          })
        });
        return tmpCompanies;
      })
    );
  }

  getAddresses = (companies: Company[] = []) => {
    const arrayOfObservables = companies.map((c: Company) =>
      c.getRelatedCollection<ResourceCollection<CompanyAddress>>('companyAddresses')
    )
    return forkJoin(arrayOfObservables);
  }

  getAddressesById = (addressIds: string[] = []) => {
    const arrayOfObservables = addressIds.map((c: string) =>
      this.addressService.getAddress(c)
    )
    return forkJoin(arrayOfObservables);
  }

  getOwnCompany(): Observable<any> {
    if (this.globalMyCompany != undefined) {
      return of(this.globalMyCompany);
    } else {
      let tmpCompany: Company = new Company();
      return this.authService.getUser().pipe(
        map((user: any) => {
          if (user == undefined) {
            throwError("user empty");
            return 0;
          } else {
            let companyId: any = user.companyId;
            return companyId;
          }
        }),
        mergeMap((companyId: any) => iif(() => companyId != 0, this.resourceHateoasService.getResource(Company, companyId, {}), of(0))),
        map((company: any) => {

          if (company != 0) {
            tmpCompany = company;
            return tmpCompany;
          } else {
            tmpCompany = new Company();
            company.id = "0";
            return tmpCompany;
          }
        }),
        mergeMap((company: Company) => iif(() => company.id != "0", company.getRelatedCollection<ResourceCollection<CompanyAddress>>('companyAddresses'), of(0))),
        map((companyAddresses: any) => {
          //  console.log(companyAddresses);
          tmpCompany.companyAddresses = [];
          Object.entries(companyAddresses.resources).forEach(([key, value], index) => {
            tmpCompany.companyAddresses?.push(companyAddresses.resources[index])
          });

          return tmpCompany;
        }),
        map((company: any) => {
          if (company != 0) {
            this.globalService.setGlobalCompany(company);
          }
          //console.log(company);
          return company;
        }),
        catchError((err, caught) => {
          return throwError(err);
        }),
        retry(0)
      );
    }
  }


  getCompanyById(id: any): Observable<any> {
    if (id != undefined) {
      return this.resourceHateoasService.getResource(Company, id, {});
    }
    return of(null);
  }

  getGlobalCompanyProperties(): Observable<any> {
    return this.resourceHateoasService.getPage(GlobalCompanyProperty, {});
  }

  insertGlobalCompanyProperty(newProperty: GlobalCompanyProperty): Observable<any> {
    // post packageProperty to api
    return this.resourceHateoasService.createResource(GlobalCompanyProperty, {
      body: newProperty
    }).pipe(
      mergeMap(() => this.resourceHateoasService.getPage(GlobalCompanyProperty, {})),
      map((collection: ResourceCollection<GlobalCompanyProperty>) => {
        return collection;
      })
    )
  }

  updateGlobalCompanyProperty(newData: any): Observable<any> {
    // post packageProperty to api
    return this.resourceHateoasService.updateResourceById(GlobalCompanyProperty, newData.id, {
      body: newData
    });
  }

  getCustomers(): Observable<any> {

    let customers: Customer[] = [];
    return this.getOwnCompany().pipe(
      // mergeMap((company: Company) => company.getRelatedCollection<ResourceCollection<Customer>>('customers'),
      mergeMap((company: Company) => this.http.get<any>(environment.backend + '/companies/' + company.id.toString() + '/customers/')),
      map((c: any) => {
        let cs: Customer[] = [];

        if (c != undefined) {
          if (c._embedded != undefined) {
            if (c._embedded.customers != undefined) {
              cs = c._embedded.customers;
            }
          }
        }
        customers = cs;
        let addressIds: string[] = [];

        customers.forEach((cs: Customer) => {
          if (cs.addressId) {
            addressIds.push(cs.addressId);
          }
        })
        return addressIds;
      }),
      mergeMap((addressIds: string[]) => iif(() => customers.length > 0, this.getAddressesById(addressIds), of(0))),
      map((addresses: any) => {
        if (addresses && addresses != 0) {

          customers.forEach((cs: Customer) => {
            let ad: Address = addresses.filter((a: Address) => a.id == cs.addressId)[0];
            if (ad) {
              cs.address = ad;
            }
          });
        }
        return customers;
      })
    );
  }

  getCompanyContactPersons(refreshFromServer: boolean = false): Observable<any> {

    if (this.globalContactPersons != undefined && !refreshFromServer) {
      return of(this.globalContactPersons);
    } else {
      return this.resourceHateoasService.getPage(ContactPerson, {
        params: {
          filter: "deleted==false",
          createdByMyCompany: true,
        }
      });
    }
  }

  insertCompanyContactPerson(cp: ContactPerson): Observable<any> {
    return this.resourceHateoasService.createResource(ContactPerson, {
      body: cp
    }).pipe(
      map((cPResponse: ContactPerson) => {
        if (cPResponse) {
          if (this.globalContactPersons != undefined) {
            // let a: any = this.globalContactPersons;
            //a.resources.push(cPResponse);
            // this.globalService.setGlobalContactPersons(a);
          }
        }
      })
    )
  }

  insertCustomer(customer: Customer): Observable<any> {
    return this.getOwnCompany().pipe(
      map((company: Company) => {
        customer.companyId = company.id;
        return company;
      }),
      // mergeMap((company: Company) => company.postRelation('customers', {body: customer},{})),
      mergeMap((company: Company) => this.http.post<any>(environment.backend + '/companies/' + company.id.toString() + '/customers/', customer))
    )
  }

  updateCustomer(customerId: string, customer: any): Observable<any> {
    return this.getOwnCompany().pipe(
      map((company: Company) => {
        return company;
      }),
      mergeMap((company: Company) => this.http.patch<any>(environment.backend + '/companies/' + company.id.toString() + '/customers/' + customerId, customer))
    )
  }


  deleteGlobalCompanyPropertyById(id: any): Observable<any> {
    return this.resourceHateoasService.deleteResourceById(GlobalCompanyProperty, id);
  }


  postCompanyDefaultSharingRights(companyId: string, defaultSharingRights: any): Observable<any> {
    const url = environment.backend + '/companies/' + companyId + '/defaultsharingrights/'
    return this.http.post<any>(url, {
      defaultSharingRights: defaultSharingRights
    }).pipe(
      catchError((error: any) => {
        throw error;
      })
    );
  }


  getCompanyDefaultSharingRights(companyId: string): Observable<any> {
    const url = environment.backend + '/companies/' + companyId + '/defaultsharingrights/'
    return this.http.get<any>(url).pipe(
      catchError((error: any) => {
        if (error.status === 404) {
          this.postDefaultCompanyDefaultSharingRights(companyId).subscribe();
        }
        return this.getDefaultSharingRights(companyId);
      })
    );
  }


  getDefaultSharingRights(companyId: string): any {
    let eR: any = this.entityRightService.getGlobalEntityRights();
    let defaultSharingRights: any = eR.entityRights.filter((item: any) => item.entity !== 'orderProperty');

    let postRights: any = [];

    defaultSharingRights.forEach((entityRight: EntityRightGlobal) => {

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

    return postRights;

  }

  postDefaultCompanyDefaultSharingRights(companyId: string): Observable<any> {
    let postRights: any = this.getDefaultSharingRights(companyId);
    return this.postCompanyDefaultSharingRights(companyId, postRights);
  }

  postCompanyFavoriteList(companyId: string, data: any): Observable<any> {
    const url = environment.backend + '/companies/' + companyId + '/favorites'
    return this.http.post<any>(url, data).pipe(
      map((response: any) => {

        return response;
      }),
      catchError((error: any) => {
        if (error.status === 404) {

        }
        throw error;
      })
    );
  }


  patchCompanyFavoriteList(id: string, companyId: string, data: any): Observable<any> {
    const url = environment.backend + '/companies/' + companyId + '/favorites/' + id
    return this.http.patch<any>(url, data).pipe(
      map((response: any) => {

        return response;
      }),
      catchError((error: any) => {
        if (error.status === 404) {

        }
        throw error;
      })
    );
  }

  deleteCompanyFavoriteList(id: string, companyId: string): Observable<any> {
    const url = environment.backend + '/companies/' + companyId + '/favorites/' + id
    return this.http.delete<any>(url).pipe(
      map((response: any) => {

        return response;
      }),
      catchError((error: any) => {
        if (error.status === 404) {

        }
        throw error;
      })
    );
  }


  getCompanyFavoriteList(companyId: string): Observable<any> {

    let companyLists: any = [];
    const url = environment.backend + '/companies/' + companyId + '/favorites'
    return this.http.get<any>(url).pipe(
      map((response: any) => {
        if (response._embedded) {
          if (response._embedded.companyFavorites) {
            companyLists = response._embedded.companyFavorites;
          }
        }

        return companyLists;
      }),
      catchError((error: any) => {
        if (error.status === 404) {

        }
        return companyLists;
      })
    );
  }

}
