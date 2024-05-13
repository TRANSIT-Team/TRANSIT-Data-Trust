import {Resource, HateoasResource} from '@lagoshny/ngx-hateoas-client';
import {AppResource} from "../../_core/AbstractResource";

@HateoasResource('packageitems')
export class Package extends AppResource {
  packagePackageProperties?: PackagePackageProperty[];
  packageClass?: PackageClass;
  weightKg?: number;
  heightCm?: number;
  widthCm?: number;
  deepCm?: number;
  packagePrice?: string;


  //dto
  shortId?: string;
}

@HateoasResource('packageclasses')
export class PackageClass extends AppResource {
  name?: string;
}


@HateoasResource('packageproperties')
export class PackageProperty extends AppResource {
  key?: string;
  value?: string;
  defaultValue?: string;
  type?: string;
}

@HateoasResource('packagepackageproperties')
export class PackagePackageProperty extends AppResource {
  key?: string;
  value?: string;
  type?: string;

}
