import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CompanyService} from "./shared/company.service";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {map, mergeMap, Observable, of} from "rxjs";
import {Company, CompanyFavorite} from "./shared/company";
import {SnackbarService} from "../_core/snackbar.service";
import {Order} from "../order/shared/order";

@Component({
  selector: 'app-company-lists',
  templateUrl: './company-lists.component.html',
  styleUrls: ['./company-lists.component.css']
})
export class CompanyListsComponent implements OnInit {
  @Input() companiesIds: string[];
  @Output() submitData = new EventEmitter<any>();
  createForm: UntypedFormGroup;
  editForm: UntypedFormGroup;
  selectedList: any = '';
  companyLists: CompanyFavorite[] = [];
  dataSending = false;

  constructor(private companyService: CompanyService, private fb: UntypedFormBuilder, public snackbar: SnackbarService) {

    this.createForm = this.fb.group({
      name: ['', [Validators.required]]
    });
    this.editForm = this.fb.group({
      name: ['', [Validators.required]]
    });

  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.getCompanyLists().subscribe();
  }


  getCompanyLists(): Observable<any> {
    return of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      mergeMap((c: Company) => this.companyService.getCompanyFavoriteList(c.id)),
      map((data: CompanyFavorite[]) => {
        this.companyLists = data;
      }))
  }

  submitEdit() {
    if (this.editForm.valid) {

      this.dataSending = true;
      let coFa: CompanyFavorite = this.companyLists.filter((c: CompanyFavorite) => c.id == this.selectedList)[0];
      coFa.companyIds?.push(...this.companiesIds);
      let d:any={
        companyIds:coFa.companyIds
      }

      of(0).pipe(
        mergeMap(() => this.companyService.getOwnCompany()),
        mergeMap((c: Company) => this.companyService.patchCompanyFavoriteList(coFa.id, c.id, d))
      ).subscribe((data: any) => {
        this.dataSending = false;
        this.submitData.emit(data.name);

      })
    }

  }

  submitNew() {
    if (this.createForm.valid) {

      let cF: CompanyFavorite = new CompanyFavorite();
      cF.name = this.createForm.controls["name"].value;
      cF.companyIds = this.companiesIds;
      this.dataSending = true;
      of(0).pipe(
        mergeMap(() => this.companyService.getOwnCompany()),
        mergeMap((c: Company) => this.companyService.postCompanyFavoriteList(c.id, cF)),
        mergeMap(() => this.getCompanyLists())
      ).subscribe((data: any) => {
        this.dataSending = false;
        this.submitData.emit(this.createForm.controls["name"].value);
      })
    }
  }
}
