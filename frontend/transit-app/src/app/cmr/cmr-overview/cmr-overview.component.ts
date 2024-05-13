import { Component, OnInit, ElementRef, ViewChild, Input } from '@angular/core';

import jspdf from 'jspdf';
import html2canvas from 'html2canvas';
import { Order } from '../../order/shared/order';

@Component({
  selector: 'app-cmr-overview',
  templateUrl: './cmr-overview.component.html',
  styleUrls: ['./cmr-overview.component.css'],
})
export class CmrOverviewComponent implements OnInit {
  @Input() order: Order;
  scaleView = false;
  loading = false;
  title = 'html-to-pdf-angular-application';
  @ViewChild('contentToConvert') myElement: ElementRef;

  constructor() {}

  ngOnInit(): void {}

  public convertToPDF() {
    this.scaleView = false;
    this.loading = true;

    setTimeout(() => {
      let fName: string =
        'CMR_' +
        this.order.id +
        '_Abs-' +
        this.order.addressFrom?.city +
        '_Empf-' +
        this.order.addressTo?.city;
      html2canvas(this.myElement.nativeElement).then((canvas) => {
        // Few necessary setting options
        var imgWidth = 200;
        var imgHeight = 288;
        // var imgHeight = (canvas.height * imgWidth) / canvas.width - 30;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');

        let pdf = new jspdf({
          orientation: 'p',
          unit: 'mm',
          format: 'a4',
          userUnit: 1200,
          compress: true,
          putOnlyUsedFonts: true,
        }); //// A4 size page of PDF
        var position = 0;
        pdf.addImage(contentDataURL, 'PNG', 5, 5, imgWidth, imgHeight);
        pdf.save(fName + '.pdf'); // Generated PDF
        this.loading = false;
      });
    }, 1000);
  }
}
