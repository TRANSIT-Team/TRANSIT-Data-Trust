import {Component, OnInit} from '@angular/core';
import {NgModule, enableProdMode} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {HateoasResourceService, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {DxDataGridModule, DxButtonModule} from 'devextreme-angular';
import {Address} from './shared/address';

import {confirm} from 'devextreme/ui/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {exportDataGrid} from 'devextreme/pdf_exporter';
import {jsPDF} from 'jspdf';
import {catchError, mergeMap, map, of} from 'rxjs';

@Component({
  selector: 'app-addresse',
  templateUrl: './address.component.html',
  styleUrls: ['./address.component.css']
})
export class AddressComponent implements OnInit {

  addAddress = false;
  dataSource: Address[];
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];
  types = [
    {id: 'int', name: 'Zahl'},
    {id: 'boolean', name: 'Ja/Nein'},
    {id: 'string', name: 'Text'},
  ]

  constructor(private resourceHateoasService: HateoasResourceService, private _snackBar: MatSnackBar) {

  }

  ngOnInit(): void {

  }

  afterAdd() {
    this.addAddress = false;
  }
}
