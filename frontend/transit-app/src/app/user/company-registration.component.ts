import {Component, OnInit} from '@angular/core';
import {
  AbstractControl,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {Company, CompanyProperty, LocationPoint, CompanyAddress} from '../company/shared/company';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HateoasResourceService} from '@lagoshny/ngx-hateoas-client';
import {ActivatedRoute, Router} from "@angular/router";
import {KeycloakService} from 'keycloak-angular';
import {UserRegistration} from './shared/user';
import {KeycloakProfile} from 'keycloak-js';
import {CountryIsoCodes, CountryService} from '../_core/isoCodes';
import {PropertyResource} from '../_core/AbstractResource';
import {HttpResponse} from '@angular/common/http';
import {catchError, map, mergeMap, of, throwError} from 'rxjs';
import {DirectionService} from '../map/shared/direction.service';
import {GlobalService} from "../_core/shared/global.service";
import {SnackbarService} from "../_core/snackbar.service";
import {DialogInfoComponent} from "../dialog/dialog-info.component";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {MatCheckbox} from "@angular/material/checkbox";


@Component({
  selector: 'app-company-registration',
  templateUrl: './company-registration.component.html',
  styleUrls: ['./company-registration.component.css']
})
export class CompanyRegistrationComponent implements OnInit {

  editClassItem: Company = new Company;
  editClassItemProperies: CompanyProperty[] = [];
  selectedClassItemProperties: CompanyProperty[];
  createItemForm: UntypedFormGroup;
  keycloakInstance: KeycloakProfile;
  countriesWithIso: CountryIsoCodes[];
  selectedCountriesWithIso: any = 'DE';
  selectedCountry: any = 'Germany';
  alertMessage: string = '';
  alertDisplay: boolean = false;
  sendingData: boolean = false;

  constructor(private _snackBar: MatSnackBar, public dialog: MatDialog,
              private _router: Router,
              private resourceHateoasService: HateoasResourceService,
              private fb: UntypedFormBuilder, private globalService: GlobalService,
              private keycloakService: KeycloakService,
              private directionService: DirectionService,
              private countrieService: CountryService, private snackbarService: SnackbarService) {
    this.countriesWithIso = this.countrieService.getCountries();
    this.countriesWithIso.sort((a: any, b: any) => a.name.localeCompare(b.name));

  }

  ngOnInit(): void {
    this.globalService.setglobalDataLoading(true);
    // @ts-ignore
    this.keycloakInstance = this.keycloakService.getKeycloakInstance().profile;


    this.createItemForm = this.fb.group({
      userGender: ['', Validators.required],
      userJobPostion: ['', Validators.required],
      companyName: ['', Validators.required],
      companyCity: ['', Validators.required],
      companyZip: ['', Validators.required],
      companyAddress: ['', Validators.required],
      companyState: ['', Validators.required],
      acceptTerms: [false, ((control:any) => control.value ? null : { required: true })],
      companyCountry: [this.selectedCountriesWithIso, Validators.required],
      companyComment: ['',],
      //  companyWebUrl: ['', Validators.required]
    });

  }


  changeCountry(country: any) {
    this.selectedCountry = this.countriesWithIso[country].name;
    this.selectedCountriesWithIso = this.countriesWithIso[country].countryCode
  }

  onSubmit() {
    // console.log('onSubmit');
    if (this.createItemForm.valid) {
      this.sendingData=true;
      this.registerUser();
    }
  }

  registerUser() {

    this.alertDisplay = false;
    let newUserRegistration: UserRegistration = new UserRegistration;
    newUserRegistration.keycloakId = this.keycloakService.getKeycloakInstance()?.profile?.id;
    newUserRegistration.realmRoles = ['ownerCompany', 'adminCompany', 'creatorOrder', 'plannerOrder'];

    let userProperty: PropertyResource = new PropertyResource();
    userProperty.key = "Geschlecht";
    userProperty.value = this.createItemForm.controls["userGender"].value;
    userProperty.type = "text";
    newUserRegistration.userProperties = [];
    newUserRegistration.userProperties.push(userProperty);
    newUserRegistration.jobPosition = this.createItemForm.controls["userJobPostion"].value;
    newUserRegistration.enabled = true;

    let newCom: Company | undefined = new Company();
    newCom!.name = this.createItemForm.controls["companyName"].value;
    newUserRegistration.company = newCom;
    newUserRegistration.company!.companyProperties = [];

    let companyAddress: CompanyAddress = new CompanyAddress();
    companyAddress.addressType = "Hauptanschrift";
    companyAddress.street = this.createItemForm.controls["companyAddress"].value;
    companyAddress.zip = this.createItemForm.controls["companyZip"].value.trim();
    companyAddress.city = this.createItemForm.controls["companyCity"].value;
    companyAddress.state = this.createItemForm.controls["companyState"].value;
    companyAddress.country = this.selectedCountry;
    companyAddress.isoCode = this.selectedCountriesWithIso;
    companyAddress.addressExtra = this.createItemForm.controls["companyComment"].value;

    //test values
    let companyLocation: LocationPoint = new LocationPoint;
    companyLocation.type = "Point";
    companyLocation.coordinates = [50, 50];

    companyAddress.locationPoint = companyLocation;
    newUserRegistration.company!.companyAddresses = [];
    newUserRegistration.company!.companyAddresses.push(companyAddress);

    // console.log('newUserRegistration', newUserRegistration);


    let apiAddresse: string = companyAddress.street + ',' + companyAddress.zip + ',' + companyAddress.city;

    this.directionService.getCoordinatesFromAddresse(apiAddresse).pipe(
      map((geodata: any) => {
        //companyAddress.locationPoint = geodata.features[0].geometry;
        let newCoord: number[] = [geodata.features[0].geometry.coordinates[0], geodata.features[0].geometry.coordinates[1]];
        geodata.features[0].geometry.coordinates = newCoord;

        if (newUserRegistration.company != undefined) {
          if (newUserRegistration.company.companyAddresses != undefined) {
            newUserRegistration.company.companyAddresses[0].locationPoint = geodata.features[0].geometry;
          }
        }
      }),
      mergeMap((user: any) => this.resourceHateoasService.createResource(UserRegistration, {
          body: newUserRegistration
        }, {observe: 'response'}
      )),
      catchError(err => {
          // console.log('caught mapping error and rethrowing', err);
          this.sendingData=false;
          this.errorHandling(err);
          return throwError(err);
        }
      )
    ).subscribe((response: HttpResponse<any>) => {

      //console.log(response);

      this.snackbarService.openSnackBar('Erfolgreich gespeichert. Willkommen!', 'Ok', 'green-snackbar');

      window.location.reload();
    });
  }

  errorHandling(err: any) {
    this.alertMessage = 'Es ist ein Fehler aufgetreten. Bitte versuchen Sie es erneut.';
    if (err != null) {
      if (err.error != null) {
        this.alertMessage = '';
        if (err.error.errors != undefined) {

          Object.entries(err.error.errors).forEach(([key, value], index) => {
            this.alertMessage += err.error.errors[index].message + ' '
          });
        }


      }
    }
    this.alertDisplay = true;
  }


  openInfoDialog(type:string) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      type: type
    };
    const dialogRef = this.dialog.open(DialogInfoComponent,dialogConfig);

    dialogRef.afterClosed().subscribe((result:any) => {

    });
  }

}
