import {Injectable} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';
import {
  BehaviorSubject,
  delay,
  from,
  map,
  mergeMap,
  iif,
  Observable,
  of,
  Subscription,
  catchError,
  throwError
} from 'rxjs';
import {User, UserGetId} from "../user/shared/user";
import {GlobalService} from "./shared/global.service";
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {Company, CompanyAddress} from "../company/shared/company";
import {LoaderService} from "../loader/loader.service";
import {PackageService} from "../packages/shared/packages.service";
import {CompanyService} from "../company/shared/company.service";
import {ContactPerson} from "../contact-person/shared/contact-person";


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  globalMyKeyCloakUserId: any = undefined;
  globalMyUserId: any = undefined;
  globalDataLoaded: any = undefined;
  private subscriptionGlobalDataLoading: Subscription;
  private subscriptionMyKeyCloakUserId: Subscription;
  private subscriptionMyUserId: Subscription;
  loggedIn: boolean;

  constructor(private keycloakService: KeycloakService,
              private globalService: GlobalService,
              private resourceHateoasService: HateoasResourceService,
              private loaderService: LoaderService,
              private packageService: PackageService,
  ) {

    this.subscriptionMyUserId = this.globalService.globalMyUser$.subscribe((data: any) => {
      this.globalMyUserId = data;
    });
    this.subscriptionMyKeyCloakUserId = this.globalService.globalMyKeyCloakUserIdSubject$.subscribe((data: any) => {
      this.globalMyKeyCloakUserId = data;
    });
    this.subscriptionGlobalDataLoading = this.globalService.globalDataLoading$.subscribe((data: any) => {
      this.globalDataLoaded = data;
    });
  }

  getLoggedInStatus(): Observable<boolean> {
    const observableKc = from(this.keycloakService.isLoggedIn());
    return observableKc;
  }

  setLoggedInStatus(state: boolean) {
    this.loggedIn = state;
  }

  getLoggedIn() {
    return this.loggedIn;
  }

  getUserRoles(allRoles: boolean = true) {
    const observableKc = from(this.keycloakService.getUserRoles(allRoles));
    return observableKc;
  }


  getKeyCloakUserId() {
    if (this.globalMyKeyCloakUserId == undefined) {
      let keycloakInstance: any = this.keycloakService.getKeycloakInstance().profile;
      let id: any = keycloakInstance.id;
      this.globalMyKeyCloakUserId = id;
      this.globalService.setGlobalKeyCloakUserId(id);
    }
    return this.globalMyKeyCloakUserId;
  }

  getUser(): Observable<any> {

    if (this.globalMyUserId != undefined) {
      return of(this.globalMyUserId);
    } else {
      return this.resourceHateoasService.getResource(UserGetId, this.getKeyCloakUserId(), {}).pipe(
        mergeMap((userId: any) => this.resourceHateoasService.getResource(User, userId.userId, {})),
        map((u: User) => {
          this.globalService.setGlobalUserId(u)
          this.globalService.setglobalDataLoading(false);
        }),
      )
    }
  }

  getCompanyContactPersons(): Observable<any> {
    return this.resourceHateoasService.getPage(ContactPerson, {
      params: {
        filter: "deleted==false",
        createdByMyCompany: true,
      }

    });
  }

  setStateLoading(b: any) {
    this.loaderService.isCollectingData.next(b);
  }

  initLoadingStart(): Observable<any> {
    ////console.log("Start Loading Global Data");
    let keycloakInstance: any = this.keycloakService.getKeycloakInstance().profile;
    if (keycloakInstance == undefined) {
      this.globalService.setglobalDataLoading(true);
      return of(0);
    } else {
      let id: any = keycloakInstance.id;
      let c: Company = new Company();
      this.globalService.setGlobalKeyCloakUserId(id);
      let userNotReady: boolean = false;
      return this.resourceHateoasService.getResource(UserGetId, id, {}).pipe(
        mergeMap((userId: any) => this.resourceHateoasService.getResource(User, userId.userId, {})),
        map((u: User) => {
          if (u != undefined) {
            this.globalService.setGlobalUserId(u)
          } else {
            userNotReady == true;
          }
          return u;
        }),
        mergeMap((u: User) => iif(() => userNotReady == false, this.resourceHateoasService.getResource(Company, u.companyId!, {}), of(0))),
        map((company: any) => {

          if (company != 0) {
            c = company;
            return company;
          } else {
            c.id = "0";
            return c;
          }
        }),
        mergeMap((company: Company) => iif(() => company.id != "0", company.getRelatedCollection<ResourceCollection<CompanyAddress>>('companyAddresses'), of(0))),
        map((companyAddresses: any) => {
          // //console.log(companyAddresses);
          c.companyAddresses = [];
          Object.entries(companyAddresses.resources).forEach(([key, value], index) => {
            c.companyAddresses?.push(companyAddresses.resources[index])
          });

          return c;
        }),
        map((company: any) => {
          if (!userNotReady) {
            this.globalService.setGlobalCompany(company);
          }
          // //console.log("End Loading Global Data");
          return company;
        }),
        mergeMap(() => this.packageService.getGlobalPackageClasses()),
        map((pClasses: any) => {
          this.globalService.setGlobalPackageClasses(pClasses);
        }),
        mergeMap(() => this.packageService.getGlobalPackageProperties()),
        map((pClasses: any) => {
          this.globalService.setGlobalPackageProperties(pClasses);
        }),
        mergeMap(() => this.getCompanyContactPersons()),
        map((contactPersons: any) => {
          this.globalService.setGlobalContactPersons(contactPersons);
        }),
        catchError(err => {
            if (err != undefined) {
              this.setStateLoading(false);
            }
            return of(err);
          }
        )
      );
    }
  }

}
