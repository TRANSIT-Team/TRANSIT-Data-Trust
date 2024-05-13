import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import {
  catchError,
  empty,
  forkJoin,
  from,
  iif,
  map,
  mergeMap,
  Observable,
  of,
  Subscription,
  throwError,
} from 'rxjs';
import {
  Company,
  CompanyAddress,
  LocationPoint,
  OutsourceStategie,
} from 'src/app/company/shared/company';
import { AppResource, UniqueResource } from 'src/app/_core/AbstractResource';
import {
  Order,
  OrderComment,
  OrderProperty,
  OrderStatus,
  SubOrder,
} from './order';
import {
  GeoJsonProperties,
  Geometry,
  FeatureCollection,
  Feature,
} from 'geojson';
import { DirectionService } from 'src/app/map/shared/direction.service';
import { BehaviorSubject } from 'rxjs';
import { Package, PackageProperty } from 'src/app/packages/shared/package';
import {
  HateoasResourceService,
  PagedResourceCollection,
  Resource,
  ResourceCollection,
} from '@lagoshny/ngx-hateoas-client';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { Address } from '../../address/shared/address';
import { ContactPerson } from '../../contact-person/shared/contact-person';
import { AddressService } from '../../address/shared/address.service';
import { CompanyService } from '../../company/shared/company.service';
import { AuthService } from '../../_core/auth.service';
import { MapService } from '../../map/shared/map.service';
import { OrderRight, OrderRightByStatus, OrderRights } from './order-rights';

