import {HateoasEmbeddedResource, HateoasResource} from "@lagoshny/ngx-hateoas-client";
import {AppEmbeddedResource, AppResource} from "../../_core/AbstractResource";
import {LocationPoint} from "../../company/shared/company";

@HateoasResource('orders/chat')
export class ChatEntry extends AppResource {
  sequenceId?:     number;
  text?:           string;
  orderId?:        string;
  orderShortId?:        string;
  companyId?:      string;
  readStatus?:     boolean;
  person?:     string;
  active?:     boolean=false;
}


@HateoasEmbeddedResource(['chat'])
export class Chat extends AppEmbeddedResource {
  key?:string;
  value?:string;
}

