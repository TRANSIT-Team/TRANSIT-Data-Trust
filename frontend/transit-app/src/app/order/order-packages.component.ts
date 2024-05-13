import {ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {Package} from "../packages/shared/package";
import {Order} from "./shared/order";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {DirectionService} from "../map/shared/direction.service";
import {SnackbarService} from "../_core/snackbar.service";
import {DialogAddressComponent} from "../dialog/dialog-address.component";
import {DialogPackageComponent} from "../dialog/dialog-package.component";
import {MatTable} from "@angular/material/table";
import {FunctionsService} from "../_core/functions.service";

@Component({
  selector: 'app-order-packages',
  templateUrl: './order-packages.component.html',
  styleUrls: ['./order-packages.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class OrderPackagesComponent implements OnInit {
  displayedColumnsPackages: string[] = ['id', 'packageClass', 'packagePrice', 'deepCm', 'widthCm', 'heightCm', 'weightKg', 'volume', 'edit'];
  packages: Package[] = [];
  columnsToDisplayWithExpand = [...this.displayedColumnsPackages.filter((c) => c != 'edit'), 'expand'];
  expandedElement: Package | null;
  currentOpenedPackage: any;
  @ViewChild(MatTable) table: MatTable<any>;
  dataSource: any = [];
  @Input() order: Order;

  constructor(public dialog: MatDialog, private directionService: DirectionService, public snackbarService: SnackbarService, private cdr: ChangeDetectorRef, private functionsService: FunctionsService) {
  }

  ngOnInit(): void {
    this.order.packageItems = this.order.packageItems!.sort((a: any, b: any) => a.id - b.id);
  }

  updateDataSource() {
    this.dataSource = this.order;
    // Manually trigger change detection
    this.cdr.detectChanges();
  }

  openDialog(p: any) {
    let dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "60vw";
    dialogConfig.height = "75vh";
    dialogConfig.data = {
      packageItem: p,
      order: this.order
    };

    const dialogRef = this.dialog.open(DialogPackageComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result != 'canceled') {
        if (p != 'newPackage') {
          let packageToUpdate: Package | undefined = this.order.packageItems!.find((pa: Package) => pa.id === result.id);
          if (packageToUpdate) {
            // Update properties of the found element
            packageToUpdate.weightKg = result.weightKg;
            packageToUpdate.heightCm = result.heightCm;
            packageToUpdate.widthCm = result.widthCm;
            packageToUpdate.deepCm = result.deepCm;
            packageToUpdate.packagePackageProperties = result.packagePackageProperties;
            packageToUpdate.packageClass = result.packageClass;
            packageToUpdate.packagePrice = result.packagePrice;
            // Update other properties as needed
          }
        }
      }
      this.cdr.detectChanges();
      // Refresh the MatTable
      this.table.renderRows();
    });
  }

  expandCollapse(expandedElement: any, element: any) {
    //console.log(expandedElement, element);
    return (expandedElement = expandedElement === element ? null : element);
  }

  calculateVolumeWeight(element: any): number {
    if (element && element.heightCm && element.widthCm && element.deepCm) {
      const volumeWeight = (element.heightCm * element.widthCm * element.deepCm) / 6000;
      return +volumeWeight.toFixed(6);
    } else {
      return 0;
    }
  }


}
