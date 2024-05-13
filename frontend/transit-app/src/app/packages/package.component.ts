import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {PackageService} from "./shared/packages.service";
import {Package, PackageClass, PackagePackageProperty, PackageProperty} from "./shared/package";
import {Location, ViewportScroller} from '@angular/common';
import {MatSnackBar} from '@angular/material/snack-bar';

import {MatProgressBar} from "@angular/material/progress-bar";
import {iif, map, mergeMap, Observable, of, pairwise, startWith} from "rxjs";
import {MatSort, Sort} from "@angular/material/sort";
import {HateoasResourceService, PagedResourceCollection, ResourceCollection} from "@lagoshny/ngx-hateoas-client";
import {MatTableDataSource} from "@angular/material/table";

import {
  AbstractControl,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {Order} from '../order/shared/order';
import {AppResource, UniqueResource} from 'src/app/_core/AbstractResource';
import {Company} from 'src/app/company/shared/company';
import {SnackbarService} from 'src/app/_core/snackbar.service';
import {Globals} from 'src/app/_core/globals';
import {OrderService} from '../order/shared/order.service';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DialogContactpersonComponent} from "../dialog/dialog-contactperson.component";
import {DialogPackagePropertiesComponent} from "../dialog/dialog-package-properties.component";


@Component({
  selector: 'app-package',
  templateUrl: './package.component.html',
  styleUrls: ['./package.component.css']
})
export class PackageComponent implements OnInit {

  @Input() p: any;
  @Input() isDialog: boolean = false;
  @Input() order: Order;


  packageId: string
  editPackage: Package;
  newPackage: Package;
  newPackageClass: PackageClass;
  newPackagePackageProperties: PackagePackageProperty[];

  selectedPackageProperties: PackagePropertySelection[];

  packageClasses: PackageClass[];
  packagePropertiesForSelection: PackageProperty[];
  forbiddenPackagePropertiesForSelection: PackageProperty[] = [];
  selectedPackageClass = '';
  tempSelectedPackageProperty = '';
  currentOrder: Order;
  currentOrderId: string = "";
  sendingData = false;

  createPackageForm: UntypedFormGroup = this.fb.group({
    heightCm: [0, [Validators.required, Validators.min(0.1), Validators.max(99999)]],
    widthCm: [0, [Validators.required, Validators.min(0.1), Validators.max(99999)]],
    weightKg: [0, [Validators.required, Validators.min(0.1), Validators.max(99999)]],
    deepCm: [0, [Validators.required, Validators.min(0.1), Validators.max(99999)]],
    packageClass: ['', Validators.required],
    packagePrice: [''],
    packageProperties: this.fb.array([])
  });
  @Output() crudPackage = new EventEmitter<any>();

  static validCustom(control: UntypedFormControl): { [key: string]: any } {

    if (control.value === "") {
      return {name: {valid: false}}
    }
    return {name: {valid: true}};
  }

  constructor(
    private route: ActivatedRoute, public dialog: MatDialog,
    private packageService: PackageService,
    private orderService: OrderService,
    private location: Location,
    private _snackBar: MatSnackBar,
    private _router: Router,
    private resourceHateoasService: HateoasResourceService,
    private fb: UntypedFormBuilder,
    public snackbar: SnackbarService,
    private viewportScroller: ViewportScroller,
    private globals: Globals
  ) {
  }

  @Output() hideProgressBar = new EventEmitter<boolean>();

  ngOnInit(): void {

    this.viewportScroller.scrollToAnchor('main');
    this.hideProgressBar.emit(false);
    if (this.isDialog) {
      if (this.p != "newPackage") {
        if (this.p != undefined) {
          this.packageId = this.p.id;
          this.getDialogPackage(true);
        }
      } else {
        this.getDialogPackage(false);
      }
    } else {
      this.getOrder((this.route.snapshot.paramMap.get('orderId')!.toString()));
    }
  }

  getDialogPackage(loadPackage = false) {
    of(0).pipe(
      mergeMap(() => this.packageService.getGlobalPackageClasses()),
      map((collection: ResourceCollection<PackageClass>) => {
        this.packageClasses = collection.resources;
      }),
      mergeMap(() => this.packageService.getGlobalPackageProperties()),
      map((collection: ResourceCollection<PackageProperty>) => {
        this.packagePropertiesForSelection = collection.resources;
      })).subscribe(() => {
      if (loadPackage) {
        this.initializePackage(this.p);
      }
    });
  }

  getOrder(id: string) {
    this.resourceHateoasService.getResource(Order, id,
    ).pipe(
      map((order: Order) => {

        this.currentOrder = order;
        this.currentOrderId = order.id;
      }),
      mergeMap(() => this.packageService.getGlobalPackageClasses()),
      map((collection: ResourceCollection<PackageClass>) => {
        this.packageClasses = collection.resources;
      }),
      mergeMap(() => this.packageService.getGlobalPackageProperties()),
      map((collection: ResourceCollection<PackageProperty>) => {
        this.packagePropertiesForSelection = collection.resources;
      })
    ).subscribe(() => {
      this.packageId = this.route.snapshot.paramMap.get('packageId')!;

      if (this.packageId) {
        this.getPackage(this.packageId)
      }
    });


  }

