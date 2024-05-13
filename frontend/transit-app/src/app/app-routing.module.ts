import {NgModule} from '@angular/core';
import {RouteReuseStrategy, RouterModule, Routes} from '@angular/router';
import {PackageComponent} from './packages/package.component';
import {
  PackagePropertiesDevextremeComponent
} from './packages/package-properties/package-properties-devextreme.component';
import {KeycloakService} from 'keycloak-angular';
import {KeycloakGuard} from './keycloak.guard';
import {HomeComponent} from './home/home.component';
import {TableTemplateComponent} from './table-template/table-template.component';
import {PackageClassesComponent} from './packages/package-classes/package-classes.component';
import {PackageRouteComponent} from './package-route/package-route.component';
import {MapComponent} from './map/map.component';
import {OrderComponent} from './order/order.component';
import {CompanyRegistrationComponent} from './user/company-registration.component';
import {UserSettingsComponent} from './user/user-settings/user-settings.component';
import {AddOrderComponent} from './order/order-add.component';


import {CompanyComponent} from './company/company.component';
import {NotfoundComponent} from './notfound/notfound.component';

import {CompanySettingsComponent} from './user/company-settings/company-settings.component';

import {OutsourceComponent} from './outsource/outsource.component';
import {OutsourceMapComponent} from './outsource/outsource-map.component';
import {CompanyPropertiesComponent} from './company/company-properties.component';
import {AdminGuard} from './app.admin.guard';
import {ImportDataComponent} from './import-data/import-data.component';
import {AddressComponent} from './address/address.component';
import {ContactPersonComponent} from './contact-person/contact-person.component';
import {ChatComponent} from './chat/chat.component';
import {ChatBoxComponent} from './chat/chat-box.component';
import {CustomersComponent} from './customers/customers.component';
import {HomeUserComponent} from './home/home-user.component';
import {OrdersComponent} from './orders/orders.component';
import {PackageAddComponent} from './packages/package-add.component';
import {CmrOverviewComponent} from './cmr/cmr-overview/cmr-overview.component';
import {ImprintComponent} from './home/imprint/imprint.component';
import {PrivacyPolicyComponent} from './home/privacy-policy/privacy-policy.component';
import {
  CompanyOutsourceStrategyComponent
} from './user/company-outsource-strategy/company-outsource-strategy.component';
import {OutsourceCompaniesComponent} from './outsource/outsource-companies.component';
import {AgbComponent} from './home/agb/agb.component';
import {DashboardComponent} from "./dashboard/dashboard.component";

