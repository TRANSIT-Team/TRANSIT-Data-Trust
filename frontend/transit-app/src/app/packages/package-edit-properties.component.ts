import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Package, PackageClass, PackagePackageProperty, PackageProperty} from "./shared/package";
import {PackagePropertySelection} from "./package.component";
import {Order} from "../order/shared/order";
import {iif, map, mergeMap, Observable, of} from "rxjs";
import {ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {PackageService} from "./shared/packages.service";
import {SnackbarService} from "../_core/snackbar.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogPackagePropertiesComponent} from "../dialog/dialog-package-properties.component";

@Component({
  selector: 'app-package-edit-properties',
  templateUrl: './package-edit-properties.component.html',
  styleUrls: ['./package-edit-properties.component.css']
})
export class PackageEditPropertiesComponent implements OnInit {
  @Input() p: any;
  @Input() isDialog: boolean = false;
  @Input() order: Order;
  @Output() crudPackage = new EventEmitter<any>();
  editPackage: Package;
  newPackagePackageProperties: PackagePackageProperty[];
  selectedPackageProperties: PackagePropertySelection[];
  packageClasses: PackageClass[];
  packagePropertiesForSelection: PackageProperty[] =[];
  forbiddenPackagePropertiesForSelection: PackageProperty[] = [];
  selectedPackageClass = '';
  tempSelectedPackageProperty = '';
  sendingData = false;
  editPackageForm: UntypedFormGroup = this.fb.group({
    packageProperties: this.fb.array([])
  });

  constructor(private fb: UntypedFormBuilder,public dialog: MatDialog,
              private packageService: PackageService,
              private snackbarService: SnackbarService) {
  }

  ngOnInit(): void {
    this.getDialogPackage(true);
  }

  getDialogPackage(loadPackage = false) {
     this.getGlobalProperties().subscribe(() => {
      if (loadPackage) {
        this.initializePackage(this.p);
      }
    });
  }


  getGlobalProperties():Observable<any>{
   return  of(0).pipe(mergeMap(() => this.packageService.getGlobalPackageProperties()),
      map((collection: ResourceCollection<PackageProperty>) => {
        this.packagePropertiesForSelection = collection.resources;
      }));
  }

  initializePackage(pack: any) {
    this.editPackage = pack;

    this.editPackageForm = this.fb.group({
      packageProperties: this.fb.array([])
    });

    this.selectedPackageProperties = [];

    this.editPackage.packagePackageProperties?.forEach(ls => {

      if (!ls.deleted) {
        //fill select with options
        let selectedVal: PackagePropertySelection = new PackagePropertySelection();
        selectedVal.id = ls.id;
        selectedVal.value = ls.value;
        selectedVal.defaultValue = ls.value;
        selectedVal.type = ls.type;
        selectedVal.disabled = true;
        this.selectedPackageProperties.push(selectedVal);

        // get packageProperty.Id from packagePackageProperty.key
        let packProp: PackagePropertySelection = this.packagePropertiesForSelection.filter((p) => p.key === ls.key)[0];
        if (packProp == undefined) {
          packProp = ls;
          this.packagePropertiesForSelection.push(ls);
        }
        // set this as fordbiddel for other selects
        this.forbiddenPackagePropertiesForSelection.push(packProp);
        // add the for the start list of selects
        this.packageProperties().push(this.editPackageProperty(packProp.id, ls.value));
      }
    });


  }

  packageProperties(): UntypedFormArray {
    return this.editPackageForm.get('packageProperties') as UntypedFormArray;
  }

  newPackageProperty(k?: string, val?: string): UntypedFormGroup {
    return this.fb.group({
      key: '',
      value: '',
      disabled: false
    });
  }

  editPackageProperty(k?: string, val?: string): UntypedFormGroup {
    return this.fb.group({
      key: k,
      value: val,
      disabled: true
    });
  }

  addPackageProperty() {
    this.packageProperties().push(this.newPackageProperty());
  }

  removePackageProperty(empIndex: number, selectedItem: string) {
    this.forbiddenPackagePropertiesForSelection.forEach(ls => {
      if (selectedItem == ls.id) {
        delete this.forbiddenPackagePropertiesForSelection[this.forbiddenPackagePropertiesForSelection.indexOf(ls, 0)];
      }
    });
    this.packageProperties().removeAt(empIndex);
  }

  public onMatSelectOpen(selectedItem: string): void {
    this.tempSelectedPackageProperty = selectedItem;
  }

  changeInputValue(index: number, newValue: string) {
    let control = this.packageProperties().at(index).get('value');
    if (control != undefined) {
      control.setValue(newValue);
    }
  }

  changePackagePropertiesForSelection(selectedItem: any, index: any) {
    // on change selection-> check if another item is selected and removed the old one from the list packagePropertiesForSelection
    if (this.tempSelectedPackageProperty != selectedItem) {
      this.packagePropertiesForSelection.forEach(ls => {
        if (this.tempSelectedPackageProperty == ls.id) {
          let selectedVal: PackageProperty = new PackageProperty();
          delete this.forbiddenPackagePropertiesForSelection[this.forbiddenPackagePropertiesForSelection.indexOf(ls, 0)];
        }
        let deVa: any = this.packagePropertiesForSelection.filter((p) => p.id == selectedItem)[0];
        if (deVa != undefined) {
          this.changeInputValue(index, deVa.defaultValue!.toString());
        }
        return;
      });
    }

    // adding the currently selected item to the list packagePropertiesForSelection
    this.packagePropertiesForSelection.forEach(ls => {
      if (selectedItem == ls.id) {
        let selectedVal: PackageProperty = new PackageProperty();
        selectedVal = ls;
        this.forbiddenPackagePropertiesForSelection.push(selectedVal);
        return;
      }
    });
  }

  isPropertySelected(selectedItem: string) {
    let forbidden = false;
    this.forbiddenPackagePropertiesForSelection.forEach(ls => {
      if (selectedItem == ls.id) {
        forbidden = true;
      }
    });
    return forbidden;
  }


  onSubmit() {
    if (this.editPackageForm.valid) {

      this.updatePackage();
    }
  }


  updatePackage() {

    this.sendingData = true;
    //packagePackageProperties must be udated over them endppoint
    let newPackage = new Package();
    if (this.editPackage) {
      newPackage.id = this.editPackage.id;
    }
    if (this.editPackage.packagePackageProperties == undefined) {
      this.editPackage.packagePackageProperties = [];
    }

    // packageProperties - start ------------------------------------------------------
    let newPackagePackageProperties: any = [];
    let editPackagePackageProperties: any = [];
    this.selectedPackageProperties = [];
    this.selectedPackageProperties = this.editPackageForm.controls['packageProperties'].value;

    this.selectedPackageProperties.forEach(selectedProp => {
      let newVal: PackagePackageProperty = new PackagePackageProperty();

      let getItemSelection: any = this.packagePropertiesForSelection.filter((p) => p.id === selectedProp.key)[0];

      let existingItem: any = this.editPackage.packagePackageProperties?.filter((p) => p.key === getItemSelection.key)[0];


      //if exist then update else create
      if (existingItem) {
        newVal.id = existingItem.id;
        newVal.key = existingItem.key;
        newVal.value = selectedProp.value;
        newVal.type = selectedProp.type;
        editPackagePackageProperties.push(newVal);
      } else {
        let selectedVal: PackageProperty = new PackageProperty();
        selectedVal = this.packagePropertiesForSelection.filter((p) => p.id === selectedProp.key)[0];
        if (selectedVal != undefined) {
          newVal.id = selectedVal.id;
          newVal.key = selectedVal.key;
          newVal.value = selectedProp.value;
          newVal.type = selectedVal.type;
          newPackagePackageProperties.push(newVal);
        }
      }


    });

    // packageProperties - end --------------------------------------------------------
    //newPackage.packagePackageProperties = this.newPackagePackageProperties;
    // put package to api

    of(0).pipe(
      mergeMap(() => iif(() => newPackagePackageProperties.length > 0, this.packageService.postPackagePropertiesForPackageId(this.editPackage.id, newPackagePackageProperties), of(null))),
      map((response: any) => {
        if (response != undefined) {
          response.forEach((r: any) => {
            this.editPackage.packagePackageProperties!.push(r);
          });
        }
      }),
      mergeMap(() => iif(() => editPackagePackageProperties.length > 0, this.packageService.patchPackagePropertiesForPackageId(this.editPackage.id, editPackagePackageProperties), of(null))),
      map((response: any) => {
        if (response != undefined) {
          response.forEach((r: any) => {
            let updateItem: any = this.editPackage.packagePackageProperties?.filter((p) => p.id == r.id)[0];
            updateItem.value = r.value;
          });
        }
      }),
    ).subscribe(() => {
      this.snackbarService.openSnackBar('Sendungs ' + this.editPackage.id + ' aktualisiert', 'Ok', 'green-snackbar');
      this.crudPackage.emit(this.editPackage)
    });

  }


  packagePropertyIsAlreadyStores(p: any): boolean {
    if (this.p != undefined) {
      if (this.p.packagePackageProperties != undefined) {
        if (this.p.packagePackageProperties.length > 0) {
          let deVa: any = this.packagePropertiesForSelection.filter((pP) => pP.id == p.controls.key.value)[0];
          if (deVa != undefined) {
            let a: any = this.p.packagePackageProperties.filter((pP: any) => pP.key == deVa.key)[0];
            if (a != undefined) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }


  opePackagePropertiesDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "80vw";
    //dialogConfig.height = "80vh";
    dialogConfig.data = {};

    const dialogRef = this.dialog.open(DialogPackagePropertiesComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
        this.getGlobalProperties().subscribe();
      }
    );
  }

}
