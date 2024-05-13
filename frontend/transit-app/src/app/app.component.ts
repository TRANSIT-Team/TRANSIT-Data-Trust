import {ViewportScroller} from '@angular/common';
import {AfterViewInit, Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {NavigationEnd, NavigationStart, Router} from '@angular/router';

import {KeycloakService} from 'keycloak-angular';
import {KeycloakProfile} from 'keycloak-js';
import {BehaviorSubject, of, Subscription} from 'rxjs';
import {LoaderService} from './loader/loader.service';
import {AuthService} from './_core/auth.service';
import {Globals} from './_core/globals';
import {CompanyService} from "./company/shared/company.service";
import {GlobalService} from "./_core/shared/global.service";

import {CompanyRegistrationComponent} from './user/company-registration.component';
// @ts-ignore
import deMessages from "./_core/de.json";
import {locale, loadMessages} from "devextreme/localization";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  providers: [Globals],
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  @ViewChildren(CompanyRegistrationComponent) companyRegistrationComponent: QueryList<CompanyRegistrationComponent>;
  title = 'transit-app';
  isLoggedIn$: any = new BehaviorSubject(false);
  loggedIn$: any;

  basicKeycloakRoles: string[] = ['manage-account', 'manage-account-links', 'view-profile', 'offline_access', 'uma_authorization', 'default-roles-transit-project-local', 'default-roles-transit-project'];
  firstChildData$: any;
  globalDataLoaded: any = false;
  localDataLoaded: any = false;


  private subscriptionGlobalDataLoading: Subscription;

  constructor(public globalService: GlobalService,
              private companyService: CompanyService,
              router: Router,
              public authService: AuthService,
              private readonly keycloak: KeycloakService,
              public loaderService: LoaderService, private globals: Globals, private viewportScroller: ViewportScroller) {

    loadMessages(deMessages);
    locale(navigator.language);

    this.subscriptionGlobalDataLoading = this.globalService.globalDataLoading$.subscribe((data: any) => {
      this.globalDataLoaded = data;
    });


    router.events.forEach((event) => {
      if (event instanceof NavigationEnd) {
        if (this.globals.scrollTo != "") {
          //console.log(this.globals.scrollTo);
          this.viewportScroller.scrollToAnchor(this.globals.scrollTo);
          this.globals.scrollTo = ""
        }
      }
    });

  }

  ngOnInit(): void {

  }

  setLoggedIn(data: any) {

    this.isLoggedIn$ = of(data);

  }

  reloadStartData(once: any = false) {
    this.authService.initLoadingStart().subscribe((data) => {
      //  console.log(data);
      if (data != 0) {
        this.authService.setStateLoading(false);
        setTimeout(() => {
          this.localDataLoaded = true;
        }, 1000);

      } else {
        if (!once) {
          setTimeout(() => {
            this.reloadStartData(true)

          }, 2000);
        } else {
          this.authService.setStateLoading(false);
          this.localDataLoaded = true;
        }
      }
    });
  }

  ngAfterViewInit() {
    this.authService.getLoggedInStatus().subscribe((data) => {
      this.setLoggedIn(data);
      if (data == false) {
        this.authService.setStateLoading(false);
      }

    });

    let top = document.getElementById('topwindow');
    if (top !== null) {
      top.scrollIntoView();
      top = null;
    }
    this.reloadStartData();

  }


}