//@ts-ignore
import orderRightsGlobal from './order-rights.json';
import { EntryEntityDto } from '../../entity-rights/shared/entity-right';
import {forEach} from "lodash";
import {FunctionsService} from "../../_core/functions.service";

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private outsourceStategie = new BehaviorSubject<any>({});
  currentOutsourceStategie = this.outsourceStategie.asObservable();

  constructor(
    private mapService: MapService,
    public authService: AuthService,
    private companyService: CompanyService,
    private addressService: AddressService,
    private http: HttpClient,
    private keycloakService: KeycloakService,
    private directionService: DirectionService,
    private resourceHateoasService: HateoasResourceService,
    private functionsService: FunctionsService
  ) {}

  getOrderStatusFlow(orderStatus: string): string[] {
    let flow: string[] = [];
    let tmpOrder: Order = new Order();

    if (orderStatus == 'REQUESTED') {
      // flow = tmpOrder.orderStatusSelection.filter(stat => (stat != "COMPLETE" && stat != "PROCESSING" && stat != "CANCELED" && stat != orderStatus));
      flow = [];
    }

    if (orderStatus == 'CREATED') {
      //flow = tmpOrder.orderStatusSelection.filter(stat => (stat != "REQUESTED" && stat != "REJECTED" && stat != "COMPLETE" && stat != orderStatus));
      flow = ['OPEN', 'CANCELED'];
    }

    if (orderStatus == 'OPEN') {
      //flow = tmpOrder.orderStatusSelection.filter(stat => (stat != "REQUESTED" && stat != "REJECTED" && stat != "COMPLETE" && stat != orderStatus));
      flow = ['PROCESSING', 'CANCELED'];
    }

    if (orderStatus == 'PROCESSING') {
      flow = ['COMPLETE', 'CANCELED'];
    }

    if (orderStatus == 'CANCELED') {
      flow = [];
    }

    if (orderStatus == 'COMPLETE') {
      flow = [];
    }

    flow.push(orderStatus);

    flow.sort((a: any, b: any) => (a > b ? 1 : -1));

    return flow;
  }

  updateOursourceStrategie(outsourceStategie: OutsourceStategie) {
    this.outsourceStategie.next(outsourceStategie);
  }

  splitOrderInTwoSubOrders(order: Order) {}

  getAddressPointsFromOrder(order: Order): any {
    let points: Feature[] = [];
    if (order.addressFrom != undefined) {
      if (order.addressFrom.locationPoint != undefined) {
        points.push(
          this.mapService.createFeature(
            order.addressFrom.locationPoint.coordinates!,
            order.addressFrom.locationPoint!.type
          )
        );
      }
    }

    if (order.addressTo != undefined) {
      if (order.addressTo.locationPoint != undefined) {
        points.push(
          this.mapService.createFeature(
            order.addressTo.locationPoint.coordinates!,
            order.addressTo.locationPoint!.type
          )
        );
      }
    }

    return points;
  }

  getOrderWithoutPackages(orderId: string): Observable<any> {
    this.resourceHateoasService.evictResourcesCache();
    // let keycloakInstance: any = this.keycloakService.getKeycloakInstance().profile;
    let kUserId: any = '';
    let tmpOrder: Order = new Order();
    return this.resourceHateoasService.getResource(Order, orderId).pipe(
      map((order: any) => {
        tmpOrder = order;
        if (!tmpOrder.addressIdBilling) {
          tmpOrder.addressIdBilling = new Address();
          tmpOrder.addressIdBilling.id = '';
        }
      }),
      mergeMap(() =>
        iif(
          () => tmpOrder.addressIdFrom != null,
          this.resourceHateoasService.getResource(
            Address,
            tmpOrder.addressIdFrom != null ? tmpOrder.addressIdFrom.id : 0,
            {}
          ),
          of(null)
        )
      ),
      map((address: any) => {
        if (address != null) {
          tmpOrder.addressFrom = address;
        }
        return address;
      }),
      mergeMap(() =>
        iif(
          () => tmpOrder.addressIdTo != null,
          this.resourceHateoasService.getResource(
            Address,
            tmpOrder.addressIdTo != null ? tmpOrder.addressIdTo.id : 0,
            {}
          ),
          of(null)
        )
      ),
      map((address: any) => {
        if (address != null) {
          tmpOrder.addressTo = address;
        }
        return address;
      }),
      //getCoordinates for FromAddress
      mergeMap(() =>
        this.directionService.getCoordinatesFromAddresse(
          tmpOrder.addressFrom?.street +
            ',' +
            tmpOrder.addressFrom?.zip +
            ',' +
            tmpOrder.addressFrom?.city
        )
      ),
      map((points: FeatureCollection) => {
        // take the first, because it's the best/nearest locationresult
        if (tmpOrder.addressFrom != undefined) {
          if (tmpOrder.addressFrom.locationPoint != undefined) {
            tmpOrder.addressFrom.locationPoint.feature = points.features[0];
          }
        }
        return null;
      }),
      //getCoordinates for ToAddress
      mergeMap(() =>
        this.directionService.getCoordinatesFromAddresse(
          tmpOrder.addressTo?.street +
            ',' +
            tmpOrder.addressTo?.zip +
            ',' +
            tmpOrder.addressTo?.city
        )
      ),
      map((points: FeatureCollection) => {
        // take the first, because it's the best/nearest locationresult
        if (tmpOrder.addressTo != undefined) {
          if (tmpOrder.addressTo.locationPoint != undefined) {
            tmpOrder.addressTo.locationPoint.feature = points.features[0];
          }
        }
      }),
      map(() => {
        return tmpOrder;
      })
    );
  }

  getOrderPackages = (packages: AppResource[] = []) => {
    const arrayOfObservables = packages.map((p: Package) =>
      this.resourceHateoasService.getResource(Package, p.id)
    );
    return forkJoin(arrayOfObservables);
  };

  getSimplyOrder(orderId: string): Observable<any> {
    return this.resourceHateoasService.getResource(Order, orderId).pipe(
      map((order: any) => {
        if (!order.addressIdBilling) {
          order.addressIdBilling = new Address();
          order.addressIdBilling.id = '';
        }

        return order;
      })
    );
  }

  getMyOrders(p: any = {}, withAddresses: boolean = false): Observable<any> {
    let filter = 'deleted==false';

    let tmpResponse: any = [];
    let companyId: any = '';
    return this.companyService.getOwnCompany().pipe(
      map((company: Company) => {
        companyId = company.id;

        if (p.filter) {
          filter = filter + p.filter.replace(filter, '');
        }
        filter = filter + ';company.id==' + company.id + '';
        p.filter = filter;
        return company;
      }),
      mergeMap((company: Company) =>
        this.resourceHateoasService.getPage<Order>(Order, {
          params: p,
        })
      ),
      map((orders: any) => {
        let tmpOrders: any = [];

        if (orders.resources != undefined) {
          tmpOrders.push(
            ...orders.resources.filter(
              (order: Order) => order.companyId?.id == companyId
            )
          );
          //tmpOrders.push(...orders.resources.filter((order: Order) => order.companyId?.id == companyId && order.suborderType == false));
          // tmpOrders.push(...orders.resources.filter((order: Order) => order.companyId?.id == companyId && order.orderStatus != "REQUESTED" && order.orderStatus != "REJECTED" && order.suborderType == true));
        }

        tmpResponse = orders;
        //tmpResponse.totalElements = tmpOrders.length;
        return orders;
      }),
      mergeMap((orders: any) =>
        iif(
          () => withAddresses && orders.totalElements > 0,
          this.getOrdersAddresses(orders.resources),
          of(orders)
        )
      ),
      map((orders: any) => {
        if (withAddresses) {
          tmpResponse.resources = orders;
          orders = tmpResponse;
        }
        return orders;
      }),
      map((orders: any) => {
        if (orders == undefined) {
          orders = {
            totalElements: 0,
            resources: [],
          };
        }
        if (orders.totalElements == 0) {
          orders.resources = [];
        } else {
          orders.resources.forEach((o: Order) => {
            o.shortId = '#';
            if (o.id != undefined) {
              const parts = o.id.split('-');
              o.shortId = '#' + parts[0];
            }
          });
        }
        return orders;
      })
    );
  }

  getOrdersAddresses = (orders: Order[]) => {
    const arrayOfObservables = orders.map((order: Order) =>
      this.getOrderAddresses(order)
    );
    return forkJoin(arrayOfObservables);
  };

  getSimpleOrder(orderId: string): Observable<any> {
    return this.resourceHateoasService.getResource(Order, orderId);
  }

  setOrderParentInformations(order: Order): any {
    if (order.orderProperties != undefined && order.suborderType) {
      let s: any = order.orderProperties.filter(
        (a: OrderProperty) => a.key == 'ParentOrderCompanyName'
      )[0];
      if (s != undefined && s.value != '') {
        // console.log(s);
        order.parentOrderInformations += '<span>' + s.value + '</span><br/>';
      }
      s = order.orderProperties.filter(
        (a: OrderProperty) => a.key == 'ParentOrderContactPersonName'
      )[0];
      if (s != undefined && s.value != '') {
        order.parentOrderInformations += '<span>' + s.value + '</span><br/>';
      }
      s = order.orderProperties.filter(
        (a: OrderProperty) => a.key == 'ParentOrderContactPersonEmail'
      )[0];
      if (s != undefined && s.value != '') {
        order.parentOrderInformations += '<span>' + s.value + '</span><br/>';
      }
      s = order.orderProperties.filter(
        (a: OrderProperty) => a.key == 'ParentOrderContactPersonPhone'
      )[0];
      if (s != undefined && s.value != '') {
        order.parentOrderInformations += '<span>' + s.value + '</span>';
      }
      return order;
    }
  }

  getSubOrderFromParentOrder(
    orderId: string,
    callContactPerson: boolean = true,
    parentOrder: Order
  ): Observable<any> {
    let tmpOrder: Order = new Order();
    let updateFromAddress: boolean = false;
    let updateToAddress: boolean = false;
    let myOrder: boolean = false;

    this.resourceHateoasService.evictResourcesCache();
    return of(0).pipe(
      mergeMap(() => this.resourceHateoasService.getResource(Order, orderId)),
      map((order: any) => {
        tmpOrder = order;
        tmpOrder.orderStatusDescription = this.getOrderStatusDescription(
          tmpOrder.orderStatus!
        );
        let nO: any = this.setOrderParentInformations(order.orderProperties);
        if (nO != undefined) {
          tmpOrder = nO;
        }
        if (!tmpOrder.addressIdBilling) {
          tmpOrder.addressIdBilling = new Address();
          tmpOrder.addressIdBilling.id = '';
        }

        if (parentOrder.addressFrom) {
          tmpOrder.addressFrom = parentOrder.addressFrom;
          let a: Address = new Address();
          a.id = tmpOrder.addressIdFrom!.id!;
          a.name = parentOrder.addressFrom.name;
          a.street = parentOrder.addressFrom.street;
          a.zip = parentOrder.addressFrom.zip;
          a.city = parentOrder.addressFrom.city;
          a.state = parentOrder.addressFrom.state;
          a.country = parentOrder.addressFrom.country;
          tmpOrder.addressFrom = a;
        }

        if (parentOrder.addressTo) {
          let a: Address = new Address();
          a.id = tmpOrder.addressIdTo!.id!;
          a.name = parentOrder.addressTo.name;
          a.street = parentOrder.addressTo.street;
          a.zip = parentOrder.addressTo.zip;
          a.city = parentOrder.addressTo.city;
          a.state = parentOrder.addressTo.state;
          a.country = parentOrder.addressTo.country;
          tmpOrder.addressTo = a;
        }

        if (parentOrder.packageItems) {
          tmpOrder.packageItems = parentOrder.packageItems;
        }

        return order;
      }),
      mergeMap(() =>
        iif(
          () => tmpOrder.contactPersonId != null && callContactPerson,
          this.resourceHateoasService.getResource(
            ContactPerson,
            tmpOrder.contactPersonId != null ? tmpOrder.contactPersonId.id : 0,
            {}
          ),
          of(null)
        )
      ),
      catchError((err) => {
        return of(null);
      }),
      map((contactPerson: any) => {
        if (contactPerson != null) {
          tmpOrder.contactPerson = contactPerson;
        }
      }),
      mergeMap(() =>
        iif(
          () => tmpOrder.companyId != undefined,
          this.companyService.getCompanyById(tmpOrder.companyId!.id),
          of(null)
        )
      ),
      map((company: Company) => {
        tmpOrder.companyId = company;
      }),
      mergeMap(() =>
        iif(
          () => tmpOrder.companyId != undefined,
          this.companyService.getOwnCompany(),
          of(null)
        )
      ),
      map((company: Company) => {
        if (tmpOrder.companyId != undefined) {
          if (tmpOrder.companyId.id != undefined) {
            if (tmpOrder.companyId.id == company.id) {
              tmpOrder.isMyOrder = true;
            }
          }
        }
      }),
      map(() => {
        return tmpOrder;
      })
    );
  }

  getOrderAddresses(order: any): Observable<any> {
    return of(0).pipe(
      mergeMap(() =>
        iif(
          () => order.addressIdFrom != null,
          this.resourceHateoasService.getResource(
            Address,
            order.addressIdFrom.id != null ? order.addressIdFrom.id : 0,
            {}
          ),
          of(null)
        )
      ),
      map((address: any) => {
        if (address != null) {
          order.addressFrom = address;
        }
      }),
      mergeMap(() =>
        iif(
          () => order.addressIdTo != null,
          this.resourceHateoasService.getResource(
            Address,
            order.addressIdTo.id != null ? order.addressIdTo.id : 0,
            {}
          ),
          of(null)
        )
      ),
      map((address: any) => {
        if (address != null) {
          order.addressTo = address;
        }
        return order;
      })
    );
  }

  getOrder(
    orderId: string,
    callContactPerson: boolean = true
  ): Observable<any> {
    let tmpOrder: Order = new Order();
    let updateFromAddress: boolean = false;
    let updateToAddress: boolean = false;

    this.resourceHateoasService.evictResourcesCache();
    return of(0).pipe(
      mergeMap(() => this.resourceHateoasService.getResource(Order, orderId)),
      map((order: any) => {
        tmpOrder = order;
        tmpOrder.orderStatusDescription = this.getOrderStatusDescription(
          tmpOrder.orderStatus!
        );
        let nO: any = this.setOrderParentInformations(order.orderProperties);
        if (nO != undefined) {
          tmpOrder = nO;
        }
        if (!tmpOrder.addressIdBilling) {
          tmpOrder.addressIdBilling = new Address();
          tmpOrder.addressIdBilling.id = '';
        }

        return order;
      }),
      mergeMap((order: any) =>
        iif(
          () => order.packageItemIds.length > 0,
          this.getOrderPackages(order.packageItemIds),
          of(0)
        )
      ),
      map((packages: any) => {
        tmpOrder.packageItems = [];
        if (packages != 0) {
          packages.forEach((p: Package) => {
            const parts = p.id.split('-');
            p.shortId = '#' + parts[0];
            tmpOrder.packageItems?.push(p);
          });
        }
        return null;
      }),
      mergeMap(() =>
        iif(
          () => tmpOrder.addressIdFrom != null,
          this.resourceHateoasService.getResource(
            Address,
            tmpOrder.addressIdFrom != null ? tmpOrder.addressIdFrom.id : 0,
            {}
          ),
          of(null)
        )
      ),
      map((address: any) => {
        if (address != null) {
          tmpOrder.addressFrom = address;
          if (address?.locationPoint != undefined) {
            if (
              address?.locationPoint.coordinates[0] == 50 &&
              address?.locationPoint.coordinates[1] == 50
            ) {
              updateFromAddress = true;
            }
          } else {
            updateFromAddress = true;
          }
        }
        return address;
      }),
      mergeMap(() =>
        iif(
          () => updateFromAddress,
          this.directionService.getCoordinatesFromAddresse(
            tmpOrder.addressFrom?.street +
              ',' +
              tmpOrder.addressFrom?.zip +
              ',' +
              tmpOrder.addressFrom?.city
          ),
          of(null)
        )
      ),
      map((points: any) => {
        if (updateFromAddress) {
          let newLocation: LocationPoint = new LocationPoint();
          newLocation.type = 'Point';
          newLocation.coordinates = [
            points.features[0].geometry.coordinates[0]!,
            points.features[0].geometry.coordinates[1],
          ];
          tmpOrder.addressFrom!.locationPoint = newLocation;
        }
      }),
      mergeMap(() =>
        iif(
          () => updateFromAddress,
          this.addressService.patchAddressLocationpoint(tmpOrder.addressFrom!),
          of(null)
        )
      ),
      mergeMap(() =>
        iif(
          () => tmpOrder.addressIdTo != null,
          this.resourceHateoasService.getResource(
            Address,
            tmpOrder.addressIdTo != null ? tmpOrder.addressIdTo.id : 0,
            {}
          ),
          of(null)
        )
      ),
      map((address: any) => {
        if (address != null) {
          tmpOrder.addressTo = address;
          if (address?.locationPoint != undefined) {
            if (
              address?.locationPoint.coordinates[0] == 50 &&
              address?.locationPoint.coordinates[1] == 50
            ) {
              updateToAddress = true;
            }
          } else {
            updateToAddress = true;
          }
        }
        return address;
      }),
      mergeMap(() =>
        iif(
          () => updateToAddress,
          this.directionService.getCoordinatesFromAddresse(
            tmpOrder.addressTo?.street +
              ',' +
              tmpOrder.addressTo?.zip +
              ',' +
              tmpOrder.addressTo?.city
          ),
          of(null)
        )
      ),
      map((points: any) => {
        if (updateToAddress) {
          let newLocation: LocationPoint = new LocationPoint();
          newLocation.type = 'Point';
          newLocation.coordinates = [
            points.features[0].geometry.coordinates[0]!,
            points.features[0].geometry.coordinates[1],
          ];
          tmpOrder.addressTo!.locationPoint = newLocation;
        }
      }),
      mergeMap(() =>
        iif(
          () => updateFromAddress,
          this.addressService.patchAddressLocationpoint(tmpOrder.addressTo!),
          of(null)
        )
      ),
      mergeMap(() =>
        iif(
          () => tmpOrder.contactPersonId != null && callContactPerson,
          this.resourceHateoasService.getResource(
            ContactPerson,
            tmpOrder.contactPersonId != null ? tmpOrder.contactPersonId.id : 0,
            {}
          ),
          of(null)
        )
      ),
      map((contactPerson: any) => {
        if (contactPerson != null) {
          tmpOrder.contactPerson = contactPerson;
        }
      }),
      mergeMap(() =>
        iif(
          () => tmpOrder.companyId != undefined,
          this.companyService.getOwnCompany(),
          of(null)
        )
      ),
      map((company: Company) => {
        if (tmpOrder.companyId != undefined) {
          if (tmpOrder.companyId.id != undefined) {
            if (tmpOrder.companyId.id == company.id) {
              tmpOrder.companyId = company;
              tmpOrder.isMyOrder = true;
            }
          }
        }
      }),
      map(() => {
        const parts = tmpOrder.id.split('-');
        tmpOrder.shortId = '#' + parts[0];
        return tmpOrder;
      })
    );
  }

  insertOrder(order: Order): Observable<any> {
    return of(0).pipe(
      map(() => {
        let props: any[] = [];
        order.orderDefaultProperties.forEach((p) => {
          let newProp: any = new UniqueResource();
          newProp.key = p.name;
          newProp.description = p.description;
          newProp.value = 'empty';
          newProp.type = 'string';
          props.push(newProp);
        });
        order.orderProperties = props;
        return props;
      }),
      // mergeMap((props: OrderProperty[]) => this.insertOrderProperies(order,props)),
      mergeMap((props: OrderProperty[]) =>
        this.resourceHateoasService.createResource(Order, { body: order })
      )
    );
  }

  patchOrder(order: any): Observable<any> {
    return this.resourceHateoasService.patchResourceById(Order, order.id, {
      body: order,
    });
  }

  patchOrderInternalComment(order: any): Observable<any> {
    if (order.comment == undefined) {
      order.comment = '';
    }
    return this.resourceHateoasService.patchResourceById(Order, order.id, {
      body: { comment: order.comment },
    });
  }

  patchOrderReasonForCancel(order: any): Observable<any> {
    if (order.reasonForCancel == undefined) {
      order.reasonForCancel = '';
    }
    return this.resourceHateoasService.patchResourceById(Order, order.id, {
      body: { reasonForCancel: order.reasonForCancel },
    });
  }

  deleteOrder(id: any): Observable<any> {
    return this.resourceHateoasService.deleteResourceById(Order, id);
  }

  getOrdersCompany = (orders: any) => {
    const arrayOfObservables = orders.map((o: any) =>
      this.companyService.getCompanyById(o.companyId!.id)
    );
    return forkJoin(arrayOfObservables);
  };

  getOrderStatus(orderId: string): Observable<any> {
    return this.http
      .get<any>(environment.backend + '/orders/' + orderId + '/orderstatus/')
      .pipe(
        map((response: OrderStatus) => {
          let oStatus: OrderStatus = new OrderStatus();
          oStatus.orderStatus = response.orderStatus;
          oStatus.id = orderId;
          return oStatus;
        })
      );
  }

  getOrdersWeekly(): Observable<any> {
    return this.http
      .get<any>(environment.backend + '/orders/overview/daily')
      .pipe(
        map((response: any) => {

          if (response._embedded){
            if (response._embedded.ordersWeekly) {

              response._embedded.ordersWeekly.forEach((item:any)=>{

                item.orderDate = this.functionsService.formatDateTimeToGermanDate(item.orderDate, true);


              });


              return response._embedded.ordersWeekly;
            }
          }
          return response;
        })
      );
  }

  getOrdersSummary(): Observable<any> {
    return this.http
      .get<any>(environment.backend + '/orders/overview?notDeleted=true')
      .pipe(
        map((response: any) => {
          return response;
        })
      );
  }

  getOrdersSuborderSummary(status: string): Observable<any> {
    return this.http
      .get<any>(
        environment.backend + '/orders/overview/suborders?orderStatus' + status
      )
      .pipe(
        map((response: any) => {
          response.status = status;
          return response;
        })
      );
  }

  getOrderComments(orderId: string): Observable<any> {
    let orderComments: OrderComment[] = [];
    return this.http
      .get<any>(environment.backend + '/orders/' + orderId + '/comment')
      .pipe(
        map((response: any) => {
          if (response._embedded) {
            orderComments = response._embedded.ordercommentchatentries;
          }
          return orderComments;
        }),
        mergeMap((o: OrderComment[]) =>
          iif(
            () => orderComments.length > 0,
            this.getOrderCommentsCompanies(o),
            of(0)
          )
        ),
        map((companies: any) => {
          if (companies != 0) {
            orderComments.forEach((o: OrderComment) => {
              let c: Company = companies.filter(
                (company: Company) => o.companyId == company.id
              )[0];
              o.company = c;
            });
          }
          return orderComments;
        })
      );
  }

  getOrderCommentsCompanies = (oC: OrderComment[] = []) => {
    const arrayOfObservables = oC.map((c: any) =>
      this.companyService.getCompanyById(c.companyId)
    );
    return forkJoin(arrayOfObservables);
  };

  postOrderComment(orderId: string, comment: any): Observable<any> {
    return this.http
      .post<any>(
        environment.backend + '/orders/' + orderId + '/comment',
        comment
      )
      .pipe(
        map((response: any) => {
          return response;
        })
      );
  }

  deleteOrderComment(orderId: string, commentId: string): Observable<any> {
    return this.http
      .delete<any>(
        environment.backend + '/orders/' + orderId + '/comment/' + commentId
      )
      .pipe(
        map((response: any) => {
          return response;
        })
      );
  }

  putOrderComment(
    orderId: string,
    commentId: string,
    comment: any
  ): Observable<any> {
    return this.http
      .put<any>(
        environment.backend + '/orders/' + orderId + '/comment/' + commentId,
        comment
      )
      .pipe(
        map((response: any) => {
          return response;
        })
      );
  }

  patchOrderComment(
    orderId: string,
    commentId: string,
    comment: any
  ): Observable<any> {
    return this.http
      .patch<any>(
        environment.backend + '/orders/' + orderId + '/comment/' + commentId,
        comment
      )
      .pipe(
        map((response: any) => {
          return response;
        })
      );
  }

  resetOutsource(orderId: string, e: EntryEntityDto[]): Observable<any> {
    return this.http
      .put<any>(
        environment.backend + '/orders/' + orderId + '/resetoutsource/',
        e
      )
      .pipe(
        map((response: any) => {
          return response;
        })
      );
  }

  getOrdersStatus = (subOrders: UniqueResource[]) => {
    const arrayOfObservables = subOrders.map((subOrderId: UniqueResource) =>
      this.getOrderStatus(subOrderId.id)
    );
    return forkJoin(arrayOfObservables);
  };

  getSubOrders = (subOrdersIds: UniqueResource[]) => {
    const arrayOfObservables = subOrdersIds.map((subOrderId: UniqueResource) =>
      //this.orderService.getOrder(subOrderId.id)
      this.getOrder(subOrderId.id, false)
    );
    return forkJoin(arrayOfObservables);
  };

  getSubOrdersFromOrder = (
    subOrdersIds: UniqueResource[],
    parentOrder: Order
  ) => {
    const arrayOfObservables = subOrdersIds.map((subOrderId: UniqueResource) =>
      this.getSubOrderFromParentOrder(subOrderId.id, true, parentOrder).pipe(
        catchError((err) => {
          console.error(
            `Error fetching sub-order with ID ${subOrderId.id}:`,
            err
          );
          // Return a safe value or propagate the error as needed
          return of(null);
        })
      )
    );

    return forkJoin(arrayOfObservables).pipe(
      map((subOrders) => subOrders.filter((subOrder) => subOrder !== null))
    );
  };

  getSubOrdersStatus = (subOrdersIds: UniqueResource[]) => {
    const arrayOfObservables = subOrdersIds.map((subOrderId: UniqueResource) =>
      this.getOrderStatus(subOrderId.id)
    );
    return forkJoin(arrayOfObservables);
  };

  getOrderStatusDescription(orderStatus: string): string {
    if (orderStatus == undefined) {
      return '';
    }
    let order: Order = new Order();
    let desc: any = order.orderStatusSelection.filter(
      (k) => k.name == orderStatus
    )[0];
    if (desc == undefined) {
      desc = orderStatus;
    }
    if (desc.description != undefined) {
      desc = desc.description;
    } else {
      console.log('Orderstatus: No translation for ' + orderStatus);
    }
    return desc;
  }

  postOrderStatus(order: Order): Observable<any> {
    return this.http.post<any>(
      environment.backend + '/orders/' + order.id + '/orderstatus/',
      {
        orderStatus: order.patchOrderStatus?.orderStatus,
      }
    );
  }

  postOrderStatusById(id: any, stat: string): Observable<any> {
    return this.http.post<any>(
      environment.backend + '/orders/' + id + '/orderstatus/',
      {
        orderStatus: stat,
      }
    );
  }

  revokeSubOrder(orderId: any): Observable<any> {
    return this.http.post<any>(
      environment.backend + '/orders/' + orderId + '/orderstatus/',
      {
        orderStatus: 'REVOKED',
      }
    );
  }

  getGlobalOrderRights(): OrderRights {
    return orderRightsGlobal;
  }

  getOrderRights(
    orderRightType: string,
    orderStatus: string,
    subOrders: Order[]
  ): OrderRightByStatus {
    let orderRights: OrderRights = this.getGlobalOrderRights();

    let orderRight: OrderRight = orderRights.orderRights.filter(
      (oR: OrderRight) => oR.type == orderRightType
    )[0];
    let oRbS: OrderRightByStatus[] = orderRight.orderStatus.filter(
      (orderRightStatus) => orderRightStatus.status == orderStatus
    );

    let orderRightDefault: OrderRightByStatus = orderRights.orderRights.filter(
      (oR: OrderRight) => oR.type == 'Default'
    )[0].orderStatus[0];

    let o: OrderRightByStatus;

    if (subOrders.length > 0) {
      let subStatus: string = '';
      const allRevokedOrRejected = subOrders.every((subOrder) => {
        return (
          subOrder.orderStatus === 'REVOKED' ||
          subOrder.orderStatus === 'REJECTED'
        );
      });

      if (allRevokedOrRejected) {
        subStatus = 'REVOKED';
      } else {
        subOrders.sort(this.sortOrderStatus);

        subOrders.forEach((subOrder: Order) => {
          if (
            subOrder.orderStatus == 'COMPLETE' ||
            subOrder.orderStatus == 'OPEN' ||
            subOrder.orderStatus == 'CANCELED' ||
            subOrder.orderStatus == 'PROCESSING' ||
            subOrder.orderStatus == 'ACCEPTED'
          ) {
            subStatus = subOrder.orderStatus;
          }
        });
      }

      if (subStatus == '') {
        o = oRbS[0];
      } else {
        o = oRbS.filter((s) => s.subStatus == subStatus)[0];
      }
    } else {
      o = oRbS[0];
    }

    if (o == undefined) {
      o = orderRightDefault;
    }
    return o;
  }

  public sortOrderStatus(a: Order, b: Order): number {
    const sortOrder = [
      'OPEN',
      'PROCESSING',
      'COMPLETE',
      'CANCELED',
      'ACCEPTED',
      'REQUESTED',
    ];
    const indexA = sortOrder.indexOf(a.orderStatus!);
    const indexB = sortOrder.indexOf(b.orderStatus!);
    // If both elements are in the sortOrder, compare their indices
    if (indexA !== -1 && indexB !== -1) {
      return indexA - indexB;
    }
    // If only one of the elements is in the sortOrder, prioritize it
    if (indexA !== -1) {
      return -1;
    }
    if (indexB !== -1) {
      return 1;
    }
    // If neither element is in the sortOrder, use alphabetical order
    return a.orderStatus!.localeCompare(b.orderStatus!);
  }

  postCopyOrderById(id: any): Observable<any> {
    return this.http.post<any>(
      environment.backend + '/orders/' + id + '/copy',
      {}
    );
  }
}
