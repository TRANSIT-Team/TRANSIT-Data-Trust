import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NgModule, enableProdMode} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {HateoasResourceService, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {DxDataGridModule, DxButtonModule, DxDataGridComponent} from 'devextreme-angular';
import {Address} from './shared/address';

import {confirm} from 'devextreme/ui/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {exportDataGrid} from 'devextreme/pdf_exporter';
import {jsPDF} from 'jspdf';
import {catchError, mergeMap, map, of, lastValueFrom} from 'rxjs';

import CustomStore from 'devextreme/data/custom_store';
import {HttpParams} from "@angular/common/http";
import {AddressService} from "./shared/address.service";
import {FunctionsService} from '../_core/functions.service';
import {SnackbarService} from "../_core/snackbar.service";

@Component({
  selector: 'app-address-table',
  templateUrl: './address-table.component.html',
  styleUrls: ['./address-table.component.css']
})
export class AddressTableComponent implements OnInit {

  @Input() dialogComponent: boolean = false;
  @Output() externButtonClick = new EventEmitter<any>();
  @ViewChild('gridContainer', {static: false}) dataGrid: DxDataGridComponent;

  currentPage: number = 0;
  currentPageSize: number = 5;
  //dataSource: Address[];
  defaultFilter: string = 'deleted==false';
  dataSource: any;
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];
  types = [
    {id: 'int', name: 'Zahl'},
    {id: 'boolean', name: 'Ja/Nein'},
    {id: 'string', name: 'Text'},
  ]

  constructor(private resourceHateoasService: HateoasResourceService,
              private _snackBar: MatSnackBar,
              private addressService: AddressService,
              private functionsService: FunctionsService,
              private snackbarService: SnackbarService) {
    this.setDataSource();
  }

  isNotEmpty(value: any): boolean {
    return value !== undefined && value !== null && value !== '';
  }

  ngOnInit(): void {

  }

  chooseItem(item: any) {
    this.externButtonClick.emit(item.data);
  }

  getGlobalAddresses() {
    let addresses: Address[] = [];
    this.dataSource = addresses;

    this.resourceHateoasService.getPage(Address, {
      params: {
        filter: "deleted==false",
        createdByMyCompany: true,
      },

    })
      .subscribe((collection: ResourceCollection<Address>) => {
        if (collection) {
          this.dataSource = collection.resources;
        }
      })
  }

  insertRow(event: any) {
    let tmpRowData = new Address();
    tmpRowData.clientName = event.data['clientName'];
    tmpRowData.street = event.data['street'];
    tmpRowData.zip = event.data['zip'];
    tmpRowData.city = event.data['city'];
    tmpRowData.country = event.data['country'];
    tmpRowData.phoneNumber = event.data['phoneNumber'];

    // post packageProperty to api
    this.resourceHateoasService.createResource(Address, {
      body: tmpRowData
    }).pipe(
      mergeMap(() => this.resourceHateoasService.getPage(Address, {})),
      map((collection: ResourceCollection<Address>) => {
        if (collection) {
          this.dataSource = collection.resources;
        }
      })
    ).subscribe(() => {
      this._snackBar.open("Adresse angelegt", "Ok", {
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

    this.addressService.updateAddress(tmpRowData).subscribe((responseRowData: Address) => {
      this._snackBar.open("Adresse aktualisiert in Funktion.", "Ok", {
        duration: 3000,
      });
    });
  }

  deleteRow(event: any) {
    console.log(event);
    this.resourceHateoasService.deleteResourceById(Address, event.key)
      .subscribe((result: any) => {
        this._snackBar.open("Adresse gelöscht.", "Ok", {
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

  setDataSource() {
    let p: any = {};
    this.dataSource = new CustomStore({
        key: 'id',
        load: (loadOptions: any) => {
          let transformed: boolean = false;
          const params: HttpParams = new HttpParams();
          p['createdByMyCompany'] = 'true';
          p['extraFilterParameters'] = 'noCompanyAddress==true';
          p['filter'] = this.defaultFilter;
          ['filter',
            'skip',
            'take',
            'searchExpr',
            'searchOperation',
            'searchValue',
            'requireTotalCount',
            'requireGroupCount',
            'sort',
            'select',
            'totalElements',
            'group',
            'groupSummary',
          ].forEach((i) => {
            // console.log('filter-devExpress', p['filter']);

            if (p['searchOperation'] == 'contains' && (p['filter'] != this.defaultFilter)) {
              if (!transformed) {
                //console.log('filter-devExpress', p['filter']);
                let filterNew: string = this.defaultFilter + this.functionsService.transformDevExtremeHeaderRowFilter(p['filter']);
                p['filter'] = filterNew;
                //console.log('filter-transformed', filterNew);
                transformed = true;


              }
            }

            if (i in loadOptions && this.isNotEmpty(loadOptions[i])) {
              params.set(i, JSON.stringify(loadOptions[i]));

              if (i === 'sort' && loadOptions[i][0]) {
                let {selector, desc} = loadOptions[i][0];
                //p[i] = `${selector}&${selector}.dir=${desc ? 'desc' : 'asc'}`;
                p[i] = `${selector},${desc ? 'desc' : 'asc'}`;
              } else {
                p[i] = loadOptions[i].toString();
              }
            }
          });
          return this.resourceHateoasService.getPage(Address, {
            params: p
          })
            .toPromise()
            .then((data: any) => ({
              data: data.resources.filter((a: any) => a.companyAddress == false), // Assuming your API response has a 'data' property675
              totalCount: data.totalElements, // Replace with your API's total count
              // summary, groupCount, etc.
            }))
            .catch((error: any) => {
              console.log(error);
              throw 'Data Loading Error';
            });
        },
        update: (key, values) => {

          return this.addressService.updateAddressById(key, values)
            .toPromise()
            .then(() => {
              this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');

            })
            .catch((response: any) => {
              if (response.error != undefined) {
                if (response.error.message != undefined) {
                  if (response.error.message.includes("Address cannot changed, because it is already used in an Order.")) {

                    throw 'Adresse kann nicht geändert werden, da sie schon in einem Auftrag verwendet wird.'
                  }
                }
              }
              throw 'Update failed'
            });
        }
      }
    );
  }

}
