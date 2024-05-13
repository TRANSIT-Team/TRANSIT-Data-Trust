import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Order} from "./shared/order";
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {OrderService} from "./shared/order.service";
import {SnackbarService} from "../_core/snackbar.service";
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import {DxDateBoxModule} from 'devextreme-angular';
import {iif, map, mergeMap, of} from "rxjs";


@Component({
  selector: 'app-order-dates-info',
  templateUrl: './order-dates-info.component.html',
  styleUrls: ['./order-dates-info.component.css'],

})
export class OrderDatesInfoComponent implements OnInit {
  @Input() order: Order;
  @Input() type: string;
  @Input() labelText: string;
  @Output() refreshOrder = new EventEmitter<any>();
  loading: boolean = false;
  showFinish: boolean = false;

  d: Date = new Date();
  aWeekAgo: Date = new Date(this.d.setDate(this.d.getDate() - 7));


  minDateArrive: Date = this.aWeekAgo;
  minDateLeave: Date = this.aWeekAgo;
  now: Date = new Date(new Date().getTime() + 1);

  min: Date = new Date(1900, 0, 1);


  maxDate: Date = new Date((new Date().getTime()));
  editForm: UntypedFormGroup = this.fb.group({
    actionTimestampArrive: [new Date(), [Validators.required]],
    actionTimestampLeave: [new Date(), [Validators.required]],
    actionPerson: ['', [Validators.required]],
  });
  actionTimestampLeave: any = undefined;
  actionTimestampArrive: any = undefined;
  isMyOrder: boolean = true;
  localCompleteDateArrive: Date;
  localCompleteDateLeave: Date;
  typeDescriptionArrive: string = "";
  typeDescriptionLeave: string = "";

  constructor(private orderService: OrderService, private snackbarService: SnackbarService, private fb: UntypedFormBuilder) {

  }

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm() {

    let actionPerson: any = "";
    let timeStampLeave: any;
    let timeStampArrive: any;
    let person: any;


    if (this.type == 'pickUp') {
      this.typeDescriptionArrive = "Ankunft am";
      this.typeDescriptionLeave = "Abgeholt am";
      timeStampLeave = this.order.pickUpTimestampLeave;
      timeStampArrive = this.order.pickUpTimestampArrive;
      person = this.order.pickUpPerson;
    }

    if (this.type == 'delivery') {
      this.typeDescriptionArrive = "Ankunft am";
      this.typeDescriptionLeave = "Abgegeben am";
      timeStampLeave = this.order.deliveryTimestampLeave;
      timeStampArrive = this.order.deliveryTimestampArrive;
      person = this.order.deliveryPerson;

      if (this.order.pickUpTimestampLeave != undefined) {
        this.minDateArrive = this.order.pickUpTimestampLeave;
        this.minDateLeave = this.order.pickUpTimestampLeave;
      }

    }

    if (timeStampArrive != undefined) {
      this.actionTimestampArrive = new Date(timeStampArrive);
      // this.localCompleteDateArrive = this.actionTimestampArrive.substring(0, this.actionTimestampArrive.length - 1);
    }

    if (timeStampLeave != undefined) {
      this.actionTimestampLeave = new Date(timeStampLeave);
      // this.localCompleteDateLeave = this.actionTimestampLeave.substring(0, this.actionTimestampLeave.length - 1);
    }

    if (person != undefined) {
      actionPerson = person;
    }


    this.editForm = this.fb.group({
      actionTimestampLeave: [this.actionTimestampLeave, {disabled: !this.isMyOrder}, [Validators.required]],
      actionTimestampArrive: [this.actionTimestampArrive, {disabled: !this.isMyOrder}, [Validators.required]],
      actionPerson: [{value: actionPerson, disabled: !this.isMyOrder}, [Validators.required]]
    });

    if (this.isMyOrder) {
      this.editForm.enable();
    }
  }

  changeDateTo(e: any) {
    this.minDateLeave = e.value!;
  }

