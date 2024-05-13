import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from './_core/material.module';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PackageComponent } from './packages/package.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  Package,
  PackageClass,
  PackageProperty,
} from './packages/shared/package';
import { PackageService } from './packages/shared/packages.service';
import { DialogDeleteComponent } from './dialog/dialog-delete.component';

import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { InterceptorService } from './loader/interceptor.service';
import { MainNavComponent } from './main-nav/main-nav.component';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { HomeComponent } from './home/home.component';
import { TableTemplateComponent } from './table-template/table-template.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';

import {
  DxDataGridModule,
  DxButtonModule,
  DevExtremeModule,
  DxDateBoxModule,
} from 'devextreme-angular';
import { jsPDF } from 'jspdf';
import { exportDataGrid } from 'devextreme/pdf_exporter';

import {
  HateoasResourceService,
  NgxHateoasClientConfigurationService,
  NgxHateoasClientModule,
} from '@lagoshny/ngx-hateoas-client';
import { PackagePropertiesDevextremeComponent } from './packages/package-properties/package-properties-devextreme.component';
import { PackageClassesComponent } from './packages/package-classes/package-classes.component';
import { PackageRouteComponent } from './package-route/package-route.component';
import { MapComponent } from './map/map.component';
import { NgxMapboxGLModule } from 'ngx-mapbox-gl';

import { environment } from '../environments/environment';
import { DirectionService } from './map/shared/direction.service';
import { MapService } from './map/shared/map.service';
import { CommonModule } from '@angular/common';
import { AuthService } from './_core/auth.service';
import { OrderComponent } from './order/order.component';
import { CompanyRegistrationComponent } from './user/company-registration.component';
import { UserSettingsComponent } from './user/user-settings/user-settings.component';
import {
  Company,
  CompanyAddress,
  CompanyProperty,
  GlobalCompanyProperty,
} from './company/shared/company';

import { AddOrderComponent } from './order/order-add.component';

import { AddHeaderInterceptor } from './_core/add-header-interceptor';
import { DialogDataSharingComponent } from './dialog/dialog-data-sharing.component';

import { PopupComponent } from './map/popup/popup.component';
import { RouteReuseStrategy, TitleStrategy } from '@angular/router';
import { TemplatePageTitleStrategy } from './_core/template-title-strategy';
import { PiechartComponent } from './charts/piechart/piechart.component';
import { AreachartComponent } from './charts/areachart/areachart.component';
import { SunburstComponent } from './charts/sunburst/sunburst.component';
import { CompanyComponent } from './company/company.component';
import { NotfoundComponent } from './notfound/notfound.component';

import { CompanySettingsComponent } from './user/company-settings/company-settings.component';
import { CustomReuseStrategy } from './_core/route-reuse-strategy';

