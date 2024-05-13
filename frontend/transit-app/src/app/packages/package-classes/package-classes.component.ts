import {Component, OnInit} from '@angular/core';
import {NgModule, enableProdMode} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {HateoasResourceService, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {DxDataGridModule, DxButtonModule} from 'devextreme-angular';
import {PackageClass, PackageProperty} from '../shared/package';

import {confirm} from 'devextreme/ui/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {exportDataGrid} from 'devextreme/pdf_exporter';
import {jsPDF} from 'jspdf';
import {catchError, mergeMap, map, of} from 'rxjs';
import DevExpress from "devextreme";
import {GlobalService} from "../../_core/shared/global.service";


@Component({
  selector: 'app-package-classes',
  templateUrl: './package-classes.component.html',
  styleUrls: ['./package-classes.component.css']
})
export class PackageClassesComponent implements OnInit {
  dataSource: PackageClass[];
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];

  constructor(private resourceHateoasService: HateoasResourceService, private _snackBar: MatSnackBar,private globalService: GlobalService) {
    this.getGlobalPackagClasses();
  }

  ngOnInit(): void {
  }

  getGlobalPackagClasses() {
    let packageClasses: PackageClass[] = [];
    this.dataSource = packageClasses;
    this.resourceHateoasService.getPage(PackageClass, {})
      .subscribe((collection: ResourceCollection<PackageClass>) => {
        if (collection) {
          this.dataSource = collection.resources;
        }
      })
  }

  insertRow(event: any) {
    let tmpPackageProperty = new PackageClass();
    tmpPackageProperty.name = event.data['name'];



    // post packageProperty to api
    this.resourceHateoasService.createResource(PackageClass, {
      body: tmpPackageProperty
    }).pipe(
      mergeMap(() => this.resourceHateoasService.getPage(PackageClass, {})),
      map((collection: ResourceCollection<PackageClass>) => {
        if (collection) {
          this.dataSource = collection.resources;
          this.globalService.setGlobalPackageClasses(collection.resources);
        }
      })
    ).subscribe(() => {
        this._snackBar.open("Sendungsklasse '" + tmpPackageProperty.name + "' angelegt", "Ok", {
          duration: 3000,
        });
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
    this.resourceHateoasService.updateResourceById(PackageClass, event.key, {
      body: tmpRowData
    }).subscribe((responseRowData: PackageClass) => {
      this._snackBar.open("Datensatz aktualisiert.", "Ok", {
        duration: 3000,
      });
    });
  }

  deleteRow(event: any) {

    this.resourceHateoasService.deleteResourceById(PackageClass, event.key)
      .subscribe((result: any) => {
        this._snackBar.open("Datensatz gelöscht.", "Ok", {
          duration: 3000,
        });
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