  saveInformations() {
    if (this.editForm.valid) {
      let tmpOrder: any;

      let actionTimestampArrive: Date = new Date(this.editForm.controls["actionTimestampArrive"].value);
      // Add one hour to the deliveryTimestamp
      //actionTimestampArrive.setHours(actionTimestampArrive.getHours() + 1);


      let actionTimestampLeave: Date = new Date(this.editForm.controls["actionTimestampLeave"].value);
      // Add one hour to the deliveryTimestamp
      //actionTimestampLeave.setHours(actionTimestampLeave.getHours() + 1);
      let actionPerson: Date = this.editForm.controls["actionPerson"].value;

      if (this.editForm.controls["actionTimestampArrive"].value == undefined || this.editForm.controls["actionTimestampArrive"].value == "") {
        this.snackbarService.openSnackBar('Zeitpunkt der Ankuft muss eingtragen werden.', 'Ok', 'red-snackbar');
        return;
      } else {
        if (this.editForm.controls["actionTimestampLeave"].value == undefined || this.editForm.controls["actionTimestampLeave"].value == "") {
          this.snackbarService.openSnackBar('Zeitpunkt der Übergabe muss eingtragen werden.', 'Ok', 'red-snackbar');
          return;
        }
      }

      if (this.type == 'delivery') {
        tmpOrder = {
          id: this.order.id,
          deliveryTimestampLeave: actionTimestampLeave,
          deliveryTimestampArrive: actionTimestampArrive,
          deliveryPerson: actionPerson,
        };

        if (this.order.pickUpTimestampLeave == undefined || this.order.pickUpPerson == "") {
          this.snackbarService.openSnackBar('Abholungszeitpunkt und Person muss eingetragen sein.', 'Ok', 'red-snackbar');
          return;
        }
      }

      if (this.type == 'pickUp') {
        tmpOrder = {
          id: this.order.id,
          pickUpTimestampLeave: actionTimestampLeave,
          pickUpTimestampArrive: actionTimestampArrive,
          pickUpPerson: actionPerson,
        };
      }


      let finishOrder: boolean = false;

      //this.globals.dataLoadingText="Daten senden...";
      this.loading = true;

      this.orderService.patchOrder(tmpOrder).pipe(
        map((order: any) => {
          if (this.type == 'pickUp') {
            this.order.pickUpTimestampLeave = order.pickUpTimestampLeave;
            this.order.pickUpTimestampArrive = order.pickUpTimestampArrive;
            this.order.pickUpPerson = order.pickUpPerson;
          }
          if (this.type == 'delivery') {
            this.order.deliveryTimestampArrive = order.deliveryTimestampArrive;
            this.order.deliveryTimestampLeave = order.deliveryTimestampLeave;
            this.order.deliveryPerson = order.deliveryPerson;


            if (this.order.deliveryTimestampArrive != undefined &&
              this.order.deliveryTimestampLeave != undefined &&
              this.order.orderStatus != 'COMPLETE' &&
              this.order.deliveryPerson != undefined) {
              finishOrder = true;
            }

          }
        }),
        mergeMap(() => iif(() => (finishOrder), this.orderService.postOrderStatusById(this.order.id, 'COMPLETE'), of(0)))
      ).subscribe((response: any) => {

          this.loading = false;
          this.initializeForm();

          if (finishOrder) {
            this.order.orderStatus = "COMPLETE";
            this.order.orderStatusDescription = this.orderService.getOrderStatusDescription("COMPLETE");
            this.refreshOrder.emit();
            this.showFinish = true;
            this.snackbarService.openSnackBar('Der Auftrag wurde erledigt.', 'Ok', 'green-snackbar');

            setTimeout(() => {
              this.showFinish = false;
            }, 2500);

          } else {

            this.snackbarService.openSnackBar('Aktualisiert.', 'Ok', 'green-snackbar');

          }
        }
      )
    }
  }


  private formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    const hours = ('0' + date.getHours()).slice(-2);
    const minutes = ('0' + date.getMinutes()).slice(-2);

    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

}