import { DialogTemplateComponent } from './dialog/dialog-template.component';
import { OutsourceComponent } from './outsource/outsource.component';
import { OutsourceMapComponent } from './outsource/outsource-map.component';
import { CompanyPropertiesComponent } from './company/company-properties.component';
import { ImportDataComponent } from './import-data/import-data.component';
import { DialogInputComponent } from './dialog/dialog-input.component';
import { AddressComponent } from './address/address.component';
import { ContactPersonComponent } from './contact-person/contact-person.component';
import { ChatComponent } from './chat/chat.component';
import { ChatBoxComponent } from './chat/chat-box.component';
import { DialogAddressComponent } from './dialog/dialog-address.component';
import { AddressTableComponent } from './address/address-table.component';
import { CustomersComponent } from './customers/customers.component';
import { CompanyService } from './company/shared/company.service';
import { AddAddressComponent } from './address/add-address.component';
import { HomeUserComponent } from './home/home-user.component';
import { HomeNonuserComponent } from './home/home-nonuser.component';
import { LoadingComponent } from './loading/loading.component';
import { CustomersAddComponent } from './customers/customers-add.component';
import { CustomersTableComponent } from './customers/customers-table.component';
import { OrdersOverviewComponent } from './order/orders-overview.component';
import { OrderSubordersComponent } from './order/order-suborders.component';
import { OrderMapComponent } from './order/order-map.component';
import { OrderPackagesComponent } from './order/order-packages.component';
import { OrderAddressComponent } from './order/order-address.component';
import { OrderInfobarComponent } from './order/order-infobar.component';
import { OrderDatesComponent } from './order/order-dates.component';
import { LoadingInfoComponent } from './loading/loading-info.component';
import { OrderPriceComponent } from './order/order-price.component';
import { DialogPackageComponent } from './dialog/dialog-package.component';
import { LoadingPlaceholderComponent } from './loading/loading-placeholder.component';
import { DialogSubordersComponent } from './dialog/dialog-suborders.component';
import { OrderSubordersRightsComponent } from './order/order-suborders-rights.component';
import { OrderStatusChipComponent } from './order/order-status-chip.component';
import { DialogOutsourceComponent } from './dialog/dialog-outsource.component';
import { OutsourceRightsComponent } from './outsource/outsource-rights.component';
import { OrdersTableComponent } from './orders/orders-table.component';
import { OrdersComponent } from './orders/orders.component';
import { OrdersInfoCardComponent } from './orders/orders-info-card.component';
import { PackageAddComponent } from './packages/package-add.component';
import { OrderCommentComponent } from './order/order-comment.component';
import { OrderParentorderInfoComponent } from './order/order-parentorder-info.component';
import { OrderDatesInfoComponent } from './order/order-dates-info.component';
import { CmrOverviewComponent } from './cmr/cmr-overview/cmr-overview.component';
import { PackageEditPropertiesComponent } from './packages/package-edit-properties.component';
import { DialogCmrComponent } from './dialog/dialog-cmr.component';
import { ImprintComponent } from './home/imprint/imprint.component';
import { PrivacyPolicyComponent } from './home/privacy-policy/privacy-policy.component';
import { HomeAnonymComponent } from './home/home-anonym/home-anonym.component';
import { CompanyOutsourceStrategyComponent } from './user/company-outsource-strategy/company-outsource-strategy.component';
import { OrderCustomerComponent } from './order/order-customer.component';
import { DialogPriceComponent } from './dialog/dialog-price.component';
import { DialogCustomerComponent } from './dialog/dialog-customer.component';
import { LoadingConfettiComponent } from './loading/loading-confetti.component';
import { OrderCommentsPublicComponent } from './order/order-comments-public.component';
import { OutsourceCompaniesComponent } from './outsource/outsource-companies.component';
import { CompanyListsComponent } from './company/company-lists.component';
import { DialogFavoritesComponent } from './dialog/dialog-favorites.component';
import { DialogInfoComponent } from './dialog/dialog-info.component';
import { AgbComponent } from './home/agb/agb.component';
import { DialogCustomerEditComponent } from './dialog/dialog-customer-edit.component';
import { DialogContactpersonComponent } from './dialog/dialog-contactperson.component';
import { ContactPersonTableComponent } from './contact-person/contact-person-table.component';
import { DialogPackagePropertiesComponent } from './dialog/dialog-package-properties.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BarchartComponent } from './charts/barchart/barchart.component';
import { BarchartOrdersComponent } from './charts/barchart-orders/barchart.orders.component';

//import { NgxPrintModule } from 'ngx-print';

//live settings
let keycloakUrl = 'https://auth.transit-project.de';
let keycloakRealm = 'TRANSIT-PROJECT';
let keycloakClientid = 'transit-frontend';
let apiUrl = environment.backend;

//debug settings
let localUseRemoteBackend = false;

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: keycloakUrl,
        realm: keycloakRealm,
        clientId: keycloakClientid,
      },
      loadUserProfileAtStartUp: true,
      initOptions: {
        onLoad: 'check-sso',
        /* pkceMethod: 'S256',*/
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html',
      },
    });
}

function initializeAuth(authService: AuthService) {
  return () =>
    authService.getLoggedInStatus().subscribe({
      next: (data) => {
        // console.log('init',data);
        authService.setLoggedInStatus(data);
      },
    });
}

