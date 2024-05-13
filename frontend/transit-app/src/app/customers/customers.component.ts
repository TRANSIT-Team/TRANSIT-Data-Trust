import {Component, OnInit} from '@angular/core';
import {ContactPerson} from "../contact-person/shared/contact-person";
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {MatSnackBar} from "@angular/material/snack-bar";
import {map, mergeMap} from "rxjs";
import {Address} from "../address/shared/address";
import {jsPDF} from "jspdf";
import {exportDataGrid} from "devextreme/pdf_exporter";
import {CompanyService} from "../company/shared/company.service";
import {Customer} from "../company/shared/company";
import CustomStore from "devextreme/data/custom_store";
import {HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css']
})
export class CustomersComponent implements OnInit {
  dataSource: any = [];

  addNewCustomer = true;
  loading = true;
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];
  types = [
    {id: 'int', name: 'Zahl'},
    {id: 'boolean', name: 'Ja/Nein'},
    {id: 'string', name: 'Text'},
  ]

  constructor(private resourceHateoasService: HateoasResourceService, private _snackBar: MatSnackBar, private companyService: CompanyService) {

  }

  ngOnInit(): void {
    //  this.getCustomers();
    this.checkCustomer();
  }


  checkCustomer() {
    console.log("ssss");
    this.companyService.getCustomers().subscribe((data: any) => {
      this.loading = false;
     console.log(data);

      if (data != [] && data !=undefined) {
        if (data.length > 0) {
          this.addNewCustomer = false;

        }
      }
    });

  }

  chooseItem(customer:any){
    this.addNewCustomer = false;
  }
}
