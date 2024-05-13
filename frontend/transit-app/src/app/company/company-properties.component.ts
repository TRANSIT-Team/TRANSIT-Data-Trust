import {Component, OnInit} from '@angular/core';
import {NgModule, enableProdMode} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {HateoasResourceService, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {DxDataGridModule, DxButtonModule} from 'devextreme-angular';
import {GlobalCompanyProperty} from './shared/company';

import {confirm} from 'devextreme/ui/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {exportDataGrid} from 'devextreme/pdf_exporter';
import {jsPDF} from 'jspdf';
import {catchError, mergeMap, map, of} from 'rxjs';
import DevExpress from "devextreme";
import {CompanyService} from "./shared/company.service";
import {SnackbarService} from "../_core/snackbar.service";


@Component({
  selector: 'app-company-properties',
  templateUrl: './company-properties.component.html',
  styleUrls: ['./company-properties.component.css']
})

export class CompanyPropertiesComponent implements OnInit {
  dataSource: GlobalCompanyProperty[];
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];

  constructor(private companyService: CompanyService, public snackbarService: SnackbarService,) {
    this.getGlobalCompanyProperties();
  }

  ngOnInit(): void {
  }

  getGlobalCompanyProperties() {
    let companyProperties: GlobalCompanyProperty[] = [];
    this.dataSource = companyProperties;
    this.companyService.getGlobalCompanyProperties().subscribe((collection: ResourceCollection<GlobalCompanyProperty>) => {
      if (collection) {
        this.dataSource = collection.resources;
      }
    })
  }

  insertRow(event: any) {
    let tmpProperty = new GlobalCompanyProperty();
    tmpProperty.key = event.data['name'];
    tmpProperty.type = "string";
    tmpProperty.name = event.data['name'];


    this.companyService.insertGlobalCompanyProperty(tmpProperty)
      .subscribe((collection: ResourceCollection<GlobalCompanyProperty>) => {
        if (collection) {
          this.dataSource = collection.resources;
        }


        this.snackbarService.openSnackBar("Eigenschaft '" + tmpProperty.key + "' angelegt", 'Ok', 'green-snackbar');


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


    console.log("");
    this.companyService.updateGlobalCompanyProperty(tmpRowData)
      .subscribe((responseRowData: GlobalCompanyProperty) => {
        this.snackbarService.openSnackBar("Datensatz aktualisiert", 'Ok', 'green-snackbar');

      });
  }

  deleteRow(event: any) {
    this.companyService.deleteGlobalCompanyPropertyById(event.data.id)
      .subscribe((result: any) => {
        this.snackbarService.openSnackBar("Datensatz gelöscht", 'Ok', 'green-snackbar');
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
