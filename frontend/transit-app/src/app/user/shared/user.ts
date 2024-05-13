import {Resource, HateoasResource} from '@lagoshny/ngx-hateoas-client';
import {AppResource, PropertyResource} from "../../_core/AbstractResource";
import {Company} from "../../company/shared/company";


@HateoasResource('users/getId')
export class UserGetId extends AppResource {
  userId?: string;
}

@HateoasResource('users/getSelf')
export class UserSelf extends AppResource {
  companyId?: string;
  keycloakId?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  realmRoles?: string[];
  groups?: null;
  jobPosition?: string;
  companyID?: string;
  keycloakID?: string;
  enabled?: boolean;
  userProperties?: UserProperty[];
}


@HateoasResource('users')
export class User extends AppResource {
  companyId?: string;
  keycloakId?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  realmRoles?: string[];
  groups?: null;
  userProperties?: UserProperty[];
  jobPosition?: string;
  gender?: string;
}

@HateoasResource('userProperties')
export class UserProperty extends PropertyResource {
}

@HateoasResource('users/registration')
export class UserRegistration extends AppResource {
  keycloakId?: string;
  realmRoles?: string[];
  groups?: null;
  userProperties?: PropertyResource[];
  jobPosition?: string;
  company?: Company;
  enabled?: boolean;
}



