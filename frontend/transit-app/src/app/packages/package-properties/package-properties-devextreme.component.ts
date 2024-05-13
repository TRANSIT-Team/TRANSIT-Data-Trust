import {Component, OnInit} from '@angular/core';
import {NgModule, enableProdMode} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {HateoasResourceService, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {DxDataGridModule, DxButtonModule} from 'devextreme-angular';
import {PackageProperty} from '../shared/package';

import {confirm} from 'devextreme/ui/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {exportDataGrid} from 'devextreme/pdf_exporter';
import {jsPDF} from 'jspdf';
import {catchError, mergeMap, map, of} from 'rxjs';
import {GlobalService} from "../../_core/shared/global.service";

@Component({
  selector: 'app-package-properties-devextreme',
  templateUrl: './package-properties-devextreme.component.html',
  styleUrls: ['./package-properties-devextreme.component.css']
})
export class PackagePropertiesDevextremeComponent implements OnInit {
  dataSource: PackageProperty[];
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];
  types = [
    {id: 'int', name: 'Zahl'},
    {id: 'boolean', name: 'Ja/Nein'},
    {id: 'string', name: 'Text'},
  ]

  constructor(private resourceHateoasService: HateoasResourceService, private _snackBar: MatSnackBar, private globalService: GlobalService) {
    this.getGlobalPackageProperties();
  }

  ngOnInit(): void {

  }

  getGlobalPackageProperties() {
    let packageProperties: PackageProperty[] = [];
    this.dataSource = packageProperties;

    this.resourceHateoasService.getPage(PackageProperty, {
      sort: {key: 'ASC'}
    })
      .subscribe((collection: ResourceCollection<PackageProperty>) => {
        if (collection) {
          this.dataSource = collection.resources;

        }
      })
  }

  insertRow(event: any) {
    let tmpRowData = new PackageProperty();
    tmpRowData.key = event.data['key'];
    tmpRowData.defaultValue = event.data['defaultValue'];
    tmpRowData.type = event.data['type'];

    // post packageProperty to api
    this.resourceHateoasService.createResource(PackageProperty, {
      body: tmpRowData
    }).pipe(
      mergeMap(() => this.resourceHateoasService.getPage(PackageProperty, {
        sort: {key: 'ASC'}
      })),
      map((collection: ResourceCollection<PackageProperty>) => {
        if (collection) {
          this.dataSource = collection.resources;
          this.globalService.setGlobalPackageProperties(collection);
        }
      })
    ).subscribe(() => {
      this._snackBar.open("Sendungseigenschaft angelegt", "Ok", {
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
    this.resourceHateoasService.updateResourceById(PackageProperty, event.key, {
      body: tmpRowData
    }).pipe(
      mergeMap(() => this.resourceHateoasService.getPage(PackageProperty, {
        sort: {key: 'ASC'}
      })),
      map((collection: ResourceCollection<PackageProperty>) => {
        if (collection) {
          this.dataSource = collection.resources;
          this.globalService.setGlobalPackageProperties(collection);
        }
      })
    ).subscribe(() => {
      this._snackBar.open("Sendungseigenschaft aktualisiert.", "Ok", {
        duration: 3000,
      });
    });
  }

  deleteRow(event: any) {
    console.log(event);
    this.resourceHateoasService.deleteResourceById(PackageProperty, event.key)
      .pipe(
        mergeMap(() => this.resourceHateoasService.getPage(PackageProperty, {
          sort: {key: 'ASC'}
        })),
        map((collection: ResourceCollection<PackageProperty>) => {
          if (collection) {
            this.dataSource = collection.resources;
            this.globalService.setGlobalPackageProperties(collection);
          }
        })
      ).subscribe((result: any) => {
        this._snackBar.open("Sendungseigenschaft gelöscht.", "Ok", {
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
