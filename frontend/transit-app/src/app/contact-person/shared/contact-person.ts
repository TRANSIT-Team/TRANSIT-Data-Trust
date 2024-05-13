import {HateoasResource} from "@lagoshny/ngx-hateoas-client";
import {AppResource} from "../../_core/AbstractResource";

@HateoasResource('contactpersons')
export class ContactPerson extends AppResource {
  name?: string;
  email?: string;
  phone?: string;
}