  getPackage(id: string): void {


    this.resourceHateoasService.getResource(Package, id, {
      params: {
        filter: 'deleted==false'
      },
      sort: {
        packagepackageProperties: 'ASC'
      },
    })
      .subscribe((pack: Package) => {
        this.initializePackage(pack);

        // console.log(this.packageProperties());
      });
  }

  preventNonNumericalInput(event: KeyboardEvent): void {
    const charCode = event.which || event.keyCode;
    const charStr = String.fromCharCode(charCode);

    // Allow digits, commas, periods, backspace, and an empty input
    if (!charStr.match(/^[0-9\.,]*$/) && charCode !== 8) {
      event.preventDefault();
    }
  }


  initializePackage(pack: any) {
    this.editPackage = pack;

    this.createPackageForm = this.fb.group({
      heightCm: [this.editPackage.heightCm, [Validators.required, Validators.min(0.1), Validators.max(99999), Validators.pattern('[0-9]*(\\.[0-9]+)?')]],
      widthCm: [this.editPackage.widthCm, [Validators.required, Validators.min(0.1), Validators.max(99999), Validators.pattern('[0-9]*(\\.[0-9]+)?')]],
      weightKg: [this.editPackage.weightKg, [Validators.required, Validators.min(0.1), Validators.max(99999), Validators.pattern('[0-9]*(\\.[0-9]+)?')]],
      deepCm: [this.editPackage.deepCm, [Validators.required, Validators.min(0.1), Validators.max(99999), Validators.pattern('[0-9]*(\\.[0-9]+)?')]],
      packageClass: [this.editPackage.packageClass?.id, Validators.required],
      packagePrice: [this.editPackage.packagePrice, [Validators.pattern('[0-9]+([.,][0-9]+)?')]],
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

  isPropertyDisabled(i: number): boolean {
    let disabled = false;
    console.log(this.selectedPackageProperties[i].disabled);
    if (this.selectedPackageProperties[i].disabled) {
      disabled = true;
    }
    return disabled;
  }

  packageProperties(): UntypedFormArray {
    return this.createPackageForm.get('packageProperties') as UntypedFormArray;
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
    if (this.createPackageForm.valid) {
      if (this.packageId) {
        this.updatePackage();
      } else {
        if (this.isDialog) {
          this.createPackageDialog();
        } else {
          this.createPackage();
        }

      }
    }
  }

  updatePackage() {

    this.sendingData = true;
    //packagePackageProperties must be udated over them endppoint
    this.setPackageForCrud();
    // put package to api
    let tmpP: any;

    tmpP = {
      id: this.newPackage.id,
      deepCm: this.newPackage.deepCm,
      heightCm: this.newPackage.heightCm,
      weightKg: this.newPackage.weightKg,
      widthCm: this.newPackage.widthCm,
      packageClass: {id: this.newPackage.packageClass!.id},
      packagePrice: this.newPackage.packagePrice
    }

    // packageProperties - start ------------------------------------------------------
    let newPackagePackageProperties: any = [];
    let editPackagePackageProperties: any = [];
    this.selectedPackageProperties = [];
    this.selectedPackageProperties = this.createPackageForm.controls['packageProperties'].value;


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

    this.newPackage.packagePackageProperties = [];
    this.packageService.patchPackage(tmpP).pipe(
      map((updatedProduct: Package) => {
        this.newPackage = updatedProduct;
      }),
      mergeMap(() => iif(() => newPackagePackageProperties.length > 0, this.packageService.postPackagePropertiesForPackageId(this.editPackage.id, newPackagePackageProperties), of(null))),
      map((response: any) => {
        if (response != undefined) {
          response.forEach((r: any) => {
            this.newPackage.packagePackageProperties!.push(r);
          });
        }
      }),
      mergeMap(() => iif(() => editPackagePackageProperties.length > 0, this.packageService.patchPackagePropertiesForPackageId(this.editPackage.id, editPackagePackageProperties), of(null))),
      map((response: any) => {
        if (response != undefined) {
          response.forEach((r: any) => {
            let updateItem: any = this.newPackage.packagePackageProperties?.filter((p) => p.id == r.id)[0];
            updateItem.value = r.value;
          });
        }

        return response;

      }),)
      .subscribe((updatedProduct: any) => {

        console.log(updatedProduct);
        console.log(this.newPackage);
        this.snackbar.openSnackBar('Sendungs ' + this.newPackage.id + ' aktualisiert', 'Ok', 'green-snackbar');
        if (this.isDialog) {
          this.crudPackage.emit(this.newPackage)
        } else {
          this.globals.scrollTo = "packages";
          this.globals.scrollToSingle = "packages";
          this._router.navigate(['/order/' + this.order.id], {relativeTo: this.route});
          this.sendingData = false;
        }
      });
  }

  createPackageDialog() {
    this.sendingData = true;
    this.globals.dataLoading = true;
    this.setPackageForCrud();
    let updateOrder: Order = new Order();
    let newPack: Package = new Package();
    updateOrder.id = this.order.id;
    updateOrder.packageItemIds = [];
    this.order.packageItemIds?.forEach(((item, index) => {
      let oldPackage = new AppResource();
      oldPackage.id = item.id;
      updateOrder.packageItemIds?.push(oldPackage);
    }));

    this.packageService.createPackage(this.newPackage).pipe(
      map((newPackage: any) => {
          newPack = newPackage;
          const parts = newPack.id.split('-');
          newPack.shortId = '#' + parts[0];
          let tmpPackage = new AppResource();
          tmpPackage.id = newPackage.id;
          updateOrder.packageItemIds?.push(tmpPackage);
          this.order.packageItemIds?.push(tmpPackage);
          this.order.packageItems?.push(newPackage);
        }
      ),
      mergeMap(() => this.orderService.patchOrder(updateOrder)),
    ).subscribe((newOrder: any) => {
      this.snackbar.openSnackBar('Sendung angelegt', 'Ok', 'green-snackbar');
      this.globals.dataLoading = false;
      this.sendingData = false;
      this.crudPackage.emit(newPack);
    });
  }

  createPackage() {
    this.sendingData = true;
    // version post
    this.setPackageForCrud();

    let orderId = this.route.snapshot.paramMap.get('orderId')!;
    let newPackageId;
    let updateOrder: Order = new Order();

    let newPack: Package;
    updateOrder.packageItemIds = [];
    // post package to api
    this.packageService.createPackage(this.newPackage).pipe(
      map((newPackage: any) => {
        newPackageId = newPackage.id;
        newPack = newPackage;

        const parts = newPack.id.split('-');
        newPack.shortId = '#' + parts[0];


        this.currentOrder.packageItemIds?.forEach(((item, index) => {
          let oldPackage = new AppResource();
          oldPackage.id = item.id;
          updateOrder.packageItemIds?.push(oldPackage);
        }));

        let packageItem: AppResource = new AppResource();
        packageItem.id = newPackageId;
        updateOrder.packageItemIds?.push(packageItem);

        // console.log(updateOrder);
        return newPackageId;
      }),
      mergeMap(() => this.resourceHateoasService.patchResourceById(Order, orderId, {
        body: updateOrder
      })),
    ).subscribe((newOrder: any) => {
      this.snackbar.openSnackBar('Sendung angelegt', 'Ok', 'green-snackbar');
      if (this.isDialog) {
        this.crudPackage.emit(newPack)
      } else {
        // this.globals.scrollTo = "packages";
        //  this.globals.scrollToSingle = "packages";
        this.globals.dataLoading = false;
        this.sendingData = false;
        this.cancel();
      }
    });
  }

  setPackageForCrud() {
    this.newPackage = new Package();
    if (this.editPackage) {
      this.newPackage.id = this.editPackage.id;
    }


    this.newPackage.heightCm = this.createPackageForm.controls["heightCm"].value;
    this.newPackage.weightKg = this.createPackageForm.controls["weightKg"].value;
    this.newPackage.widthCm = this.createPackageForm.controls["widthCm"].value;
    this.newPackage.deepCm = this.createPackageForm.controls["deepCm"].value;
    this.newPackage.packagePrice = this.createPackageForm.controls["packagePrice"].value;

    // packageClass
    this.newPackageClass = new PackageClass();
    this.newPackageClass.id = this.createPackageForm.controls["packageClass"].value;
    // this.newPackageClass.name = "";
    this.newPackage.packageClass = this.newPackageClass;

    // packageProperties - start ------------------------------------------------------
    this.newPackagePackageProperties = [];
    this.selectedPackageProperties = [];
    this.selectedPackageProperties = this.createPackageForm.controls['packageProperties'].value;

    this.selectedPackageProperties.forEach(ls => {
      let selectedVal: PackageProperty = new PackageProperty();
      let newVal: PackagePackageProperty = new PackagePackageProperty();
      selectedVal = this.packagePropertiesForSelection.filter((p) => p.id === ls.key)[0];
      if (selectedVal != undefined) {
        newVal.key = selectedVal.key;
        newVal.value = ls.value;
        newVal.type = selectedVal.type;
        this.newPackagePackageProperties.push(newVal);
      }
    });

    // packageProperties - end --------------------------------------------------------
    this.newPackage.packagePackageProperties = this.newPackagePackageProperties;
  }

  goBack()
    :
    void {
    this.location.back();
  }

  cancel() {
    // this._router.navigate(['../'], {relativeTo: this.route});

    this._router.navigate(['/orders/order/' + this.route.snapshot.paramMap.get('orderId')!.toString()], {relativeTo: this.route});
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
        this.getDialogPackage();
      }
    );
  }


}


export function

customValidator(forbiddenNumber: number) {

  return (control: AbstractControl): ValidationErrors | null => {

    const forbidden = false;
    if ((control.value) == forbiddenNumber) {
      return {forbiddenNumber: {value: control.value}};
    }

    return null;
  }
}

// @ts-ignore
export class PackagePropertySelection
  extends PackageProperty {
  disabled?: boolean;
}
