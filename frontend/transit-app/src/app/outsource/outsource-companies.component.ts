import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {OutsourceService} from "./shared/outsource-component.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogInputComponent} from "../dialog/dialog-input.component";
import {Order, OrderComment} from "../order/shared/order";
import {DialogFavoritesComponent} from "../dialog/dialog-favorites.component";
import {SnackbarService} from "../_core/snackbar.service";
import {CompanyService} from "../company/shared/company.service";
import {iif, map, mergeMap, Observable, of} from "rxjs";
import {Company, CompanyFavorite} from "../company/shared/company";
import {DialogDeleteComponent} from "../dialog/dialog-delete.component";
import {DxDataGridComponent} from "devextreme-angular";
import * as mapboxgl from "mapbox-gl";

@Component({
  selector: 'app-outsource-companies',
  templateUrl: './outsource-companies.component.html',
  styleUrls: ['./outsource-companies.component.css']
})
export class OutsourceCompaniesComponent implements OnInit {
  @ViewChild(DxDataGridComponent, {static: false}) dataGrid: DxDataGridComponent;
  @Input() outsourceMapCompanies: Company[];
  @Input() order: Order;

  @Output() submitData = new EventEmitter<any>();
  @Output() submitListData = new EventEmitter<any>();

  dataSource: any = [];
  companyListFilterData: any = [];
  companyLists: CompanyFavorite[] = [];
  loading = false;
  dataSending = false;


  constructor(private outsourceService: OutsourceService, public dialog: MatDialog, private snackbarService: SnackbarService, private companyService: CompanyService) {
  }

  ngOnInit(): void {
    this.getCompanies();
  }

  getCompanies() {
    this.getCompanyLists().pipe(
      mergeMap(() => this.outsourceService.getOutsourceCompanies())
    ).subscribe((data: any) => {
      this.loading = false;
    });
  }

  getCompanyLists(): Observable<any> {
    return of(0).pipe(
      mergeMap(() => iif(() => this.outsourceMapCompanies != undefined && this.outsourceMapCompanies.length > 0, of(this.outsourceMapCompanies), this.outsourceService.getOutsourceCompanies())),
      map((data: any) => {
        this.dataSource = data;
      }),
      mergeMap(() => this.companyService.getOwnCompany()),
      mergeMap((c: Company) => this.companyService.getCompanyFavoriteList(c.id)),
      map((data: CompanyFavorite[]) => {
        this.companyLists = data;
        this.companyLists.forEach((coFa: CompanyFavorite) => {
          this.companyListFilterData.push(coFa.name);
          coFa.companyIds?.forEach((companyId: string) => {
            if (coFa.companies == undefined) {
              coFa.companies = [];
            }
            coFa.companies.push(this.dataSource.filter((company: any) => company.id == companyId)[0]);
          })
        });
      })
    );
  }


