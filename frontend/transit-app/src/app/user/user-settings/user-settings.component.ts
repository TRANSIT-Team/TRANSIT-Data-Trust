import {Component, Inject, OnInit} from '@angular/core';
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
import {Company, CompanyProperty, LocationPoint, CompanyAddress} from '../../company/shared/company';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HateoasResourceService, PagedResourceCollection, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {ActivatedRoute, Router} from "@angular/router";
import {KeycloakService} from 'keycloak-angular';
import {User, UserGetId, UserProperty, UserRegistration} from '../shared/user';
import {KeycloakProfile} from 'keycloak-js';
import {CountryIsoCodes, CountryService} from '../../_core/isoCodes';
import {PropertyResource} from '../../_core/AbstractResource';
import {HttpResponse} from '@angular/common/http';
import {catchError, map, mergeMap, Observable, throwError} from 'rxjs';
import {DirectionService} from 'src/app/map/shared/direction.service';
import {SnackbarService} from 'src/app/_core/snackbar.service';
import {MatChipsModule} from '@angular/material/chips';
import {Address} from "../../address/shared/address";
import {AuthService} from "../../_core/auth.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogDeleteComponent} from "../../dialog/dialog-delete.component";
import {ExcelService} from "../../_core/shared/excel.service";
import {DOCUMENT} from "@angular/common";

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.css']
})

export class UserSettingsComponent implements OnInit {

  editClassItem: Company = new Company;
  editClassItemProperies: CompanyProperty[] = [];
  selectedClassItemProperties: CompanyProperty[];
  dataItemForm: UntypedFormGroup;
  keycloakInstance: KeycloakProfile;
  countriesWithIso: CountryIsoCodes[];
  selectedCountriesWithIso: any = '';
  selectedCountry: any = '';
  selectedCountriesWithIsoIndex: any = 1;
  selectedGender: any = '';
  currentUser: User = new User();
  currentUserId: string = "";
  currentCompany: Company;
  mainAddress: CompanyAddress = new CompanyAddress();
  alertMessage: string = '';
  alertDisplay: boolean = false;

  userIsCompanyOwner: boolean = false;
  realmRolesToAdd: string[] = [];

  genders: any[] = [{key: 'w', value: 'weiblich'}, {key: 'm', value: 'männlich'}, {key: 'd', value: 'keine Angabe'}];


  constructor(private _snackBar: MatSnackBar, public authService: AuthService, public dialog: MatDialog, private excelService: ExcelService,
              private _router: Router, @Inject(DOCUMENT) private document: Document,
              private resourceHateoasService: HateoasResourceService,
              private fb: UntypedFormBuilder,
              private keycloakService: KeycloakService,
              private directionService: DirectionService,
              private countryService: CountryService,
              private snackbarService: SnackbarService,
  ) {
  }

  ngOnInit(): void {
    this.setForm();
    this.getUserData();
  }


  setForm() {

    if (this.currentUser.id == null) {
      // @ts-ignore
      this.keycloakInstance = this.keycloakService.getKeycloakInstance().profile;

      this.dataItemForm = this.fb.group({
        userGender: ['', Validators.required],
        userJobPostion: ['', Validators.required],

      });
    } else {


      // console.log(this.currentUser);
      const gender: any = this.currentUser.userProperties?.filter(e => e.key == "Geschlecht")[0];

      if (gender != undefined) {
        if (gender.value != undefined) {
          this.selectedGender = gender.value;
        }
      }

      this.currentUser.gender = gender.value;

      let addresses: any = this.currentCompany.companyAddresses;


      this.dataItemForm = this.fb.group({
        userGender: [gender, Validators.required],
        userJobPostion: [this.currentUser.jobPosition, Validators.required],
      });


      // console.log(this.currentUser);
      // console.log('user', this.currentCompany);
    }
  }

  changeCountry(country: any) {
    this.selectedCountriesWithIso = country
    this.selectedCountry = this.countriesWithIso.filter(i => i.countryCode.includes(country));
  }

  onSubmit() {
    // console.log('onSubmit');
    if (this.dataItemForm.valid) {
      this.submitSettings();
    }
  }


