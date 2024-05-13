import {HateoasResource} from "@lagoshny/ngx-hateoas-client";
import {AppResource} from "../../_core/AbstractResource";
import {LocationPoint} from "../../company/shared/company";

@HateoasResource('addresses')
export class Address extends AppResource {
  companyName?: string;
  clientName?: string;
  name?: string;
  locationPoint?: LocationPoint;
  street?: string;
  zip?: string;
  city?: string;
  state?: string;
  country?: string;
  isoCode?: string;
  addressExtra?: string;
  inOrderUsed?: boolean;
  phoneNumber?: string;

}
