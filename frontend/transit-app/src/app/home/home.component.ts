import {Component, OnInit} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';
import {BehaviorSubject, Observable, of, async, mergeMap, map} from 'rxjs';
import {AuthService} from '../_core/auth.service';
import * as echarts from 'echarts'; //https://echarts.apache.org/examples/en/#chart-type-bar
import {KeycloakProfile} from 'keycloak-js';
import {HateoasResourceService} from '@lagoshny/ngx-hateoas-client';
import {User, UserGetId} from '../user/shared/user';
import {
  Company,
  CompanyProperty,
  LocationPoint,
  CompanyAddress,
  CompanyDeliveryArea
} from '../company/shared/company';
import { Router } from '@angular/router';
import { Globals } from 'src/app/_core/globals';
import {Address} from "../address/shared/address";
import {CompanyService} from "../company/shared/company.service";



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  public isLoggedIn: Observable<boolean>;
  isLoggedIn$: any = new BehaviorSubject(false);
  userIsCompanyOwner: boolean = false;
  showAlertDeliveryArea: boolean = false;
  loggedIn$: any;
  keycloakInstance: KeycloakProfile;


  constructor(public authService: AuthService,
              private readonly keycloakService: KeycloakService,
              private hateoasResourceService: HateoasResourceService,
              private companyService:CompanyService,
              private router: Router,
              private globals: Globals) {


  }

  public ngOnInit() {
    //console.log("Init");
   // this.keycloakInstance = this.keycloakService.getKeycloakInstance().profile!;

    //this.checkLogin();

  }

  public checkLogin() {


    this.authService.getLoggedInStatus().subscribe({
      next: data => {
        this.isLoggedIn$ = (data);
        if (this.isLoggedIn$) {
          this.getUserData();
          console.log(this.router.url);
          if(this.router.url=="/"){
            this.router.navigate(['/home']);
          }
        }
      }
    });
  }

  public login() {
    this.keycloakService.login();
  }

  public register() {
    this.keycloakService.register();
  }


  getUserData() {


    this.authService.getUser().pipe(
      map((user: any) => {
        if (user.realmRoles != undefined) {
          user.realmRoles = user.realmRoles.sort((a: any, b: any) => a.localeCompare(b));
          if (user.realmRoles.includes("ownerCompany")) {
            this.userIsCompanyOwner = true;
          }
        }
        let companyId: any = user.companyId;
        return user;
      }),
      mergeMap((user: any) => this.companyService.getOwnCompany()),
      mergeMap((company: Company) => company.getRelation<CompanyDeliveryArea>('companyDeliveryArea')),
      map((deliveryArea: CompanyDeliveryArea) => {

        if (deliveryArea.deliveryAreaPolyline == null) {
          this.showAlertDeliveryArea = true;
        }


      })
    ).subscribe();
  }

  forwardDeliveryArea() {
    this.globals.scrollToSingle="deliveryarea";
    this.router.navigate(['/user/company']);
  }


}
