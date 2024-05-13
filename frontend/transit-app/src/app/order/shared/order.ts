import {
  Resource,
  HateoasResource,
  HateoasEmbeddedResource,
} from '@lagoshny/ngx-hateoas-client';
import { EntityRight } from 'src/app/entity-rights/shared/entity-right';
import { Package } from 'src/app/packages/shared/package';
import { Company } from 'src/app/company/shared/company';
import {
  AppEmbeddedResource,
  AppResource,
  PropertyResource,
  UniqueResource,
} from '../../_core/AbstractResource';
import { Address } from '../../address/shared/address';
import { ContactPerson } from '../../contact-person/shared/contact-person';
import { OrderRightByStatus } from './order-rights';

@HateoasEmbeddedResource(['orderstatus'])
export class OrderStatus extends AppEmbeddedResource {
  orderStatus: string;
}

@HateoasEmbeddedResource(['orderproperties'])
export class OrderProperty extends AppEmbeddedResource {
  name?: string;
  description?: string;
  key: string;
  value: string;
  type: string;
}

@HateoasResource('orders')
export class Order extends AppResource {
  readonly orderStatusSelection: OrderState[] = [
    { name: 'ALL', description: 'GESAMT' },
    { name: 'ACCEPTED', description: 'ANGENOMMEN' },
    { name: 'CANCELED', description: 'ABGEBROCHEN' },
    { name: 'COMPLETE', description: 'ERLEDIGT' },
    { name: 'CREATED', description: 'ANGELEGT' },
    { name: 'OPEN', description: 'OFFEN' },
    { name: 'PROCESSING', description: 'IN BEARBEITUNG' },
    { name: 'REJECTED', description: 'ABGELEHNT' },
    { name: 'REQUESTED', description: 'ANGEFRAGT' },
    { name: 'REVOKED', description: 'WIDERRUFEN' },
  ];

  readonly orderDefaultProperties: OrderState[] = [
    { name: 'price', description: 'Preis' },
    { name: 'parentOrderInformation', description: 'Preis' },
  ];

  addressFrom?: Address;
  addressIdFrom?: UniqueResource;
  addressTo?: Address;
  addressIdTo?: UniqueResource;
  addressBilling?: Address;
  addressIdBilling?: UniqueResource;
  suborderIds?: any[];
  orderTypeIds?: any[];
  companyId?: Company;
  contactPersonId?: UniqueResource;
  contactPerson?: ContactPerson;
  orderRouteIds?: any[];
  orderStatus?: string;
  patchOrderStatus?: OrderStatus;
  paymentIds?: any[];
  packageItemIds?: AppResource[];
  packageItems?: Package[];
  deliveryMethodId?: null;
  orderProperties?: OrderProperty[];
  parentOrderId?: UniqueResource;
  customerId?: UniqueResource;
  responsibleCompanyId?: string;
  destinationDate?: any;
  destinationDateTo?: any;
  pickUpDate?: any;
  pickUpDateTo?: any;
  suborderType?: boolean = false;
  internalComment?: string;
  price?: number;
  packagesPrice?: number;
  orderAltPrice?: number;
  outsourceCost?: number;
  deliveryTimestampLeave?: any;
  deliveryTimestampArrive?: any;
  deliveryPerson?: any;
  pickUpTimestampLeave?: any;
  pickUpTimestampArrive?: any;
  pickUpPerson?: any;
  reasonForCancel?: string;
  newOrderId?: string;
  oldOrderId?: string;

  //angular Dto
  shortId?: string;
  parentOrderInformations?: string;
  orderStatusDescription?: string;
  isMyOrder?: boolean = false;
  isOutsourced?: boolean = false;
  subOrders?: any = [];
  editable?: boolean = false;
  editableStatus?: boolean = true;
  orderStatusSelectionFlow?: string[];
  orderRightsGlobal?: OrderRightByStatus;
}

export class OrderState {
  name: string;
  description: string;
}

export class OrderComment extends AppResource {
  postParent: boolean;
  postChild: boolean;
  comment: string;
  companyId?: string;
  company?: Company;
  orderId?: string;
  person?: string;
}

export class SubOrderEntityRight {
  entitiesRights: EntityRight[];
  orderId: string;
}

@HateoasResource('orders/{id}/suborders')
export class SubOrder extends AppResource {
  addressFrom?: Address;
  addressIdFrom?: UniqueResource;
  addressTo?: Address;
  addressIdTo?: UniqueResource;
  addressBilling?: Address;
  addressIdBilling?: UniqueResource;
  suborderIds?: any[];
  orderTypeIds?: any[];
  companyId?: UniqueResource;
  orderRouteIds?: any[];
  orderStatus?: string;
  paymentIds?: any[];
  packageItemIds?: AppResource[];
  packageItems?: Package[];
  deliveryMethodId?: null;
  orderProperties?: OrderProperty[];
  parentOrderId?: UniqueResource;
  responsibleCompanyId?: string;
  destinationDate?: string;
  destinationDateTo?: string;
  pickUpDate?: string;
  pickUpDateTo?: string;
  suborderType?: boolean;
  comment?: string;
  contactPerson?: any;
  contactPersonId?: any;
}

export class OrderRight {
  orderId?: string;
  order?: Order;
  companyId?: string;
  company?: Company;
  orderEntityRights: OrderEntityRight[];
}

export class OrderEntityRight {
  name?: string;
  canRead: boolean;
  canWrite: boolean;
}

export class OrderSummary {
  ownOrders: OrderSummaryStatus;
  ownSuborders: OrderSummaryStatus;
  acceptedSubOrders: OrderSummaryStatus; // To be renamed for clarity
  parentOrderStatus?: string;
}

export class OrderSummaryStatus {
  revoked: number;
  processing: number;
  rejected: number;
  open: number;
  accepted: number;
  complete: number;
  requested: number;
  canceled: number;
  created: number;
}

export class OrdersWeekly {
  orders: number;
  orderDate: string;
}
