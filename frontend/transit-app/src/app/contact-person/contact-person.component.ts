import {Component, OnInit} from '@angular/core';
import {Address} from "../address/shared/address";
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {MatSnackBar} from "@angular/material/snack-bar";
import {map, mergeMap} from "rxjs";
import {jsPDF} from "jspdf";
import {exportDataGrid} from "devextreme/pdf_exporter";
import {ContactPerson} from "./shared/contact-person";

import {CompanyService} from "../company/shared/company.service";
import {Location} from "@angular/common";
import {GlobalService} from "../_core/shared/global.service";
import {SnackbarService} from "../_core/snackbar.service";

@Component({
  selector: 'app-contact-person',
  templateUrl: './contact-person.component.html',
  styleUrls: ['./contact-person.component.css']
})
export class ContactPersonComponent implements OnInit {


  constructor(private snackbarService: SnackbarService,
              private resourceHateoasService: HateoasResourceService,
              private _snackBar: MatSnackBar, private companyService: CompanyService,
              private location: Location,
              private globalService: GlobalService) {
  }

  ngOnInit(): void {

  }



  routeBack() {
    // this.globals.scrollToSingle = "topwindow";
    this.location.back();
  }






}
