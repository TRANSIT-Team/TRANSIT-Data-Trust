import {HateoasEmbeddedResource, HateoasResource} from '@lagoshny/ngx-hateoas-client';
import {AppEmbeddedResource, AppResource, PropertyResource} from "../../_core/AbstractResource";
import {Feature} from 'geojson';
import {Address} from "../../address/shared/address";

@HateoasEmbeddedResource(['companyaddresses'])
export class CompanyAddress extends AppEmbeddedResource {
  locationPoint?: LocationPoint;
  street?: string;
  zip?: string;
  city?: string;
  state?: string;
  country?: string;
  isoCode?: string;
  addressExtra?: string;
  addressType?: string;
  fullAddresse?: string;
}

@HateoasResource('globalcompanyproperties')
export class GlobalCompanyProperty extends PropertyResource {
  name: string;
}

@HateoasEmbeddedResource(['companyproperties'])
export class CompanyProperty extends AppEmbeddedResource {
  key?:string;
  value?:string;
}

@HateoasResource('companies')
export class Company extends AppResource {
  name?: string;
  enabled?: boolean;
  favorite?: string;
  companyProperties?: CompanyProperty[]; //frontend
  companyAddresses?: CompanyAddress[]; //frontend
  deliveryAreaData?: CompanyDeliveryArea;
  customers?:Customer[];
  deliveryAreaZips?: string[];
  deliveryAreaPolyline?: string;
  deliveryAreaGeom?: string;
  deliveryArea?: Array<number[]>;
}


@HateoasEmbeddedResource(['deliveryarea'])
export class CompanyDeliveryArea extends AppEmbeddedResource {
  companyId?:string;
  deliveryAreaZips?: string[];
  deliveryAreaPolyline?: string;
  deliveryAreaGeom?: string;
  deliveryArea?: Array<number[]>;
}

@HateoasEmbeddedResource(['customers'])
export class Customer extends AppEmbeddedResource {
  name?: string;
  email?: string;
  tel?: string;
  companyId?: string;
  addressId?: string;
  address?: Address;
}

export class CompanyFavorite extends AppResource {
  name: string;
  companyIds?: string[];
  companies?: any;
}


@HateoasResource('locations')
export class Location extends AppResource {
  locationPoint?: LocationPoint;
}




export class LocationPoint {
  type?: string;
  coordinates?: number[];
  feature?: Feature;
}

export class OutsourceStategie {
  companies?: OutsourceStategieCompany[];
  rights?: OutsourceStategieRight[];

}

export class OutsourceStategieCompany {
  company: Company;
  sort: number;
  rights?: OutsourceStategieCompanyRight[];
  entityRights?: OutsourceStategieRightProperty[];
}

export class OutsourceStategieCompanyRight {
  right: OutsourceStategieRight;
  entity: string;
  entityId?: string;
  read: boolean = false;
  write: boolean = false;
}

export class OutsourceStategieRight {
  name: string;
  entity: string;
  relation?: any;
}


export class OutsourceStategieRightProperty {
  entityId?: string;
  typeClazz?: string;
  properties: OutsourceStategieRightReadWriteProperty[];
  comment?: string;
}

export class OutsourceStategieRightReadWriteProperty {
  readProperties: string[];
  writeProperties: string[];
}



