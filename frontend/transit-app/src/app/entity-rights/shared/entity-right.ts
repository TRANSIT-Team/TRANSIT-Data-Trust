import {CompanyAddress} from "../../company/shared/company";


//@HateoasResource('entityrights')
export class EntityRight {
  entries?: EntityRightEntry[];
}

export class EntityRightEntry {
  entityId?: string;
  companyId?: string;
  orderId?: string;
  properties?: EntityRightProperties;
}

export class EntityRightProperties {
  readProperties?: string[];
  writeProperties?: string[];
}

export class EntityRightProperty {
  entityId?: string;
  typeClazz?: string;
  properties: ReadWriteProperty[];
  comment?: string;
}

export class CreatorCompany {
  companyId?: string;
  links?: any[];
}

export class CanReadWriteCompany {
  hasRightsCompany?: string;
  getRightsCompany?: string;
}

export class ReadWriteProperty {
  companyId?: string;
  readProperties: string[];
  writeProperties: string[];
}


// fine grained version
export class EntityRightsGlobal {
  entityRights: EntityRightGlobal[];
}

export class EntityRightGlobal {
  entity: string;
  properties: EntityRightsProperty[];
  valuesExpanded: boolean = false;
  description: string;
}

export class EntityRightsDefaultProperty {
  property:  string;
  defaultValue: boolean;
}

export class EntityRightsProperty {
  property: string;
  description: string;
  displayShareText: string;
  hideSelector: boolean;
  alwaysShared: boolean;
  neverShared: boolean;
  alwaysWriteAble: boolean = false;
  default: boolean = false;
}

export class EntityRightGlobalCompanyDefault {
  entity: string;
  properties: EntityRightsPropertyCompanyDefault[];
}

export class EntityRightsPropertyCompanyDefault {
  property: string;
  default: boolean;
}


export class EntryEntityDto {
  entityId: string;
  entries: EntryEntityRight[];
}


export class EntryEntityRight {
  companyId: string;
  orderId?: string;
  entityId?: string;
  properties: EntryEntityRightProperty;
}

export class EntryEntityRightProperty {
  readProperties: string[];
  writeProperties: string[];
}

export class PreDefinedOutsourceList {
  name?: string;
  preDefinedOutsources: PreDefinedOutsource[];
}

export class PreDefinedOutsource {
  parentId?: string;
  entityId?: string;
  orderId?: string;
  name?: string;
  typeClazz?: string;
  valuesExpanded: boolean = false;
  readProperties: PreDefinedProperty[];
  writeProperties: PreDefinedProperty[];
}

export class PreDefinedProperty {
  name: string;
  displayShareText: string;
  value?: string;
  selected?: boolean = true;
  description: string;
  hideSelector: boolean;
  alwaysShared: boolean;
  neverShared: boolean;
}


export class CompanyDefinedOutsource {
  parentId?: string;
  companyId?: string;
  orderStatus?: string;
  companyName?: string;
  companyAddresse?: CompanyAddress;
  valuesExpanded: boolean = false;
  valuesAllFalse: boolean = false;
  valuesAllTrue: boolean = false;
  companyDefinedProperties: CompanyDefinedOutsourceProperty[];
  companyDefinedPackageProperties: CompanyDefinedOutsourceSubProperty[];

}

export class CompanyDefinedOutsourceProperty {
  entityId?: string;
  orderId?: string;
  name?: string;
  typeClazz?: string;
  valuesExpanded: boolean = false;
  readProperties: PropertySelection[];
  writeProperties: PropertySelection[];

}

export class CompanyDefinedOutsourceSubProperty {
  entityId?: string;
  name?: string;
  typeClazz?: string;
  valuesExpanded: boolean = false;
  readProperties: PropertySelection[];
  subDefinedOutsourceProperties: CompanyDefinedOutsourceProperty[];
}


export class PropertySelection {
  property: string;
  description: string;
  displayShareText?: string;
  value: string;
  selected: boolean;
  type?: string;
  hideSelector: boolean;
  alwaysShared: boolean;
  neverShared: boolean;
}







export class CompanyDefaultSharingRight {
  defaultSharingRights?: DefaultSharingRight[];
}

export class DefaultSharingRight {
  entity?:            string;
  companyProperties?: DefaultCompanyProperty[];
}

export class DefaultCompanyProperty {
  property?: string;
  default?:  boolean;
  read?:  boolean = false;
  write?:  boolean = false;
}




