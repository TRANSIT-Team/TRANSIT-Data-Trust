import {Component, OnInit, ViewChild} from '@angular/core';
import { AbstractControl, UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { startWith, tap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HateoasResourceService, ResourceCollection, Sort } from '@lagoshny/ngx-hateoas-client';
import {MatSort} from "@angular/material/sort";
import {Sort as MSort} from "@angular/material/sort";

import {MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DialogDeleteComponent } from '../dialog/dialog-delete.component';

export interface PeriodicElement {
  name: string;
  id: string;
  weight: number;
  symbol: string;
  dropdown: string;
  dateExample: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {id: '1', name: 'Hydrogen', weight: 1.0079, symbol: 'H',dropdown:'1',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '2', name: 'Helium', weight: 4.0026, symbol: 'He',dropdown:'3',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '3', name: 'Lithium', weight: 6.941, symbol: 'Li',dropdown:'4',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '4', name: 'Beryllium', weight: 9.0122, symbol: 'Be',dropdown:'1',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '5', name: 'Boron', weight: 10.811, symbol: 'B',dropdown:'1',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '6', name: 'Carbon', weight: 12.0107, symbol: 'C',dropdown:'1',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '7', name: 'Nitrogen', weight: 14.0067, symbol: 'N',dropdown:'3',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '8', name: 'Oxygen', weight: 15.9994, symbol: 'O',dropdown:'1',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '9', name: 'Fluorine', weight: 18.9984, symbol: 'F',dropdown:'3',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '10', name: 'Neon', weight: 20.1797, symbol: 'Ne',dropdown:'2',dateExample:'2022-07-18T12:12:59.749Z'},
  {id: '11', name: 'Neon', weight: 20.1797, symbol: 'Ne',dropdown:'1',dateExample:'2022-07-18T12:12:59.749Z'},
];

@Component({
  selector: 'app-table-template',
  templateUrl: './table-template.component.html',
  styleUrls: ['./table-template.component.css']
})
export class TableTemplateComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort;
  displayedColumns: string[] = ['id','name', 'weight', 'symbol','dropdown', 'dateExample','action'];
  dataSource = new MatTableDataSource<any>();
  isLoading = true;
  pageNumber: number = 1;
  VOForm: UntypedFormGroup;
  isEditableNew: boolean = true;

  constructor(
    private fb: UntypedFormBuilder,
    private _formBuilder: UntypedFormBuilder,
    private resourceHateoasService: HateoasResourceService,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog){}

  ngOnInit(): void {
    this.loadTable();
  }

  loadTable(){
    this.VOForm = this._formBuilder.group({
      VORows: this._formBuilder.array([])
    });

    this.VOForm = this.fb.group({
      VORows: this.fb.array(ELEMENT_DATA.map(val => this.fb.group({
          id: new UntypedFormControl(val.id),
          name: new UntypedFormControl(val.name),
          weight: new UntypedFormControl(val.weight),
          symbol: new UntypedFormControl(val.symbol),
          dropdown: new UntypedFormControl(val.dropdown),
          dateExample: new UntypedFormControl(val.dateExample),
          action: new UntypedFormControl('existingRecord'),
          isEditable: new UntypedFormControl(true),
          isNewRow: new UntypedFormControl(false),
        })
      )) //end of fb array
    }); // end of form group cretation

    console.log(new Date().toISOString());
    this.isLoading = false;
    this.dataSource = new MatTableDataSource((this.VOForm.get('VORows') as UntypedFormArray).controls);
    this.dataSource.paginator = this.paginator;

    this.dataSource.sortingDataAccessor = (data: AbstractControl, sortHeaderId: string) => {
      const value: any = data.value[sortHeaderId];
      return typeof value === 'string' ? value.toLowerCase() : value;
    };

    const filterPredicate = this.dataSource.filterPredicate;
    this.dataSource.filterPredicate = (data: AbstractControl, filter) => {
      return filterPredicate.call(this.dataSource, data.value, filter);
    }
  }

  @ViewChild(MatPaginator) paginator: MatPaginator;

  goToPage() {
    this.paginator.pageIndex = this.pageNumber - 1;
    this.paginator.page.next({
      pageIndex: this.paginator.pageIndex,
      pageSize: this.paginator.pageSize,
      length: this.paginator.length
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.paginatorList = document.getElementsByClassName('mat-paginator-range-label');
    this.dataSource.sort = this.sort;
    this.onPaginateChange(this.paginator, this.paginatorList);
    this.paginator.page.subscribe(() => { // this is page change event
      this.onPaginateChange(this.paginator, this.paginatorList);
    });
  }

  applyFilter(event: Event) {
    //  debugger;
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  paginatorList: HTMLCollectionOf<Element>;
  idx: number;
  onPaginateChange(paginator: MatPaginator, list: HTMLCollectionOf<Element>) {
    setTimeout((idx:any) => {
      let from = (paginator.pageSize * paginator.pageIndex) + 1;

      let to = (paginator.length < paginator.pageSize * (paginator.pageIndex + 1))
        ? paginator.length
        : paginator.pageSize * (paginator.pageIndex + 1);

      let toFrom = (paginator.length == 0) ? 0 : `${from} - ${to}`;
      let pageNumber = (paginator.length == 0) ? `0 of 0` : `${paginator.pageIndex + 1} of ${paginator.getNumberOfPages()}`;
      let rows = `Page ${pageNumber} (${toFrom} of ${paginator.length})`;

      if (list.length >= 1)
        list[0].innerHTML = rows;

    }, 0, paginator.pageIndex);
  }

  initiateVOForm(): UntypedFormGroup {
    return this.fb.group({

      position: new UntypedFormControl(234),
      name: new UntypedFormControl(''),
      weight: new UntypedFormControl(''),
      symbol: new UntypedFormControl(''),
      dropdown: new UntypedFormControl(''),
      dateExample: new UntypedFormControl(''),
      action: new UntypedFormControl('newRecord'),
      isEditable: new UntypedFormControl(false),
      isNewRow: new UntypedFormControl(true),
    });
  }

  // this function will enabled the select field for editd
  editSVO(VOFormElement:any, i:any) {

    // VOFormElement.get('VORows').at(i).get('name').disabled(false)
    VOFormElement.get('VORows').at(i).get('isEditable').patchValue(false);
    console.log( VOFormElement.get('VORows').at(i).get('name').value);

    // this.isEditableNew = true;

  }

  // On click of correct button in table (after click on edit) this method will call
  saveVO(VOFormElement:any, i:any) {
    // alert('SaveVO')
    VOFormElement.get('VORows').at(i).get('isEditable').patchValue(true);
    this.openSnackbar('Aktualisiert.', "Ok");
  }

  // On click of cancel button in the table (after click on edit) this method will call and reset the previous data
  cancelSVO(VOFormElement:any, i:any) {
   // VOFormElement.get('VORows').at(i).get('isEditable').patchValue(true);
    this.loadTable();
  }

  deleteSVO(VOFormElement:any, i:any) {
    this.openDialog(VOFormElement.get('VORows').at(i).get('id').value);
  }

  openDialog(id: string) {

    // const dialogRef = this.dialog.open(DialogComponent);
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: id,
      title: 'Datensatz löschen?',
      text: 'Soll der Datensatz wirklich gelöscht werden?',
    };

    //  this.dialog.open(DialogComponent, dialogConfig);
    const dialogRef = this.dialog.open(DialogDeleteComponent,
      dialogConfig);

    // dialogRef.afterClosed().subscribe((result: any) => {
    //  console.log(`Dialog result: ${result}`);
    //});
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result == 'deleted') {
        this.deleteItem(id);
      }
    });
  }

  deleteItem(id: string){

    //   this.resourceHateoasService.deleteResourceById(PackageProperty,id)
    //  .subscribe((result: any) => {
    //   this.openSnackbar('Sendungs gelöscht.', "Ok schade");
      //  this.getGlobalPackageProperties();

    // });
  }

  openDialogFromTable() {
    if ("") {
      this.openDialog("dd");
    }
    else {
      this.openSnackbar('Kein Sendungs ausgewählt.', "Upsss ok");
    }
  }
  openSnackbar(info:string, hide:string){

    this._snackBar.open(info, hide, {
      duration: 3000,
    });
  }

}
