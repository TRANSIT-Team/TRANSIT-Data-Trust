import {Injectable, Type} from '@angular/core';
import {Package, PackageClass, PackageProperty} from './package';
import {
  Resource,
  HateoasResource,
  HateoasResourceService,
  HateoasResourceOperation, PagedResourceCollection, HttpMethod, ResourceCollection
} from '@lagoshny/ngx-hateoas-client';
import {forkJoin, map, mergeMap, Observable, of, Subscription} from "rxjs";
import {Order} from "../../order/shared/order";
import {GlobalService} from "../../_core/shared/global.service";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AppResource} from "../../_core/AbstractResource";

@Injectable({providedIn: 'root'})
export class PackageService {
  globalPackageProperties: any = undefined;
  globalPackageClasses: any = undefined;

  private subscriptionGlobalPackageProperties: Subscription;
  private subscriptionGlobalPackageClasses: Subscription;

  constructor(private hateoasResourceService: HateoasResourceService, private globalService: GlobalService, private http: HttpClient,) {

    this.subscriptionGlobalPackageClasses = this.globalService.globalPackageClassesSubject$.subscribe((data: any) => {
      this.globalPackageClasses = data;
    });
    this.subscriptionGlobalPackageProperties = this.globalService.globalPackagePropertiesSubject$.subscribe((data: any) => {
      this.globalPackageProperties = data;
    });
  }

  public createPackage(packageItem: Package): Observable<Package> {
    return this.hateoasResourceService.createResource(Package, {body: packageItem});
  }

  public patchPackage(packageItem: any): Observable<Package> {
    return this.hateoasResourceService.patchResourceById(Package, packageItem.id, {
      body: packageItem
    });
  }

  public getAllPackagesByPage(age: number): Observable<PagedResourceCollection<Package>> {
    return this.hateoasResourceService.getPage(Package);
  }


  public postPackagePropertyForPackageId(id: any, prop: any): Observable<any> {
    return this.http.post<any>(environment.backend + '/packageitems/' + id + '/packagepackageproperties/',
      prop
    );
  }

  public patchPackagePropertyForPackageId(id: any, prop: any): Observable<any> {
    return this.http.patch<any>(environment.backend + '/packageitems/' + id + '/packagepackageproperties/' + prop.id,
      prop
    );
  }

  patchPackagePropertiesForPackageId = (packageId: any, packagesProperties: PackageProperty[]) => {
    const arrayOfObservables = packagesProperties.map((p: PackageProperty) =>
      this.patchPackagePropertyForPackageId(packageId, p)
    )
    return forkJoin(arrayOfObservables);
  }

  postPackagePropertiesForPackageId = (packageId: any, packagesProperties: PackageProperty[]) => {
    const arrayOfObservables = packagesProperties.map((p: PackageProperty) =>
      this.postPackagePropertyForPackageId(packageId, p)
    )
    return forkJoin(arrayOfObservables);
  }

  getGlobalPackageProperties(): Observable<any> {
    if (this.globalPackageProperties != undefined) {
      return of(this.globalPackageProperties);
    } else {
      return this.hateoasResourceService.getPage(PackageProperty, {
        sort: {
          // @ts-ignore
          key: 'asc'
        },
      });
    }
  }

  getGlobalPackageClasses(): Observable<any> {
    if (this.globalPackageClasses != undefined) {
      return of(this.globalPackageClasses);
    } else {
      return this.hateoasResourceService.getPage(PackageClass, {
        sort: {
          // @ts-ignore
          name: 'asc'
        },
      });
    }
  }

}