const routes: Routes = [
  {
    path: 'agb',
    component: AgbComponent,
    title: 'Allgemeine Geschäftsbedingungen',
    canActivate: [KeycloakGuard],
    data: {reuseComponent: false},
  },

  {
    path: 'imprint',
    component: ImprintComponent,
    title: 'Impressum',
    canActivate: [KeycloakGuard],
    data: {reuseComponent: false},
  },
  {
    path: 'privacy-policy',
    component: PrivacyPolicyComponent,
    title: 'Datenschutz',
    canActivate: [KeycloakGuard],
    data: {reuseComponent: false},
  },
  {
    path: 'chat',
    component: ChatComponent,
    canActivate: [KeycloakGuard],
    title: 'Nachrichten',
    data: {reuseComponent: false},
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [KeycloakGuard],
    title: 'Dashboard',
    data: {reuseComponent: false},
  },
  {
    path: 'orders/order/:orderId',
    component: OrderComponent,
    canActivate: [KeycloakGuard],
    title: 'Auftrag bearbeiten #',
    data: {reuseComponent: false},
  },
  {
    path: 'orders',
    component: OrdersComponent,
    canActivate: [KeycloakGuard],
    title: 'Aufträge',
    data: {reuseComponent: false},
  },
  {
    path: 'orders/add',
    component: AddOrderComponent,
    canActivate: [KeycloakGuard],
    title: 'Auftrag anlegen',
    data: {reuseComponent: false},
  },
  {
    path: 'orders/add/:orderId/package',
    component: PackageAddComponent,
    canActivate: [KeycloakGuard],
    title: 'Auftrag anlegen',
    data: {reuseComponent: false},
  },
  {
    path: 'orders/order/:orderId/package/:packageId',
    component: PackageComponent,
    canActivate: [KeycloakGuard],
    title: 'Sendung bearbeiten',
  },
  {
    path: 'orders/chatbubble',
    component: ChatBoxComponent,
    canActivate: [KeycloakGuard],
    title: 'Nachrichten',
    data: {reuseComponent: false},
  },
  {
    path: 'orders/customers',
    component: CustomersComponent,
    canActivate: [KeycloakGuard],
    title: 'Kunden',
    data: {reuseComponent: false},
  },
  {
    path: 'orders/packageProperties',
    component: PackagePropertiesDevextremeComponent,
    canActivate: [KeycloakGuard],
    title: 'Globale Sendungseigenschaften',
  },
  {
    path: 'orders/addresses',
    component: AddressComponent,
    canActivate: [KeycloakGuard],
    title: 'Adressverwaltung',
  },
  {
    path: 'user/contactpersons',
    component: ContactPersonComponent,
    canActivate: [KeycloakGuard],
    title: 'Ansprechpartner',
  },

  {
    path: 'packageClasses',
    component: PackageClassesComponent,
    canActivate: [AdminGuard],
    title: 'Globale Sendungsklassen',
  },

  {
    path: 'companyProperties',
    component: CompanyPropertiesComponent,
    canActivate: [AdminGuard],
    title: 'Globale Firmeneigenschaften',
  },

  {
    path: 'package/:id',
    component: PackageComponent,
    canActivate: [KeycloakGuard],
    title: '',
    data: {reuseComponent: false},
  },
  {
    path: 'package',
    component: PackageComponent,
    canActivate: [KeycloakGuard],
    title: '',
    data: {reuseComponent: false},
  },

  {
    path: 'packageProperties',
    component: PackagePropertiesDevextremeComponent,
    canActivate: [KeycloakGuard],
    title: '',
  },
  {
    path: 'packageClasses',
    component: PackageClassesComponent,
    canActivate: [KeycloakGuard],
    title: '',
  },
  {
    path: 'packageRoute',
    component: PackageRouteComponent,
    canActivate: [KeycloakGuard],
    title: '',
  },
  {
    path: 'map',
    component: MapComponent,
    canActivate: [KeycloakGuard],
    title: '',
  },
  {
    path: 'tableTemplate',
    component: TableTemplateComponent,
    canActivate: [KeycloakGuard],
    title: '',
  },
  {
    path: 'user/settings',
    component: UserSettingsComponent,
    canActivate: [KeycloakGuard],
    title: 'Mein Konto',
  },
  {
    path: 'user/company',
    component: CompanySettingsComponent,
    canActivate: [KeycloakGuard],
    title: 'Unternehmen',
    data: {reuseComponent: false},
  },

  {
    path: 'userCompany',
    component: CompanyRegistrationComponent,
    canActivate: [KeycloakGuard],
    data: {
      role: 'companyOwner',
    },
    title: '',
  },

  {
    path: 'user/company/outsource-strategy',
    component: CompanyOutsourceStrategyComponent,
    canActivate: [KeycloakGuard],
    title: 'Unternehmen Outsourcing Standardeinstellung',
    data: {reuseComponent: false},
  },

  {
    path: '',
    component: HomeUserComponent,
    pathMatch: 'full',
    title: 'Home',
    data: {reuseComponent: false},
  },
  {
    path: 'home',
    component: HomeUserComponent,
    pathMatch: 'full',
    title: 'Home',
    data: {reuseComponent: false},
  },

  {path: '**', component: NotfoundComponent, title: '404 Not Found'},
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: 'enabled',
      useHash: false,
      anchorScrolling: 'enabled',
      onSameUrlNavigation: 'reload',
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
