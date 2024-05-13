import {Injectable} from '@angular/core';
import {Company} from "../company/shared/company";

@Injectable({
  providedIn: 'root',
})
export class Globals {
  scrollTo = '';
  scrollToSingle = '';
  myUser: any = undefined;
  myKeyCloakUserId = '';
  myCompanyId = '';
  myCompany: any = undefined;
  pageTitle: any = undefined;
  dataLoading: boolean = false;
  dataLoadingText: string = "Daten laden";
}

export const MY_FORMATS = {
  parse: {
    dateInput: 'LL',
  },
  display: {
    dateInput: 'YYYY-MM-DD',
    monthYearLabel: 'YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'YYYY',
  },
};