  getUserData() {

    let id: any = this.keycloakInstance.id;
    //console.log(id);
    this.authService.getUser().pipe(
      map((user: any) => {
        //this.currentUserId = user.id;

        this.currentUser = user;

        if (this.currentUser.realmRoles != undefined) {

          this.currentUser.realmRoles = this.currentUser.realmRoles.sort((a: any, b: any) => a.localeCompare(b));

          if (this.currentUser.realmRoles.includes("ownerCompany")) {
            this.userIsCompanyOwner = true;
          }

          if (!this.currentUser.realmRoles.includes("plannerOrder")) {
            this.realmRolesToAdd.push("plannerOrder");
          }

          if (!this.currentUser.realmRoles.includes("creatorOrder")) {
            this.realmRolesToAdd.push("creatorOrder");
          }
        }


        let companyId: any = user.companyId;

        return user;
      }),
      mergeMap((user: any) => this.resourceHateoasService.getResource(Company, user.companyId, {})),
      map((company: Company) => {
        this.currentCompany = company;
        return company;
      }),
      mergeMap((company: Company) => company.getRelatedCollection<ResourceCollection<CompanyAddress>>('companyAddresses')),
      map((companyAddresses: ResourceCollection<CompanyAddress>) => {
        this.currentCompany.companyAddresses = [];
        Object.entries(companyAddresses.resources).forEach(([key, value], index) => {
          this.currentCompany.companyAddresses?.push(companyAddresses.resources[index])
        });
        this.setForm();

      })
    ).subscribe();
  }

  addRealmRole(role: string) {

    this.currentUser.realmRoles!.push(role);
    this.realmRolesToAdd = this.realmRolesToAdd.filter(r => r != role);

  }


  submitSettings() {
    let error: boolean = false;
    this.alertDisplay = false;

    let updateUser: User = this.currentUser;

    updateUser.lastModifiedBy = undefined;
    updateUser.createDate = undefined;
    updateUser.createdBy = undefined;
    updateUser.modifyDate = undefined;

    updateUser.keycloakId = this.keycloakService.getKeycloakInstance()?.profile?.id;
    updateUser.realmRoles = this.currentUser.realmRoles;
    //updateUser.realmRoles = ["ownerCompany","adminCompany"];

    let userProperty: PropertyResource = new PropertyResource();
    userProperty.key = "Geschlecht";
    userProperty.value = this.dataItemForm.controls["userGender"].value;
    userProperty.type = "text";
    updateUser.userProperties = [];
    updateUser.userProperties.push(userProperty);

    updateUser.jobPosition = this.dataItemForm.controls["userJobPostion"].value;


    this.resourceHateoasService.patchResourceById(User, this.currentUser.id, {
        body: updateUser
      }
    ).pipe(
      map((user: any) => {
        return user;
      }),
      catchError((err, caught) => {
        error = true;
        this.errorHandling(err);

        return throwError(err);
      }),
      map((c: any) => {
        if (!error) {

          this.snackbarService.openSnackBar('Aktualisiert', 'Ok', 'green-snackbar');
        } else {

        }
      })
    ).subscribe();

  }


  errorHandling(err: any) {


    this.alertMessage = 'Es ist ein Fehler aufgetreten. Bitte versuchen Sie es erneut.';

    if (err != null) {
      if (err.error != null) {


        if (err.error != null) {


        } else {
          this.alertMessage = '';


          Object.entries(err.error.errors).forEach(([key, value], index) => {
            this.alertMessage += err.error.errors[index].message + ' '
          });

          this.snackbarService.openSnackBar('Ein Fehler ist aufgetreten', 'Ok', 'red-snackbar');

        }
      }

    }
    this.alertDisplay = true;
  }


  downloadProfil() {

    let data: any[] = [];
    data.push(this.currentUser);
    this.excelService.exportToExcel(data, 'profil_' + this.currentUser.id);
    this.snackbarService.openSnackBar('Daten exportiert', 'Ok', 'green-snackbar');
  }

  deleteAccount() {
    const dialogRef = this.openDeleteDialog('', 'Diesen Account endgültig löschen?');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted') {
        this.deleteAccountRemotely();
      }
    });
  }

  deleteAccountRemotely() {
    this.resourceHateoasService.deleteResourceById(User, this.currentUser.id).subscribe(() => {
        this.snackbarService.openSnackBar('Bye Bye', 'Ok', 'green-snackbar');


        this.keycloakService.logout(this.document.location.origin);


      }
    );
  }

  openDeleteDialog(id: string, text: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: 'Endgültig löschen?',
      text: text,
    };
    return this.dialog.open(DialogDeleteComponent,
      dialogConfig);
  }

}
