import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {KeycloakService} from "keycloak-angular";
import {DirectionService} from "../../map/shared/direction.service";
import {HateoasResourceService, ResourceCollection} from "@lagoshny/ngx-hateoas-client";


import {KeycloakProfile} from 'keycloak-js';
import {catchError, empty, forkJoin, from, iif, map, mergeMap, Observable, of, Subscription, throwError} from 'rxjs';
import {Company, LocationPoint, OutsourceStategie} from 'src/app/company/shared/company';
import {User, UserGetId} from 'src/app/user/shared/user';
import {AppResource, UniqueResource} from 'src/app/_core/AbstractResource';

import {GeoJsonProperties, Geometry, FeatureCollection, Feature} from 'geojson';
import {BehaviorSubject} from 'rxjs';
import {Package, PackageProperty} from 'src/app/packages/shared/package';
import {PagedResourceCollection, Resource} from '@lagoshny/ngx-hateoas-client';
import {environment} from 'src/environments/environment';

import {Address} from "../../address/shared/address";
import {ContactPerson} from "../../contact-person/shared/contact-person";
import {ChatEntry} from "./chat";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  constructor(private http: HttpClient, private keycloakService: KeycloakService, private directionService: DirectionService, private hateoasResourceService: HateoasResourceService) {
  }


  getChatOverview(): Observable<any> {
    return this.http.get<any>(environment.backend + '/orders/chat').pipe(map((coll: any) => {
      return coll._embedded.chatentries;
    }));
  }

  getOrderChat(orderId: string): Observable<any> {
    return this.http.get<any>(environment.backend + '/orders/' + orderId + '/chat/');
  }


  postChatMessage(orderId: any, message: any) {
    return this.http.post<any>(environment.backend + '/orders/' + orderId + '/chat/',
      {
        orderId: orderId,
        sequenceId: null,
        text: message,
        companyId: null,
        readStatus: false
      }
    );
  }

  postChatMessageReadStatus(orderId: string) {
    return this.http.post<any>(environment.backend + '/orders/' + orderId + '/chat/read',
      {}
    );
  }
}
