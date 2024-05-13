import {Component, Input, OnInit} from '@angular/core';
import {Order} from "../order/shared/order";
import {Globals} from "../_core/globals";

@Component({
  selector: 'app-loading-info',
  templateUrl: './loading-info.component.html',
  styleUrls: ['./loading-info.component.css']
})
export class LoadingInfoComponent implements OnInit {
  @Input() showText: string ="Daten laden";
  constructor(public globals:Globals) { }

  ngOnInit(): void {
  }

}
