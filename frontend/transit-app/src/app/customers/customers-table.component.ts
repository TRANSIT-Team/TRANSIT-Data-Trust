import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import CustomStore from "devextreme/data/custom_store";
import {HttpParams} from "@angular/common/http";
import {Customer} from "../company/shared/company";
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CompanyService} from "../company/shared/company.service";
import {map, mergeMap} from "rxjs";
import {Address} from "../address/shared/address";
import {jsPDF} from "jspdf";
import {exportDataGrid} from "devextreme/pdf_exporter";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogAddressComponent} from "../dialog/dialog-address.component";
import {SnackbarService} from "../_core/snackbar.service";
import {DialogCustomerEditComponent} from "../dialog/dialog-customer-edit.component";

@Component({
  selector: 'app-customers-table',
  templateUrl: './customers-table.component.html',
  styleUrls: ['./customers-table.component.css']
})
export class CustomersTableComponent implements OnInit {
  dataSource: any = [];
  @Input() dialogComponent: boolean = false;
  @Output() externButtonClick = new EventEmitter<any>();
  events: Array<string> = [];
  readonly allowedPageSizes = [5, 10, 'all'];
  types = [
    {id: 'int', name: 'Zahl'},
    {id: 'boolean', name: 'Ja/Nein'},
    {id: 'string', name: 'Text'},
  ]

  constructor(private resourceHateoasService: HateoasResourceService,
              private _snackBar: MatSnackBar, private snackbarService: SnackbarService,
              private companyService: CompanyService,
              public dialog: MatDialog,) {
  }

  ngOnInit(): void {
    this.getCustomers();
  }

  chooseItem(item: any) {
    this.externButtonClick.emit(item.data);
  }

  setUpDatasource() {
    let p: any = {};

    function isNotEmpty(value: any): boolean {
      return value !== undefined && value !== null && value !== '';
    }

    this.dataSource = new CustomStore({
      key: 'id',
      load: (loadOptions: any) => {
        const params: HttpParams = new HttpParams();
        p['createdByMyCompany'] = 'true';
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
          if (i == 'filter') {
            p[i] = 'deleted==false';
          }

          if (i in loadOptions && isNotEmpty(loadOptions[i])) {
            params.set(i, JSON.stringify(loadOptions[i]));

            if (i == 'filter') {
              p[i] = 'deleted==false;' + loadOptions[i].toString();
            } else {
              p[i] = loadOptions[i].toString();
            }
          }
        });

        return this.companyService.getCustomers()
          .toPromise()
          .then((data: any) => ({
            data: data, // Assuming your API response has a 'data' property
            totalCount: data.length, // Replace with your API's total count
            // summary, groupCount, etc.
          }))
          .catch((error: any) => {
            throw 'Data Loading Error';
          });
      },
      update: (key, values) => {
        let tmpRowData = new Customer();
        tmpRowData.email = values['email'];
        tmpRowData.name = values['name'];
        tmpRowData.tel = values['phone'];

        return this.companyService.updateCustomer("", tmpRowData)
          .toPromise()
          .then(() => {
            this._snackBar.open("Adresse aktualisiert.", "Ok", {
              duration: 3000,
            });

          })
          .catch(() => {
            throw 'Update failed'
          });
      }
    });
  }

  getCustomers() {
    this.companyService.getCustomers().subscribe((customers: any) => {
      this.dataSource = customers;
    })
  }


  insertRow(event: any) {
    let tmpRowData = new Customer();
    tmpRowData.email = event.data['email'];
    tmpRowData.name = event.data['name'];
    tmpRowData.tel = event.data['phone'];

    this.companyService.insertCustomer(tmpRowData).pipe(
      mergeMap(() => this.companyService.getCustomers()),
      map((collection: ResourceCollection<Customer>) => {
        if (collection) {
          this.dataSource = collection.resources;
        }
      })
    ).subscribe(() => {
      this._snackBar.open("Kunde angelegt", "Ok", {
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
    this.resourceHateoasService.updateResourceById(Address, event.key, {
      body: tmpRowData
    }).subscribe((responseRowData: Address) => {
      this._snackBar.open("AP aktualisiert.", "Ok", {
        duration: 3000,
      });
    });
  }

  deleteRow(event: any) {
    console.log(event);
    this.resourceHateoasService.deleteResourceById(Address, event.key)
      .subscribe((result: any) => {
        this._snackBar.open("AP gelöscht.", "Ok", {
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


  openEditDialog(customerId: any) {

    let c: any = this.dataSource.filter((customer: any) => customer.id == customerId)[0];
    if (c) {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = false;
      dialogConfig.autoFocus = true;
      dialogConfig.data = {
        customer: c
      };

      const dialogRef = this.dialog.open(DialogCustomerEditComponent, dialogConfig);
      dialogRef.afterClosed().subscribe((result: any) => {

        if (result != 'canceled' && result != undefined) {


          c.name = result.name;
          c.email = result.email;
          c.tel = result.tel;


        }

      });

    }
  }


  openAddressDialog(data: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80vw";
    dialogConfig.height = "80vh";
    dialogConfig.data = {
      type: ''
    };

    const dialogRef = this.dialog.open(DialogAddressComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {

      if (result != 'canceled' && result != undefined) {
        this.updateCustomerAddress(data, result);
      }

    });
  }

  updateCustomerAddress(customerId: string, newAddress: any) {
    let c: any = this.dataSource.filter((customer: any) => customer.id == customerId)[0];
    if (c) {
      c.addressId = newAddress.id;
      c.address = newAddress;
    }

    let tmpRowData: any = {
      addressId: newAddress.id
    }
    this.companyService.updateCustomer(customerId, tmpRowData).subscribe(() => {
      this.snackbarService.openSnackBar('Kundenadresse aktualisiert.', 'Ok', 'green-snackbar');
    })
  }


}
