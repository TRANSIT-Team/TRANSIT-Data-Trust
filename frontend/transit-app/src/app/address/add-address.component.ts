import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Address} from "./shared/address";
import {LocationPoint} from "../company/shared/company";
import {catchError, map, throwError} from "rxjs";
import {HateoasResourceService} from "@lagoshny/ngx-hateoas-client";
import {SnackbarService} from "../_core/snackbar.service";
import {AddressService} from "./shared/address.service";
import {CountryIsoCodes, CountryService} from "../_core/isoCodes";

@Component({
  selector: 'app-add-address',
  templateUrl: './add-address.component.html',
  styleUrls: ['./add-address.component.css']
})
export class AddAddressComponent implements OnInit {
  editForm: UntypedFormGroup;
  countriesWithIso: CountryIsoCodes[];
  selectedCountriesWithIso: any = 'DE';
  selectedCountry: any = 'Germany';
  sendingData=false;
  @Input() dialogComponent: boolean = false;
  @Output() externButtonClick = new EventEmitter<any>();

  constructor(private fb: UntypedFormBuilder,
              private countrieService: CountryService,
              private resourceHateoasService: HateoasResourceService,
              public snackbar: SnackbarService,
              private addressService: AddressService) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  changeCountry(country: any) {
    this.selectedCountriesWithIso = country
    this.selectedCountry = this.countriesWithIso.filter(i => i.countryCode.includes(country));
  }


  initForm() {
    this.countriesWithIso = this.countrieService.getCountries();
    this.countriesWithIso.sort((a: any, b: any) => a.name.localeCompare(b.name));
    this.editForm = this.fb.group({
      fromName: ['', [Validators.required]],
      fromCompany: ['', [Validators.required]],
      fromStreet: ['', [Validators.required]],
      fromZip: ['', [Validators.required]],
      fromCity: ['', [Validators.required]],
      fromState: ['', []],
      fromPhoneNumber: ['', []],
      companyCountry: ['Germany', [Validators.required]],
      fromAddressExtra: ['', []],
    });
  }

  putAddress() {

    let newFromAddress: Address = new Address();
    newFromAddress.addressExtra = this.editForm.controls["fromAddressExtra"].value;
    newFromAddress.street = this.editForm.controls["fromStreet"].value;
    newFromAddress.city = this.editForm.controls["fromCity"].value;
    newFromAddress.zip = this.editForm.controls["fromZip"].value.trim();
    newFromAddress.state = this.editForm.controls["fromState"].value;
    newFromAddress.phoneNumber = this.editForm.controls["fromPhoneNumber"].value;

    newFromAddress.country = this.selectedCountry;
    newFromAddress.isoCode = this.selectedCountriesWithIso;

    newFromAddress.companyName = this.editForm.controls["fromCompany"].value;
    newFromAddress.clientName = this.editForm.controls["fromName"].value;

    let newAddress = new Address();



    this.sendingData=true;
    this.addressService.createAddress(newFromAddress).pipe(
      catchError(err => {
          this.sendingData=false;
          console.log('caught mapping error and rethrowing', err);
          return throwError(err);
        }
      )
    ).subscribe((responseNewAddress: any) => {
      if (responseNewAddress) {
        newAddress = responseNewAddress;
        this.snackbar.openSnackBar('Adresse angelegt', 'Ok', 'green-snackbar');
        this.sendingData=false;
        this.initForm();
        if (this.dialogComponent) {
          this.externButtonClick.emit(responseNewAddress);
        }
      }
    });

  }


}