  openDialog(companiesIds: any): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      companiesIds: companiesIds
    };
    return this.dialog.open(DialogFavoritesComponent,
      dialogConfig);
  }


  openEditDialog(companyId: any, listId: any) {

    let listCompanies: any = [];
    listCompanies.push(companyId);

    const dialogRef = this.openDialog(listCompanies);
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result != 'canceled' && result != undefined) {

        let list: CompanyFavorite = this.companyLists.filter((c: CompanyFavorite) => c.id == listId)[0];
        let company: any = this.dataSource.filter((data: any) => data.id == companyId)[0];
        this.moveCompanyToList(companyId, list.name, result);
        company.favorite = result;

        this.snackbarService.openSnackBar('Aktualisiert', 'Ok', 'green-snackbar');
      }
    });
  }


  moveCompanyToList(companyId: string, oldListName: string, newListName: string) {
    let company: Company = this.dataSource.filter((company: any) => company.id == companyId)[0];
    let oldList: CompanyFavorite = this.companyLists.filter((c: CompanyFavorite) => c.name == oldListName)[0];
    let newList: CompanyFavorite = this.companyLists.filter((c: CompanyFavorite) => c.name == newListName)[0];

    // Remove the company from the old list
    let companyIndexInOldList: number = oldList.companies.findIndex((c: Company) => c.id === companyId);
    if (companyIndexInOldList !== -1) {
      oldList.companies.splice(companyIndexInOldList, 1);
    }

    //new list was created and not exists in this.companyLists
    if (newList == undefined) {
      let tmpC: CompanyFavorite = new CompanyFavorite();
      tmpC.name = newListName;
      tmpC.companyIds = [];
      tmpC.companies = [];
      this.companyLists.push(tmpC);
      newList = tmpC;
    }

    // Add the company to the new list if its not already there
    companyIndexInOldList = newList.companies.findIndex((c: Company) => c.id === companyId);
    if (companyIndexInOldList == -1) {
      newList.companies.push(company);
    }
  }

  onFocusedRowChanging(e: any) {

    let company: Company = e.data;

    //don't request a company which is already REQUESTED
    if (this.order.subOrders != undefined) {
      this.order.subOrders.filter((sO: Order) => sO.orderStatus == "ACCEPTED" || sO.orderStatus == "REQUESTED" || sO.orderStatus == "CANCELED").forEach((sO: Order) => {
        if (company.id == sO!.companyId!.id) {

          this.snackbarService.openSnackBar('Firma ist schon angefragt', 'Ok', 'red-snackbar');
          return;
        }
      })
    }

    this.submitData.emit(company);
  }

  selectList(item: any) {


    if (item.companies && item.companies.length > 0) {

      let companies: Company[] = [];
      companies.push(...item.companies);
      //don't request a company which is already REQUESTED
      if (this.order.subOrders != undefined) {

        this.order.subOrders.filter((sO: Order) => sO.orderStatus == "ACCEPTED" || sO.orderStatus == "REQUESTED" || sO.orderStatus == "CANCELED").forEach((sO: Order) => {

          // Remove the already requested company from the companies list
          let companyIndexInOldList: number = companies.findIndex((c: Company) => c.id === sO!.companyId!.id);
          if (companyIndexInOldList !== -1) {
            companies.splice(companyIndexInOldList, 1);
          }

          //companies = item.companies.filter((company: Company) => company.id != sO!.companyId!.id);


        })


      }
      this.submitListData.emit(companies);
    }
  }

  async selectListForCompanies() {
    const selectedItems = await this.dataGrid.instance.getSelectedRowsData();

    if (selectedItems.length == 0) {
      this.snackbarService.openSnackBar('Keine Auswahl getroffen', 'Ok', 'red-snackbar');
      return;
    }
    let listCompanies: any = [];

    selectedItems.forEach((company: any) => {
      listCompanies.push(company.id);
    });

    const dialogRef = this.openDialog(listCompanies);
    dialogRef.afterClosed().subscribe((result: any) => {

      if (result != 'canceled' && result != undefined) {
        this.dataSending = true;
        selectedItems.forEach((company: any) => {
          //this.moveCompanyToList(company.id, company.favorite, result);
          company.favorite = result;
        });
        this.getCompanyLists().subscribe((data: any) => {
          this.snackbarService.openSnackBar("In Liste '" + result + "' übernommen", 'Ok', 'green-snackbar');
          this.dataSending = false;
        });

      }
    });
  }

  deleteCompanyFromList(companyId: any, listId: string) {
    let company: CompanyFavorite = this.dataSource.filter((c: Company) => c.id == companyId)[0];
    const dialogRef = this.openDeleteDialog(company.name, 'Aus Liste entfernen?', 'Firme von der Liste nehmen?');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted' && result != undefined) {
        this.removeCompanyFromList(listId, companyId);
      }
    });
  }

  deleteList(item: any) {
    const dialogRef = this.openDeleteDialog(item.name, 'Endgültig löschen?', 'Liste wirklick löschen?');
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted' && result != undefined) {
        this.deleteCompanyList(item.id);
      }
    });
  }

  openDeleteDialog(id: string, title: string, text: string): any {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: title,
      text: text,
    };
    return this.dialog.open(DialogDeleteComponent,
      dialogConfig);
  }


  deleteCompanyList(id: string) {

    this.dataSending = true;
    of(0).pipe(
      mergeMap(() => this.companyService.getOwnCompany()),
      mergeMap((c: Company) => this.companyService.deleteCompanyFavoriteList(id, c.id)),
      mergeMap(() => this.outsourceService.getOutsourceCompanies()),
      map((data: any) => {
        this.dataSource = data;
      }),
    ).subscribe(() => {
      let companyIndexInOldList: number = this.companyLists.findIndex((c: Company) => c.id === id);
      if (companyIndexInOldList !== -1) {
        this.companyLists.splice(companyIndexInOldList, 1);
      }
      this.dataSending = false;
      this.snackbarService.openSnackBar('Liste gelöscht', 'Ok', 'green-snackbar');
    });
  }


  removeCompanyFromList(id: string, companyId: string) {
    let list: CompanyFavorite = this.companyLists.filter((c: CompanyFavorite) => c.id == id)[0];
    if (list.companyIds) {
      // Remove the company from the old list
      let companyIndexInOldList: number = list.companies.findIndex((c: Company) => c.id === companyId);
      if (companyIndexInOldList !== -1) {
        list.companies.splice(companyIndexInOldList, 1);
      }

      // Remove the companyId from the old list
      companyIndexInOldList = list.companyIds.findIndex((c: string) => c === companyId);
      if (companyIndexInOldList !== -1) {
        list.companyIds.splice(companyIndexInOldList, 1);
      }

      let d: any = {
        companyIds: list.companyIds
      }

      of(0).pipe(
        mergeMap(() => this.companyService.getOwnCompany()),
        mergeMap((c: Company) => this.companyService.patchCompanyFavoriteList(list.id, c.id, d)),
        mergeMap(() => this.outsourceService.getOutsourceCompanies()),
        map((data: any) => {
          this.dataSource = data;
        }),
      ).subscribe((data: any) => {
        this.snackbarService.openSnackBar('Firma aus Liste entfernt', 'Ok', 'green-snackbar');
      });
    }
  }


}
