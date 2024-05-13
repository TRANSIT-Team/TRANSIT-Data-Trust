import { Component, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-popup',
  templateUrl: './popup.component.html',
  styleUrls: ['./popup.component.css']
})
export class PopupComponent implements OnInit {
  @ViewChild("popupContainer") popupContainer: any;
  constructor() { }

  ngOnInit(): void {
  }
// component.ts

  popup: any;

}
