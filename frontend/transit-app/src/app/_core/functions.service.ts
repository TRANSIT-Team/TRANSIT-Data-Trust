import {Injectable} from '@angular/core';
import {Location} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class FunctionsService {
  weekdays = ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"];

  constructor(private location: Location) {
  }


  routeBack() {
    this.location.back();
  }


  getRandomInt(max: any) {
    return Math.floor(Math.random() * max);
  }


  makeUid(): string {
    let id = "";
    id = this.makeid(8) + "-" + this.makeid(4) + "-" + this.makeid(4) + "-" + this.makeid(4) + "-" + this.makeid(8);
    return id;
  }

  makeid(length: number): string {
    var result = '';
    var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for (var i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  }

  transform(value: string): string {
    const parsedDate = new Date(value);
    const formattedDate = this.formatDate(parsedDate);
    return formattedDate;
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = this.formatTwoDigits(date.getMonth() + 1);
    const day = this.formatTwoDigits(date.getDate());
    const hours = this.formatTwoDigits(date.getHours());
    const minutes = this.formatTwoDigits(date.getMinutes());
    const seconds = this.formatTwoDigits(date.getSeconds());

    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.000`;
  }

  formatDateTimeToGermanDate(dateString: string, withWeekday: boolean = false): string {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();


    if (withWeekday) {
      const dayOfWeek = this.weekdays[date.getDay()];
      return `${dayOfWeek} ${day}.${month}.${year}`;
    }

    return `${day}.${month}.${year}`;
  }

  formatTwoDigits(value: number): string {
    return value < 10 ? `0${value}` : `${value}`;
  }

  transformDevExtremeHeaderRowFilter(filter: string): string {
    const andFilter = filter.split(",and,");
    const orFilter = filter.split(",or,");
    let filterColumnsAnd: any = [];
    let filterColumnsOr: any = [];
    let newFilterAnd: string = '';
    let newFilterOr: string = '';

    if (!filter.includes(',and,') && !filter.includes(',or')) {
      filterColumnsAnd.push(this.collectColumns(filter));
      newFilterAnd = ';(' + this.translateDevExtremeFilterKeyValue(filterColumnsAnd, ';') + ')';
    } else {

      if (andFilter.length > 1) {
        for (const condition of andFilter) {
          filterColumnsAnd.push(this.collectColumns(condition));

          // semicolon is AND (;)
          newFilterAnd = ';(' + this.translateDevExtremeFilterKeyValue(filterColumnsAnd, ';') + ')';
        }
      }

      if (orFilter.length > 1) {
        for (const condition of orFilter) {
          filterColumnsOr.push(this.collectColumns(condition));

          // comma is OR (,)
          newFilterOr = ';(' + this.translateDevExtremeFilterKeyValue(filterColumnsOr, ',') + ')';
        }
      }
    }

    let newFilter: string = newFilterAnd + newFilterOr;
    return newFilter.replace(';()', '');
  }

  collectColumns(filter: string): any {
    let filterColumns: any = [];
    let filterMethods: any[] = [
      {method: 'contains', translation: 'ilike', seperator: '='},
      {method: '=', translation: '==', seperator: ''},
      {method: '>=', translation: 'ge', seperator: '='},
      {method: '<=', translation: 'le', seperator: '='},
      {method: '>', translation: 'gt', seperator: '='},
      {method: '<', translation: 'lt', seperator: '='},
    ];
    filterMethods.forEach((filterMethod: any) => {
      let methodInString: string = ',' + filterMethod.method + ',';
      const indexOfMethod = filter.indexOf(methodInString);
      if (indexOfMethod > -1) {
        const filterValue = filter.substring(indexOfMethod + methodInString.length, filter.length);
        let filterColumn = filter.substring(0, indexOfMethod);
        filterColumn = filterColumn.replace('shortId', 'idString');
        let tmpKV: any = {column: filterColumn, filterMethod: filterMethod, value: filterValue};
        filterColumns.push(tmpKV);
      }
    });

    return filterColumns[0];
  }

  translateDevExtremeFilterKeyValue(filterColumns: any, typeOfFilter: string = ','): any {
    let newFilter = '';
    newFilter += typeOfFilter;
    filterColumns.forEach((filterColumn: any) => {
      let m: any = filterColumn.filterMethod;

      if (m.column == 'pickUpDate' || m.column == 'destinationDate') {
        filterColumn.value = this.transform(filterColumn.value);

      }

      newFilter += typeOfFilter + filterColumn.column + m.seperator + m.translation + m.seperator + filterColumn.value + '';


    })
    newFilter = newFilter.replace(typeOfFilter + typeOfFilter, '');
    //newFilter += ')';
    return newFilter;
  }

  translateDevExtremeSearchFilter(filterColumns: string[], filterValue: string): any {
    let newFilter = '';
    newFilter += '(,';
    filterColumns.forEach((col: string) => {

      if (col.includes('Date') || col.includes('orderStatus')) {
        newFilter += ',' + col + '==' + filterValue + '';
      } else {
        newFilter += ',' + col + '=ilike=' + filterValue + '';
      }

    })
    newFilter = newFilter.replace('(,,', '(')
    newFilter += ')';
    return newFilter;
  }


  getDelayedValue(value: any, delay: number, callback: (result: any) => void): void {
    setTimeout(() => {
      callback(value);
    }, delay);
  }

  isValidEmail(email: string): boolean {
    // Regular expression for a valid email address
    const emailRegex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/i;
    return emailRegex.test(email);
  }


}