@NgModule({
  declarations: [
    AppComponent,
    PackageComponent,
    DialogDeleteComponent,
    DialogTemplateComponent,
    MainNavComponent,
    HomeComponent,
    TableTemplateComponent,
    PackagePropertiesDevextremeComponent,
    PackageClassesComponent,
    PackageRouteComponent,
    MapComponent,
    OrderComponent,
    CompanyRegistrationComponent,
    UserSettingsComponent,
    AddOrderComponent,
    DialogDataSharingComponent,
    PopupComponent,
    PiechartComponent,
    AreachartComponent,
    SunburstComponent,
    CompanyComponent,
    NotfoundComponent,
    CompanySettingsComponent,
    OutsourceComponent,
    OutsourceMapComponent,
    CompanyPropertiesComponent,
    ImportDataComponent,
    DialogInputComponent,
    AddressComponent,
    ContactPersonComponent,
    ChatComponent,
    ChatBoxComponent,
    DialogAddressComponent,
    AddressTableComponent,
    CustomersComponent,
    AddAddressComponent,
    HomeUserComponent,
    HomeNonuserComponent,
    LoadingComponent,
    CustomersAddComponent,
    CustomersTableComponent,
    OrdersOverviewComponent,
    OrderSubordersComponent,
    OrderMapComponent,
    OrderPackagesComponent,
    OrderAddressComponent,
    OrderInfobarComponent,
    OrderDatesComponent,
    LoadingInfoComponent,
    OrderPriceComponent,
    DialogPackageComponent,
    LoadingPlaceholderComponent,
    DialogSubordersComponent,
    OrderSubordersRightsComponent,
    OrderStatusChipComponent,
    DialogOutsourceComponent,
    OutsourceRightsComponent,
    OrdersTableComponent,
    OrdersComponent,
    OrdersInfoCardComponent,
    PackageAddComponent,
    OrderCommentComponent,
    OrderParentorderInfoComponent,
    OrderDatesInfoComponent,
    CmrOverviewComponent,
    PackageEditPropertiesComponent,
    DialogCmrComponent,
    ImprintComponent,
    PrivacyPolicyComponent,
    HomeAnonymComponent,
    CompanyOutsourceStrategyComponent,
    OrderCustomerComponent,
    DialogPriceComponent,
    DialogCustomerComponent,
    LoadingConfettiComponent,
    OrderCommentsPublicComponent,
    OutsourceCompaniesComponent,
    CompanyListsComponent,
    DialogFavoritesComponent,
    DialogInfoComponent,
    AgbComponent,
    DialogCustomerEditComponent,
    DialogContactpersonComponent,
    ContactPersonTableComponent,
    DialogPackagePropertiesComponent,
    DashboardComponent,
    BarchartComponent,
    BarchartOrdersComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    MaterialModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    NgxHateoasClientModule.forRoot(),
    ReactiveFormsModule,
    KeycloakAngularModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    DxDataGridModule,
    DxButtonModule,
    DxDateBoxModule,

    NgxMapboxGLModule.withConfig({
      accessToken: environment.mapbox.accessToken,
    }),
    DevExtremeModule,
    // NgxPrintModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: InterceptorService,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AddHeaderInterceptor,
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      multi: true,
      deps: [AuthService],
    },
    { provide: DirectionService },
    { provide: MapService },
    { provide: CompanyService },
    { provide: TitleStrategy, useClass: TemplatePageTitleStrategy },
    { provide: RouteReuseStrategy, useClass: CustomReuseStrategy },
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor(hateoasConfig: NgxHateoasClientConfigurationService) {
    //themes.current("custom.scheme");
    if (window.location.href.includes('http://localhost:4200/')) {
      //local settings
      keycloakUrl = 'https://auth.transit-project.de';
      keycloakRealm = 'TRANSIT-PROJECT-LOCAL';
      keycloakClientid = 'transit-local-app';
      if (localUseRemoteBackend) {
        apiUrl = environment.altBackend;
        environment.backend = environment.altBackend;
      } else {
        apiUrl = environment.backend;
      }
    }
    // console.log(apiUrl);

    hateoasConfig.configure({
      http: {
        rootUrl: apiUrl,
      },
      logs: {
        verboseLogs: false,
      },
      useTypes: {
        resources: [
          Company,
          GlobalCompanyProperty,
          Package,
          PackageClass,
          PackageProperty,
        ],
      },
      halFormat: {
        collections: {
          embeddedOptional: false,
        },
      },
    });
  }
}
