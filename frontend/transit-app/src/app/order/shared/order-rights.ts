export class OrderRights {
  orderRights: OrderRight[];
}

export class OrderRight {
  type: string;
  orderStatus: OrderRightByStatus[];
}

export class OrderRightByStatus {
  status: string;
  subStatus?: string;
  can: {
    commentPublic: boolean,
    outsource: boolean,
    reject: boolean,
    accept: boolean,
    resetOutsource: boolean
  };
  edit: {
    orderStatus: boolean;
    contactPerson: boolean;
    pickUpDate: boolean;
    deliveryDate: boolean;
    deliveryTimestamp: boolean;
    pickupTimestamp: boolean;
    pickUpPerson: boolean;
    deliveryPerson: boolean;
    comment: boolean;
    finance: boolean;
    price: boolean;
    package: boolean;
    packageProperty: boolean;
    addressFrom: boolean;
    addressTo: boolean;
    revoke: boolean;
    rights: boolean;
  };
  delete: {
    order: boolean
  };
  add: {
    commentPublic: boolean;
    package: boolean;
    packageProperty: boolean;
  };
}
