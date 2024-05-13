import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {Globals} from "../globals";
import {LoaderService} from "../../loader/loader.service";

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  constructor(private loaderService: LoaderService) {
  }

  public globalDataLoadingSubject = new BehaviorSubject<any>(false);
  globalDataLoading$: Observable<any> = this.globalDataLoadingSubject.asObservable();


  public globalPageTitelSubject = new BehaviorSubject<any>(false);
  globalPageTitel$: Observable<any> = this.globalPageTitelSubject.asObservable();

  private globalDataSubject = new BehaviorSubject<any>(null);
  globalData$: Observable<any> = this.globalDataSubject.asObservable();

  private globalMyUserSubject = new BehaviorSubject<any>(null);
  globalMyUser$: Observable<any> = this.globalMyUserSubject.asObservable();

  private globalMyKeyCloakUserIdSubject = new BehaviorSubject<any>(null);
  globalMyKeyCloakUserIdSubject$: Observable<any> = this.globalMyKeyCloakUserIdSubject.asObservable();

  private globalPackageClassesSubject = new BehaviorSubject<any>(null);
  globalPackageClassesSubject$: Observable<any> = this.globalPackageClassesSubject.asObservable();

  private globalPackagePropertiesSubject = new BehaviorSubject<any>(null);
  globalPackagePropertiesSubject$: Observable<any> = this.globalPackagePropertiesSubject.asObservable();

  private globalContactPersonsSubject = new BehaviorSubject<any>(null);
  globalContactPersonsSubject$: Observable<any> = this.globalContactPersonsSubject.asObservable();


  setGlobalData(data: any) {
    this.globalDataSubject.next(data);
  }

  setGlobalPackageClasses(c: any) {
    this.globalPackageClassesSubject.next(c);
  }

  setGlobalPageTitel(c: any) {
    this.globalPageTitelSubject.next(c);
  }

  setGlobalPackageProperties(c: any) {
    this.globalPackagePropertiesSubject.next(c);
  }

  setGlobalContactPersons(c: any) {
    this.globalContactPersonsSubject.next(c);
  }

  setGlobalCompany(c: any) {
    this.globalDataSubject.next(c);
  }

  setGlobalUserId(u: any) {
    this.globalMyUserSubject.next(u);
  }

  setGlobalKeyCloakUserId(u: any) {
    this.globalMyKeyCloakUserIdSubject.next(u);
  }

  setglobalDataLoading(data: any) {
    this.globalDataLoadingSubject.next(data);
  }
}
