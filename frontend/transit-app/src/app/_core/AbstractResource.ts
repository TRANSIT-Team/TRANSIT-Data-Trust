import {EmbeddedResource, Resource} from "@lagoshny/ngx-hateoas-client";
import { BaseResource } from "@lagoshny/ngx-hateoas-client/lib/model/resource/base-resource";

export class AppResource extends Resource {
  id: string;
  createDate?: Date;
  createdBy?: string;
  modifyDate?: Date;
  lastModifiedBy?: string;
  deleted?: boolean;
}

export class AppEmbeddedResource extends EmbeddedResource {
  id: string;
  createDate?: Date;
  createdBy?: string;
  modifyDate?: Date;
  lastModifiedBy?: string;
  deleted?: boolean;
}

export class PropertyResource extends AppResource {
  key?:        string;
  value?:      string;
  type?:       string;
  description?:       string;
}

export class UniqueResource {
  id: string;
}
