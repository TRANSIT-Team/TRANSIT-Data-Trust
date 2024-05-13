import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-loading-placeholder',
  templateUrl: './loading-placeholder.component.html',
  styleUrls: ['./loading-placeholder.component.css']
})
export class LoadingPlaceholderComponent implements OnInit {
  @Input() small = false;

  constructor() {
  }

  ngOnInit(): void {
  }

}
