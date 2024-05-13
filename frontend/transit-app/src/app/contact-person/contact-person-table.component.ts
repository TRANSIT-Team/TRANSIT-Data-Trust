import { Component, OnInit } from '@angular/core';
import {SnackbarService} from "../_core/snackbar.service";
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CompanyService} from "../company/shared/company.service";
import {Location} from "@angular/common";
import {GlobalService} from "../_core/shared/global.service";
import {ContactPerson} from "./shared/contact-person";
import {map, mergeMap} from "rxjs";
import {Address} from "../address/shared/address";
import {jsPDF} from "jspdf";
import {exportDataGrid} from "devextreme/pdf_exporter";

@Component({
  selector: 'app-contact-person-table',
  templateUrl: './contact-person-table.component.html',
  styleUrls: ['./contact-person-table.component.css']
})
export class ContactPersonTableComponent implements OnInit {
  dataSource: ContactPerson[];
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];
  types = [
    {id: 'int', name: 'Zahl'},
    {id: 'boolean', name: 'Ja/Nein'},
    {id: 'string', name: 'Text'},
  ]
  constructor(private snackbarService: SnackbarService,
              private resourceHateoasService: HateoasResourceService,
              private _snackBar: MatSnackBar, private companyService: CompanyService,
              private location: Location,
              private globalService: GlobalService) {
    this.getGlobalContactPersons();
  }

  ngOnInit(): void {

  }

  getGlobalContactPersons() {
    let addresses: ContactPerson[] = [];
    this.dataSource = addresses;

    this.companyService.getCompanyContactPersons()
      .subscribe((collection: ResourceCollection<ContactPerson>) => {
        if (collection) {
          this.dataSource = collection.resources;

        }
      })
  }

  routeBack() {
    // this.globals.scrollToSingle = "topwindow";
    this.location.back();
  }

  insertRow(event: any) {
    let tmpRowData = new ContactPerson();
    tmpRowData.email = event.data['email'];
    tmpRowData.name = event.data['name'];
    tmpRowData.phone = event.data['phone'];

    // post packageProperty to api
    this.companyService.insertCompanyContactPerson(tmpRowData).pipe(
      mergeMap(() => this.companyService.getCompanyContactPersons(true)),
      map((contactPersons: any) => {
        this.globalService.setGlobalContactPersons(contactPersons);
      }),
    ).subscribe(() => {
      // if (collection) {
      //  this.dataSource = collection.resources;
      //   }


      this.snackbarService.openSnackBar('AP angelegt.', 'Ok', 'green-snackbar');
    });
  }


  updateRow(event: any) {

    let tmpRowData = event.oldData;
    tmpRowData.modifyDate = null;
    tmpRowData.createDate = null;
    tmpRowData.lastModifiedBy = null;
    tmpRowData.createdBy = null;

    for (let key in event.newData) {
      tmpRowData[key] = event.newData[key];
    }

    // post packageProperty to api
    this.resourceHateoasService.updateResourceById(Address, event.key, {
      body: tmpRowData
    }).subscribe((responseRowData: Address) => {
      this.snackbarService.openSnackBar('AP aktualisiert.', 'Ok', 'green-snackbar');
    });
  }

  deleteRow(event: any) {
    this.resourceHateoasService.deleteResourceById(ContactPerson, event.key)
      .subscribe((result: any) => {
        this.snackbarService.openSnackBar('AP gelöscht.', 'Ok', 'green-snackbar');
      });
  }


  onExporting(event: any) {
    const doc = new jsPDF();

    exportDataGrid({
      jsPDFDocument: doc,
      component: event.component,
      indent: 5,
    }).then(() => {
      doc.save('Export.pdf');
    });
  }



}
