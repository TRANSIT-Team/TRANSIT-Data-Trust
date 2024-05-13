create sequence IF NOT EXISTS if not exists public.hibernate_sequence start 1 increment 1;
create table IF NOT EXISTS if not exists public.addresses
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    location_point
    GEOMETRY,
    address_extra
    varchar
(
    255
), city varchar
(
    255
), client_name varchar
(
    255
), company_name varchar
(
    255
), country varchar
(
    255
), iso_code varchar
(
    255
), show_overview_filter varchar
(
    255
) DEFAULT 'SHOW', state varchar
(
    255
), street varchar
(
    255
), zip varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.addresses_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    location_point
    GEOMETRY,
    location_point_mod
    boolean,
    address_extra
    varchar
(
    255
), address_extra_mod boolean, city varchar
(
    255
), city_mod boolean, client_name varchar
(
    255
), client_name_mod boolean, company_name varchar
(
    255
), company_name_mod boolean, country varchar
(
    255
), country_mod boolean, iso_code varchar
(
    255
), iso_code_mod boolean, show_overview_filter varchar
(
    255
) DEFAULT 'SHOW', show_overview_filter_mod boolean, state varchar
(
    255
), state_mod boolean, street varchar
(
    255
), street_mod boolean, zip varchar
(
    255
), zip_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.car
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    capacity
    varchar
(
    255
), plate varchar
(
    255
), type varchar
(
    255
), weight varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.car_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    capacity
    varchar
(
    255
), capacity_mod boolean, plate varchar
(
    255
), plate_mod boolean, type varchar
(
    255
), type_mod boolean, weight varchar
(
    255
), weight_mod boolean, car_properties_mod boolean, locations_mod boolean, order_legs_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.car_locations
(
    car_id
    uuid
    not
    null,
    locations_id
    uuid
    not
    null,
    primary
    key
(
    car_id,
    locations_id
));
create table IF NOT EXISTS if not exists public.car_locations_aud
(
    rev
    int4
    not
    null,
    car_id
    uuid
    not
    null,
    locations_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    car_id,
    locations_id
));
create table IF NOT EXISTS if not exists public.car_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    varchar
(
    255
), key_mod boolean, type varchar
(
    255
), type_mod boolean, value varchar
(
    255
), value_mod boolean, car_id uuid, car_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.car_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    varchar
(
    255
), type varchar
(
    255
), value varchar
(
    255
), car_id uuid, primary key
(
    id
));
create table IF NOT EXISTS if not exists public.chat_entry_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    company_id
    uuid,
    company_id_mod
    boolean,
    order_id
    uuid,
    order_id_mod
    boolean,
    read_status
    BOOLEAN
    DEFAULT
    FALSE,
    read_status_mod
    boolean,
    sequence_id
    int8,
    sequence_id_mod
    boolean,
    text
    text,
    text_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.chat_entry
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    company_id
    uuid,
    order_id
    uuid,
    read_status
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    sequence_id
    int8,
    text
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.companies
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    name
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.companies_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    name
    text,
    name_mod
    boolean,
    company_addresses_mod
    boolean,
    company_properties_mod
    boolean,
    company_users_mod
    boolean,
    company_delivery_area_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.companiesids
(
    id
    uuid
    not
    null,
    companyoid
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.companiesids_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    companyoid
    uuid,
    companyoid_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.company_addresses_aud
(
    address_id
    uuid
    not
    null,
    company_id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    address_type
    varchar
(
    255
), address_type_mod boolean, address_mod boolean, company_mod boolean, primary key
(
    address_id,
    company_id,
    rev
));
create table IF NOT EXISTS if not exists public.company_delivery_area_delivery_area_zips_aud
(
    rev
    int4
    not
    null,
    company_delivery_area_id
    uuid
    not
    null,
    delivery_area_zips
    varchar
(
    255
) not null, revtype int2, primary key
(
    rev,
    company_delivery_area_id,
    delivery_area_zips
));
create table IF NOT EXISTS if not exists public.company_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.company_addresses
(
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    address_type
    varchar
(
    255
), company_id uuid not null, address_id uuid not null, primary key
(
    address_id,
    company_id
));
create table IF NOT EXISTS if not exists public.company_delivery_area_delivery_area_zips
(
    company_delivery_area_id
    uuid
    not
    null,
    delivery_area_zips
    varchar
(
    255
));
create table IF NOT EXISTS if not exists public.companydeliveryareas
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    delivery_area_geom
    GEOMETRY,
    delivery_area_polyline
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.companydeliveryareas_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    delivery_area_geom
    GEOMETRY,
    delivery_area_geom_mod
    boolean,
    delivery_area_polyline
    text,
    delivery_area_polyline_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    delivery_area_zips_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.company_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.contact_persons_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    company_id
    uuid,
    company_id_mod
    boolean,
    email
    varchar
(
    255
), email_mod boolean, name varchar
(
    255
), name_mod boolean, phone varchar
(
    255
), phone_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.contact_persons
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    company_id
    uuid,
    email
    varchar
(
    255
), name varchar
(
    255
), phone varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.cost
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    cost_sum
    float8,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.cost_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    cost_sum
    float8,
    cost_sum_mod
    boolean,
    cost_properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.cost_default_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    default_value
    text,
    default_value_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.cost_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    cost_id
    uuid,
    cost_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.cost_default_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_value
    text,
    key
    text,
    type
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.cost_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    cost_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.customers
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    address_id
    uuid,
    company_id
    uuid,
    email
    varchar
(
    255
), name varchar
(
    255
), tel varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.customers_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    address_id
    uuid,
    address_id_mod
    boolean,
    company_id
    uuid,
    company_id_mod
    boolean,
    email
    varchar
(
    255
), email_mod boolean, name varchar
(
    255
), name_mod boolean, tel varchar
(
    255
), tel_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.default_sharing_rights_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    default_sharing_rights
    text,
    default_sharing_rights_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.default_sharing_rights
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_sharing_rights
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.delivery_methods_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    delivery_method_name
    text,
    delivery_method_name_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.delivery_methods
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    delivery_method_name
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.globalcompanyproperties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    name
    varchar
(
    255
), type varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.globalcompanyproperties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    name
    varchar
(
    255
), name_mod boolean, type varchar
(
    255
), type_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.locations
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    location_point
    GEOMETRY,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.locations_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    location_point
    GEOMETRY,
    location_point_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.logger
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.logger_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.logger_properties
(
    logger_id
    uuid
    not
    null,
    properties_id
    uuid
    not
    null,
    primary
    key
(
    logger_id,
    properties_id
));
create table IF NOT EXISTS if not exists public.logger_properties_aud
(
    rev
    int4
    not
    null,
    logger_id
    uuid
    not
    null,
    properties_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    logger_id,
    properties_id
));
create table IF NOT EXISTS if not exists public.logger_propeties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    varchar
(
    255
), key_mod boolean, value varchar
(
    255
), value_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.logger_propeties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    varchar
(
    255
), value varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.order_leg_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    status
    varchar
(
    255
), status_mod boolean, type varchar
(
    255
), type_mod boolean, car_id uuid, car_mod boolean, order_route_id uuid, order_route_mod boolean, warehouse_id uuid, warehouse_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.order_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    order_id
    uuid,
    order_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.order_route_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    order_legs_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.order_route_order_legs_aud
(
    rev
    int4
    not
    null,
    order_route_id
    uuid
    not
    null,
    order_legs_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_route_id,
    order_legs_id
));
create table IF NOT EXISTS if not exists public.order_type_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    order_type_id
    uuid,
    order_type_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.order_types_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    typename
    varchar
(
    255
), typename_mod boolean, order_type_properties_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.order_types_order_type_properties_aud
(
    rev
    int4
    not
    null,
    order_type_id
    uuid
    not
    null,
    order_type_properties_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_type_id,
    order_type_properties_id
));
create table IF NOT EXISTS if not exists public.order_leg
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    status
    varchar
(
    255
), type varchar
(
    255
), car_id uuid, order_route_id uuid, warehouse_id uuid, primary key
(
    id
));
create table IF NOT EXISTS if not exists public.order_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    order_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.order_route
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.order_route_order_legs
(
    order_route_id
    uuid
    not
    null,
    order_legs_id
    uuid
    not
    null
);
create table IF NOT EXISTS if not exists public.orders
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    attention_flag
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    comment
    text,
    customer_id
    uuid,
    delivery_person
    text,
    delivery_timestamp
    timestamp,
    destination_date
    timestamp,
    id_string
    varchar
(
    255
), message_counter BIGINT DEFAULT 0 not null, new_order_id uuid, old_order_id uuid, order_alt_price double precision not null, order_status varchar
(
    255
), outsource_cost double precision not null, packages_price double precision not null, pick_up_date timestamp, price double precision not null, reason_for_cancel text, suborder_type BOOLEAN DEFAULT FALSE not null, address_billing_id uuid, address_from_id uuid, address_to_id uuid, company_id uuid, contact_person_id uuid, cost_id uuid, delivery_method_id uuid, parent_order_id uuid, payment_id uuid, primary key
(
    id
));
create table IF NOT EXISTS if not exists public.orders_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    attention_flag
    BOOLEAN
    DEFAULT
    FALSE,
    attention_flag_mod
    boolean,
    comment
    text,
    comment_mod
    boolean,
    customer_id
    uuid,
    customer_id_mod
    boolean,
    delivery_person
    text,
    delivery_person_mod
    boolean,
    delivery_timestamp
    timestamp,
    delivery_timestamp_mod
    boolean,
    destination_date
    timestamp,
    destination_date_mod
    boolean,
    id_string
    varchar
(
    255
), id_string_mod boolean, message_counter BIGINT DEFAULT 0, message_counter_mod boolean, new_order_id uuid, new_order_id_mod boolean, old_order_id uuid, old_order_id_mod boolean, order_alt_price double precision, order_alt_price_mod boolean, order_status varchar
(
    255
), order_status_mod boolean, outsource_cost double precision, outsource_cost_mod boolean, packages_price double precision, packages_price_mod boolean, pick_up_date timestamp, pick_up_date_mod boolean, price double precision, price_mod boolean, reason_for_cancel text, reason_for_cancel_mod boolean, suborder_type BOOLEAN DEFAULT FALSE, suborder_type_mod boolean, address_billing_id uuid, address_billing_mod boolean, address_from_id uuid, address_from_mod boolean, address_to_id uuid, address_to_mod boolean, company_id uuid, company_mod boolean, contact_person_id uuid, contact_person_mod boolean, cost_id uuid, cost_mod boolean, delivery_method_id uuid, delivery_method_mod boolean, order_properties_mod boolean, order_routes_mod boolean, order_types_mod boolean, package_items_mod boolean, parent_order_id uuid, parent_order_mod boolean, payment_id uuid, payment_mod boolean, suborders_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.orders_order_routes_aud
(
    rev
    int4
    not
    null,
    order_id
    uuid
    not
    null,
    order_routes_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_id,
    order_routes_id
));
create table IF NOT EXISTS if not exists public.orders_order_types_aud
(
    rev
    int4
    not
    null,
    order_id
    uuid
    not
    null,
    order_types_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_id,
    order_types_id
));
create table IF NOT EXISTS if not exists public.orders_order_routes
(
    order_id
    uuid
    not
    null,
    order_routes_id
    uuid
    not
    null
);
create table IF NOT EXISTS if not exists public.orders_order_types
(
    order_id
    uuid
    not
    null,
    order_types_id
    uuid
    not
    null,
    primary
    key
(
    order_id,
    order_types_id
));
create table IF NOT EXISTS if not exists public.orders_package_items_aud
(
    rev
    int4
    not
    null,
    order_id
    uuid
    not
    null,
    package_items_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_id,
    package_items_id
));
create table IF NOT EXISTS if not exists public.orders_package_items
(
    order_id
    uuid
    not
    null,
    package_items_id
    uuid
    not
    null,
    primary
    key
(
    order_id,
    package_items_id
));
create table IF NOT EXISTS if not exists public.order_type_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    order_type_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.order_types
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    typename
    varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.order_types_order_type_properties
(
    order_type_id
    uuid
    not
    null,
    order_type_properties_id
    uuid
    not
    null,
    primary
    key
(
    order_type_id,
    order_type_properties_id
));
create table IF NOT EXISTS if not exists public.package_classes_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    class_name
    text,
    name_mod
    boolean,
    package_items_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.package_items_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    comment
    text,
    comment_mod
    boolean,
    deep_cm
    float8,
    deep_cm_mod
    boolean,
    explosive
    BOOLEAN
    DEFAULT
    FALSE,
    explosive_mod
    boolean,
    frost
    BOOLEAN
    DEFAULT
    FALSE,
    frost_mod
    boolean,
    height_cm
    float8,
    height_cm_mod
    boolean,
    package_price
    double
    precision,
    package_price_mod
    boolean,
    weight_kg
    float8,
    weight_kg_mod
    boolean,
    width_cm
    float8,
    width_cm_mod
    boolean,
    package_class_id
    uuid,
    package_class_mod
    boolean,
    package_package_properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.package_package_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    package_item_id
    uuid,
    package_item_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.package_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    default_value
    text,
    default_value_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.package_routes_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    order_legs_id
    uuid,
    order_legs_mod
    boolean,
    package_item_id
    uuid,
    package_item_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.package_tracking_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    comment
    text,
    comment_mod
    boolean,
    status
    varchar
(
    255
), status_mod boolean, car_id uuid, car_mod boolean, location_id uuid, location_mod boolean, package_item_id uuid, package_item_mod boolean, supplier_id uuid, supplier_mod boolean, warehouse_id uuid, warehouse_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.package_classes
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    class_name
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.package_items
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    comment
    text,
    deep_cm
    float8
    not
    null,
    explosive
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    frost
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    height_cm
    float8
    not
    null,
    package_price
    double
    precision
    not
    null,
    weight_kg
    float8
    not
    null,
    width_cm
    float8
    not
    null,
    package_class_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.package_package_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    package_item_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.package_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_value
    text,
    key
    text,
    type
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.package_routes
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    order_legs_id
    uuid,
    package_item_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.package_tracking
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    comment
    text,
    status
    varchar
(
    255
), car_id uuid, location_id uuid, package_item_id uuid, supplier_id uuid, warehouse_id uuid, primary key
(
    id
));
create table IF NOT EXISTS if not exists public.payment_default_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    default_value
    text,
    default_value_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.payment_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    payment_id
    uuid,
    payment_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.payment_default_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_value
    text,
    key
    text,
    type
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.payment_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    payment_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.payments
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    amount
    float8,
    payment_status
    varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS if not exists public.payments_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    amount
    float8,
    amount_mod
    boolean,
    payment_status
    varchar
(
    255
), payment_status_mod boolean, payment_properties_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.revinfo
(
    rev
    int4
    not
    null,
    revtstmp
    int8,
    primary
    key
(
    rev
));
create table IF NOT EXISTS if not exists public.user_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    user_id
    uuid,
    user_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.user_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    user_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.users
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    job_position
    text,
    keycloak_email
    text,
    keycloak_id
    uuid,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS if not exists public.users_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    job_position
    text,
    job_position_mod
    boolean,
    keycloak_email
    text,
    keycloak_email_mod
    boolean,
    keycloak_id
    uuid,
    keycloak_id_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    user_properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.warehouse
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    capacity
    int8,
    name
    varchar
(
    255
), address_id uuid, primary key
(
    id
));
create table IF NOT EXISTS if not exists public.warehouse_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    capacity
    int8,
    capacity_mod
    boolean,
    name
    varchar
(
    255
), name_mod boolean, address_id uuid, address_mod boolean, order_legs_mod boolean, warehouse_properties_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.warehouse_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    varchar
(
    255
), key_mod boolean, type varchar
(
    255
), type_mod boolean, value varchar
(
    255
), value_mod boolean, warehouse_id uuid, warehouse_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS if not exists public.warehouse_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    varchar
(
    255
), type varchar
(
    255
), value varchar
(
    255
), warehouse_id uuid, primary key
(
    id
));
alter table public.car_locations
    add constraint UK_hye6h6ubuah6ymwrf4m4ayd8j unique (locations_id);
create index IDXrm5m29soyrdwfb0b15g6e1gxv on public.companydeliveryareas (company_id);
alter table public.logger_properties
    add constraint UK_nu654s12mmfaci9i3tav44hw6 unique (properties_id);
alter table public.order_route_order_legs
    add constraint UK_n3m9tts6yr2f18t36esxdjpsj unique (order_legs_id);
alter table public.orders_order_routes
    add constraint UK_epjm8irae6syjss7dxvytuj1m unique (order_routes_id);
alter table public.orders_order_types
    add constraint UK_33ir2es3j8w0efhv9ui3wqari unique (order_types_id);
alter table public.order_types_order_type_properties
    add constraint UK_138wbd8l6u88u40x4kgmqu4p1 unique (order_type_properties_id);
alter table public.addresses_aud
    add constraint FK_slt1on64rkymbaiekchjf5te0 foreign key (rev) references public.revinfo;
alter table public.car_aud
    add constraint FK_1ntgpdadbifq2o1doy18fjho3 foreign key (rev) references public.revinfo;
alter table public.car_locations
    add constraint FK5xkpuwq37wu85aac45pl6a3gj foreign key (car_id) references public.car;
alter table public.car_locations_aud
    add constraint FKf6ht2pi8087dkuu8f4f604wvu foreign key (rev) references public.revinfo;
alter table public.car_properties_aud
    add constraint FK_77d4kqid8lcg4sgcdcqf9g1ou foreign key (rev) references public.revinfo;
alter table public.car_properties
    add constraint FKpm4l686i678r82qjfba0btrr9 foreign key (car_id) references public.car;
alter table public.chat_entry_aud
    add constraint FK_lx4gb9ep6y81rwusn2bv71hiu foreign key (rev) references public.revinfo;
alter table public.companies_aud
    add constraint FK_b6dw8uukxx71vi2ufpri6t7eu foreign key (rev) references public.revinfo;
alter table public.companiesids_aud
    add constraint FKl6dqodhy3n3t3y14lksg3grdi foreign key (rev) references public.revinfo;
alter table public.company_addresses_aud
    add constraint FK56kpm8wxxadg0e3w9v61sbjfg foreign key (rev) references public.revinfo;
alter table public.company_delivery_area_delivery_area_zips_aud
    add constraint FK5br36wbtd7pxfxfpv53vri711 foreign key (rev) references public.revinfo;
alter table public.company_properties_aud
    add constraint FK_qff0qfi2cag1ihxeo0yximrdl foreign key (rev) references public.revinfo;
alter table public.company_addresses
    add constraint FK335qovqakfvmb4xwfumpbosbc foreign key (address_id) references public.addresses;
alter table public.company_addresses
    add constraint FK710v23c1e02xk6mheanqc6vc0 foreign key (company_id) references public.companies;
alter table public.company_delivery_area_delivery_area_zips
    add constraint FK6sqti8xgvxn92p74tfct2ufht foreign key (company_delivery_area_id) references public.companydeliveryareas;
alter table public.companydeliveryareas
    add constraint FK8nn95509ih6oegoncsqx75l8k foreign key (company_id) references public.companies;
alter table public.companydeliveryareas_aud
    add constraint FK_t60gqvkkx98ssl2tkocrhqjcj foreign key (rev) references public.revinfo;
alter table public.company_properties
    add constraint FK5858rpwqock78bho4945qm3od foreign key (company_id) references public.companies;
alter table public.contact_persons_aud
    add constraint FK_oupg309qkk3py9e0eyldehhgd foreign key (rev) references public.revinfo;
alter table public.cost_aud
    add constraint FK_srya84x4qwsh4eo2f2sg42rh5 foreign key (rev) references public.revinfo;
alter table public.cost_default_properties_aud
    add constraint FK_eiywd4nylcesex62i1hul1rk9 foreign key (rev) references public.revinfo;
alter table public.cost_properties_aud
    add constraint FK_pmjsc3l39p0c1ar0e0t36t2mw foreign key (rev) references public.revinfo;
alter table public.cost_default_properties
    add constraint FKcuk68kkqt526iw0hxv3lndj34 foreign key (company_id) references public.companies;
alter table public.cost_properties
    add constraint FKlrb8dj9ctp0vhpx2jh8va9gca foreign key (cost_id) references public.cost;
alter table public.customers_aud
    add constraint FK_r8uxpo71w06pfpw8orit5gfi6 foreign key (rev) references public.revinfo;
alter table public.default_sharing_rights_aud
    add constraint FKr5542bg3ij45ln1th8san9wnu foreign key (rev) references public.revinfo;
alter table public.delivery_methods_aud
    add constraint FK_i9t5verg9336wgbyv5ukn3ncv foreign key (rev) references public.revinfo;
alter table public.globalcompanyproperties_aud
    add constraint FK_lgjjgdj8ldo4usxvxhvcoj3p5 foreign key (rev) references public.revinfo;
alter table public.locations_aud
    add constraint FK_ilfonbkp0dx01t87thsofunst foreign key (rev) references public.revinfo;
alter table public.logger_aud
    add constraint FK_d3rp713h8xvk5exm4oeksjfld foreign key (rev) references public.revinfo;
alter table public.logger_properties
    add constraint FK3t6sla4ay0t6f3on6qhdt1pgb foreign key (properties_id) references public.logger_propeties;
alter table public.logger_properties
    add constraint FKq8gp3q56ccvubb5vadjepoi5m foreign key (logger_id) references public.logger;
alter table public.logger_properties_aud
    add constraint FKr9soukfut3f5f6p93ap2k2nja foreign key (rev) references public.revinfo;
alter table public.logger_propeties_aud
    add constraint FK_awmb0jslhrrlflnt4g05ypsu5 foreign key (rev) references public.revinfo;
alter table public.order_leg_aud
    add constraint FK_lha29hu0yr60k723m4h378k6q foreign key (rev) references public.revinfo;
alter table public.order_properties_aud
    add constraint FK_dq2atpg9n1roasgxoysl6u7kk foreign key (rev) references public.revinfo;
alter table public.order_route_aud
    add constraint FK_h9dstqi9o211ks82o86mi9jti foreign key (rev) references public.revinfo;
alter table public.order_route_order_legs_aud
    add constraint FKn2nb3kemlg1as1n36qmx8kcn5 foreign key (rev) references public.revinfo;
alter table public.order_type_properties_aud
    add constraint FK_nffcesre0177l46m65rgsaaih foreign key (rev) references public.revinfo;
alter table public.order_types_aud
    add constraint FK_q9c0sq68wbh8ahdqy1intgy17 foreign key (rev) references public.revinfo;
alter table public.order_types_order_type_properties_aud
    add constraint FKaadla3ykis722mki63b7em0q foreign key (rev) references public.revinfo;
alter table public.order_leg
    add constraint FK9lefifdodse1wwsw137np13qo foreign key (car_id) references public.car;
alter table public.order_leg
    add constraint FKfw9nejd3743w174vgx8dj6phr foreign key (order_route_id) references public.order_route;
alter table public.order_leg
    add constraint FKmc40kjqr64bflaob6b493vr4q foreign key (warehouse_id) references public.warehouse;
alter table public.order_properties
    add constraint FKjv386d2rt0atj5sy8htoh4d46 foreign key (order_id) references public.orders;
alter table public.order_route_order_legs
    add constraint FKg967rrmqjtykujrt379k3pclp foreign key (order_legs_id) references public.order_leg;
alter table public.order_route_order_legs
    add constraint FKa9tlnhb4x4plj2xd9hlkubp45 foreign key (order_route_id) references public.order_route;
alter table public.orders
    add constraint FKp990232wm71ge02dpjgx94l4f foreign key (address_billing_id) references public.addresses;
alter table public.orders
    add constraint FKq662334ghv0w1c33xk9hohq5r foreign key (address_from_id) references public.addresses;
alter table public.orders
    add constraint FKqle46huw6by7a8e56kx7uioho foreign key (address_to_id) references public.addresses;
alter table public.orders
    add constraint FK1vldikbqexeu85qvsedncxvs3 foreign key (company_id) references public.companies;
alter table public.orders
    add constraint FK9dcuobjbumc1i158oa50ugcj3 foreign key (contact_person_id) references public.contact_persons;
alter table public.orders
    add constraint FK596ikpxfxod0sb5y2w5sg1fdm foreign key (cost_id) references public.cost;
alter table public.orders
    add constraint FKmkr3t9h9m05ru9uhg30uv7p1o foreign key (delivery_method_id) references public.delivery_methods;
alter table public.orders
    add constraint FKakl1p7xiogdupq1376fttx2xc foreign key (parent_order_id) references public.orders;
alter table public.orders
    add constraint FK8aol9f99s97mtyhij0tvfj41f foreign key (payment_id) references public.payments;
alter table public.orders_aud
    add constraint FK_odf5gxpn0qh60ur00skx2xp5n foreign key (rev) references public.revinfo;
alter table public.orders_order_routes_aud
    add constraint FKta2pw8r6d1g2pqxvsy1ajle8u foreign key (rev) references public.revinfo;
alter table public.orders_order_types_aud
    add constraint FKl9hqny0ngf7u3sag8s0mfm7de foreign key (rev) references public.revinfo;
alter table public.orders_order_routes
    add constraint FKf1uto8avjneheqavudfq95sny foreign key (order_routes_id) references public.order_route;
alter table public.orders_order_routes
    add constraint FKjtfmpffo714yfx58m398s1oi0 foreign key (order_id) references public.orders;
alter table public.orders_order_types
    add constraint FKorsq9hkxo572pd6ivmhatys9l foreign key (order_types_id) references public.order_types;
alter table public.orders_order_types
    add constraint FKegmbvdjcn5dvu5thd2n19p3nn foreign key (order_id) references public.orders;
alter table public.orders_package_items_aud
    add constraint FKdi2qb3gotbn16hsupjumurk46 foreign key (rev) references public.revinfo;
alter table public.orders_package_items
    add constraint FKivuey7schspuup34mxj482il4 foreign key (package_items_id) references public.package_items;
alter table public.orders_package_items
    add constraint FKs57610pys5oscfi4m87sde3y5 foreign key (order_id) references public.orders;
alter table public.order_type_properties
    add constraint FKer93q8r3wkvu13sll90hlp04c foreign key (order_type_id) references public.order_types;
alter table public.order_types_order_type_properties
    add constraint FK77w7494lfe0njq9418yo6qacn foreign key (order_type_properties_id) references public.order_type_properties;
alter table public.order_types_order_type_properties
    add constraint FKslywgnf0rdcwfd7o7htdv3beq foreign key (order_type_id) references public.order_types;
alter table public.package_classes_aud
    add constraint FK_m5dvqom266uqnc4ald1cuuc9s foreign key (rev) references public.revinfo;
alter table public.package_items_aud
    add constraint FK_53n58q3psynwquf0otqcge96n foreign key (rev) references public.revinfo;
alter table public.package_package_properties_aud
    add constraint FK_as2x18jnn83ci0ysa4a0xahry foreign key (rev) references public.revinfo;
alter table public.package_properties_aud
    add constraint FK_ke7bnxwt8fuy4vfiyg9ghppwg foreign key (rev) references public.revinfo;
alter table public.package_routes_aud
    add constraint FK_epkfnoyrm76xt4inmsi0w6qk foreign key (rev) references public.revinfo;
alter table public.package_tracking_aud
    add constraint FK_c4iroyldi9ixqnw4l9ivmoay3 foreign key (rev) references public.revinfo;
alter table public.package_items
    add constraint FKr0eqviug6iscesa889ffy42c2 foreign key (package_class_id) references public.package_classes;
alter table public.package_package_properties
    add constraint FK2m2ucdrdphqfcp541f2x4okk7 foreign key (package_item_id) references public.package_items;
alter table public.package_properties
    add constraint FK26lc41jo29p722xotobqkmo2t foreign key (company_id) references public.companies;
alter table public.package_routes
    add constraint FKg89x9mpfgntfnnijj7dlb7xst foreign key (order_legs_id) references public.order_leg;
alter table public.package_routes
    add constraint FK4hxd8665os0c2c222mdhdd9jw foreign key (package_item_id) references public.package_items;
alter table public.package_tracking
    add constraint FK8i4w7gp0eovox3gwsh8nwikjn foreign key (car_id) references public.car;
alter table public.package_tracking
    add constraint FK66d60jb2e70jhb44vstv1lh43 foreign key (package_item_id) references public.package_items;
alter table public.package_tracking
    add constraint FK53vdkt4bhqbol1hbxqsb99kn foreign key (supplier_id) references public.users;
alter table public.package_tracking
    add constraint FK4dj0t3y5rfo7eqwrro149boh5 foreign key (warehouse_id) references public.warehouse;
alter table public.payment_default_properties_aud
    add constraint FK_tp0c5ss67t4xs4bwpnslkamv foreign key (rev) references public.revinfo;
alter table public.payment_properties_aud
    add constraint FK_f4wv98x51tqfns3wbpwnyachj foreign key (rev) references public.revinfo;
alter table public.payment_default_properties
    add constraint FK5gnsky105r4de9xhb4h16ptqv foreign key (company_id) references public.companies;
alter table public.payment_properties
    add constraint FKt7s9vf90md85h6325gs2ca98k foreign key (payment_id) references public.payments;
alter table public.payments_aud
    add constraint FK_f9ggfwabysa39nm2qk02ls8op foreign key (rev) references public.revinfo;
alter table public.user_properties_aud
    add constraint FK_nmj3nf8xfdcjafg3qxot57g5l foreign key (rev) references public.revinfo;
alter table public.user_properties
    add constraint FKrdwt651fwlcinjkrcfevgbulp foreign key (user_id) references public.users;
alter table public.users
    add constraint FKin8gn4o1hpiwe6qe4ey7ykwq7 foreign key (company_id) references public.companies;
alter table public.users_aud
    add constraint FK_nwv4fh3qmwdrk28v1uxgcfahd foreign key (rev) references public.revinfo;
alter table public.warehouse
    add constraint FKdy3p3bleikxqp19sy0y3pws3t foreign key (address_id) references public.addresses;
alter table public.warehouse_aud
    add constraint FK_kfafq60ltbpfbfmadf1ko0th0 foreign key (rev) references public.revinfo;
alter table public.warehouse_properties_aud
    add constraint FK_7jvpqbpfo8y3ly4x7j8byeh3p foreign key (rev) references public.revinfo;
alter table public.warehouse_properties
    add constraint FKijlcncsvxh60vs8twmnwvcgmb foreign key (warehouse_id) references public.warehouse;
create sequence IF NOT EXISTS public.hibernate_sequence start 1 increment 1;
create table IF NOT EXISTS public.addresses
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    location_point
    GEOMETRY,
    address_extra
    varchar
(
    255
), city varchar
(
    255
), client_name varchar
(
    255
), company_name varchar
(
    255
), country varchar
(
    255
), iso_code varchar
(
    255
), show_overview_filter varchar
(
    255
) DEFAULT 'SHOW', state varchar
(
    255
), street varchar
(
    255
), zip varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.addresses_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    location_point
    GEOMETRY,
    location_point_mod
    boolean,
    address_extra
    varchar
(
    255
), address_extra_mod boolean, city varchar
(
    255
), city_mod boolean, client_name varchar
(
    255
), client_name_mod boolean, company_name varchar
(
    255
), company_name_mod boolean, country varchar
(
    255
), country_mod boolean, iso_code varchar
(
    255
), iso_code_mod boolean, show_overview_filter varchar
(
    255
) DEFAULT 'SHOW', show_overview_filter_mod boolean, state varchar
(
    255
), state_mod boolean, street varchar
(
    255
), street_mod boolean, zip varchar
(
    255
), zip_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.car
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    capacity
    varchar
(
    255
), plate varchar
(
    255
), type varchar
(
    255
), weight varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.car_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    capacity
    varchar
(
    255
), capacity_mod boolean, plate varchar
(
    255
), plate_mod boolean, type varchar
(
    255
), type_mod boolean, weight varchar
(
    255
), weight_mod boolean, car_properties_mod boolean, locations_mod boolean, order_legs_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.car_locations
(
    car_id
    uuid
    not
    null,
    locations_id
    uuid
    not
    null,
    primary
    key
(
    car_id,
    locations_id
));
create table IF NOT EXISTS public.car_locations_aud
(
    rev
    int4
    not
    null,
    car_id
    uuid
    not
    null,
    locations_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    car_id,
    locations_id
));
create table IF NOT EXISTS public.car_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    varchar
(
    255
), key_mod boolean, type varchar
(
    255
), type_mod boolean, value varchar
(
    255
), value_mod boolean, car_id uuid, car_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.car_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    varchar
(
    255
), type varchar
(
    255
), value varchar
(
    255
), car_id uuid, primary key
(
    id
));
create table IF NOT EXISTS public.chat_entry_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    company_id
    uuid,
    company_id_mod
    boolean,
    order_id
    uuid,
    order_id_mod
    boolean,
    read_status
    BOOLEAN
    DEFAULT
    FALSE,
    read_status_mod
    boolean,
    sequence_id
    int8,
    sequence_id_mod
    boolean,
    text
    text,
    text_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.chat_entry
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    company_id
    uuid,
    order_id
    uuid,
    read_status
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    sequence_id
    int8,
    text
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.companies
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    name
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.companies_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    name
    text,
    name_mod
    boolean,
    company_addresses_mod
    boolean,
    company_properties_mod
    boolean,
    company_users_mod
    boolean,
    company_delivery_area_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.companiesids
(
    id
    uuid
    not
    null,
    companyoid
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.companiesids_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    companyoid
    uuid,
    companyoid_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.company_addresses_aud
(
    address_id
    uuid
    not
    null,
    company_id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    address_type
    varchar
(
    255
), address_type_mod boolean, address_mod boolean, company_mod boolean, primary key
(
    address_id,
    company_id,
    rev
));
create table IF NOT EXISTS public.company_delivery_area_delivery_area_zips_aud
(
    rev
    int4
    not
    null,
    company_delivery_area_id
    uuid
    not
    null,
    delivery_area_zips
    varchar
(
    255
) not null, revtype int2, primary key
(
    rev,
    company_delivery_area_id,
    delivery_area_zips
));
create table IF NOT EXISTS public.company_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.company_addresses
(
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    address_type
    varchar
(
    255
), company_id uuid not null, address_id uuid not null, primary key
(
    address_id,
    company_id
));
create table IF NOT EXISTS public.company_delivery_area_delivery_area_zips
(
    company_delivery_area_id
    uuid
    not
    null,
    delivery_area_zips
    varchar
(
    255
));
create table IF NOT EXISTS public.companydeliveryareas
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    delivery_area_geom
    GEOMETRY,
    delivery_area_polyline
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.companydeliveryareas_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    delivery_area_geom
    GEOMETRY,
    delivery_area_geom_mod
    boolean,
    delivery_area_polyline
    text,
    delivery_area_polyline_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    delivery_area_zips_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.company_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.contact_persons_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    company_id
    uuid,
    company_id_mod
    boolean,
    email
    varchar
(
    255
), email_mod boolean, name varchar
(
    255
), name_mod boolean, phone varchar
(
    255
), phone_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.contact_persons
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    company_id
    uuid,
    email
    varchar
(
    255
), name varchar
(
    255
), phone varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.cost
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    cost_sum
    float8,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.cost_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    cost_sum
    float8,
    cost_sum_mod
    boolean,
    cost_properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.cost_default_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    default_value
    text,
    default_value_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.cost_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    cost_id
    uuid,
    cost_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.cost_default_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_value
    text,
    key
    text,
    type
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.cost_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    cost_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.customers
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    address_id
    uuid,
    company_id
    uuid,
    email
    varchar
(
    255
), name varchar
(
    255
), tel varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.customers_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    address_id
    uuid,
    address_id_mod
    boolean,
    company_id
    uuid,
    company_id_mod
    boolean,
    email
    varchar
(
    255
), email_mod boolean, name varchar
(
    255
), name_mod boolean, tel varchar
(
    255
), tel_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.default_sharing_rights_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    default_sharing_rights
    text,
    default_sharing_rights_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.default_sharing_rights
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_sharing_rights
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.delivery_methods_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    delivery_method_name
    text,
    delivery_method_name_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.delivery_methods
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    delivery_method_name
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.globalcompanyproperties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    name
    varchar
(
    255
), type varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.globalcompanyproperties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    name
    varchar
(
    255
), name_mod boolean, type varchar
(
    255
), type_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.locations
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    location_point
    GEOMETRY,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.locations_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    location_point
    GEOMETRY,
    location_point_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.logger
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.logger_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.logger_properties
(
    logger_id
    uuid
    not
    null,
    properties_id
    uuid
    not
    null,
    primary
    key
(
    logger_id,
    properties_id
));
create table IF NOT EXISTS public.logger_properties_aud
(
    rev
    int4
    not
    null,
    logger_id
    uuid
    not
    null,
    properties_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    logger_id,
    properties_id
));
create table IF NOT EXISTS public.logger_propeties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    varchar
(
    255
), key_mod boolean, value varchar
(
    255
), value_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.logger_propeties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    varchar
(
    255
), value varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.order_leg_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    status
    varchar
(
    255
), status_mod boolean, type varchar
(
    255
), type_mod boolean, car_id uuid, car_mod boolean, order_route_id uuid, order_route_mod boolean, warehouse_id uuid, warehouse_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.order_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    order_id
    uuid,
    order_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.order_route_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    order_legs_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.order_route_order_legs_aud
(
    rev
    int4
    not
    null,
    order_route_id
    uuid
    not
    null,
    order_legs_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_route_id,
    order_legs_id
));
create table IF NOT EXISTS public.order_type_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    order_type_id
    uuid,
    order_type_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.order_types_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    typename
    varchar
(
    255
), typename_mod boolean, order_type_properties_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.order_types_order_type_properties_aud
(
    rev
    int4
    not
    null,
    order_type_id
    uuid
    not
    null,
    order_type_properties_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_type_id,
    order_type_properties_id
));
create table IF NOT EXISTS public.order_leg
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    status
    varchar
(
    255
), type varchar
(
    255
), car_id uuid, order_route_id uuid, warehouse_id uuid, primary key
(
    id
));
create table IF NOT EXISTS public.order_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    order_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.order_route
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.order_route_order_legs
(
    order_route_id
    uuid
    not
    null,
    order_legs_id
    uuid
    not
    null
);
create table IF NOT EXISTS public.orders
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    attention_flag
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    comment
    text,
    customer_id
    uuid,
    delivery_person
    text,
    delivery_timestamp
    timestamp,
    destination_date
    timestamp,
    id_string
    varchar
(
    255
), message_counter BIGINT DEFAULT 0 not null, new_order_id uuid, old_order_id uuid, order_alt_price double precision not null, order_status varchar
(
    255
), outsource_cost double precision not null, packages_price double precision not null, pick_up_date timestamp, price double precision not null, reason_for_cancel text, suborder_type BOOLEAN DEFAULT FALSE not null, address_billing_id uuid, address_from_id uuid, address_to_id uuid, company_id uuid, contact_person_id uuid, cost_id uuid, delivery_method_id uuid, parent_order_id uuid, payment_id uuid, primary key
(
    id
));
create table IF NOT EXISTS public.orders_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    attention_flag
    BOOLEAN
    DEFAULT
    FALSE,
    attention_flag_mod
    boolean,
    comment
    text,
    comment_mod
    boolean,
    customer_id
    uuid,
    customer_id_mod
    boolean,
    delivery_person
    text,
    delivery_person_mod
    boolean,
    delivery_timestamp
    timestamp,
    delivery_timestamp_mod
    boolean,
    destination_date
    timestamp,
    destination_date_mod
    boolean,
    id_string
    varchar
(
    255
), id_string_mod boolean, message_counter BIGINT DEFAULT 0, message_counter_mod boolean, new_order_id uuid, new_order_id_mod boolean, old_order_id uuid, old_order_id_mod boolean, order_alt_price double precision, order_alt_price_mod boolean, order_status varchar
(
    255
), order_status_mod boolean, outsource_cost double precision, outsource_cost_mod boolean, packages_price double precision, packages_price_mod boolean, pick_up_date timestamp, pick_up_date_mod boolean, price double precision, price_mod boolean, reason_for_cancel text, reason_for_cancel_mod boolean, suborder_type BOOLEAN DEFAULT FALSE, suborder_type_mod boolean, address_billing_id uuid, address_billing_mod boolean, address_from_id uuid, address_from_mod boolean, address_to_id uuid, address_to_mod boolean, company_id uuid, company_mod boolean, contact_person_id uuid, contact_person_mod boolean, cost_id uuid, cost_mod boolean, delivery_method_id uuid, delivery_method_mod boolean, order_properties_mod boolean, order_routes_mod boolean, order_types_mod boolean, package_items_mod boolean, parent_order_id uuid, parent_order_mod boolean, payment_id uuid, payment_mod boolean, suborders_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.orders_order_routes_aud
(
    rev
    int4
    not
    null,
    order_id
    uuid
    not
    null,
    order_routes_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_id,
    order_routes_id
));
create table IF NOT EXISTS public.orders_order_types_aud
(
    rev
    int4
    not
    null,
    order_id
    uuid
    not
    null,
    order_types_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_id,
    order_types_id
));
create table IF NOT EXISTS public.orders_order_routes
(
    order_id
    uuid
    not
    null,
    order_routes_id
    uuid
    not
    null
);
create table IF NOT EXISTS public.orders_order_types
(
    order_id
    uuid
    not
    null,
    order_types_id
    uuid
    not
    null,
    primary
    key
(
    order_id,
    order_types_id
));
create table IF NOT EXISTS public.orders_package_items_aud
(
    rev
    int4
    not
    null,
    order_id
    uuid
    not
    null,
    package_items_id
    uuid
    not
    null,
    revtype
    int2,
    primary
    key
(
    rev,
    order_id,
    package_items_id
));
create table IF NOT EXISTS public.orders_package_items
(
    order_id
    uuid
    not
    null,
    package_items_id
    uuid
    not
    null,
    primary
    key
(
    order_id,
    package_items_id
));
create table IF NOT EXISTS public.order_type_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    order_type_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.order_types
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    typename
    varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.order_types_order_type_properties
(
    order_type_id
    uuid
    not
    null,
    order_type_properties_id
    uuid
    not
    null,
    primary
    key
(
    order_type_id,
    order_type_properties_id
));
create table IF NOT EXISTS public.package_classes_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    class_name
    text,
    name_mod
    boolean,
    package_items_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.package_items_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    comment
    text,
    comment_mod
    boolean,
    deep_cm
    float8,
    deep_cm_mod
    boolean,
    explosive
    BOOLEAN
    DEFAULT
    FALSE,
    explosive_mod
    boolean,
    frost
    BOOLEAN
    DEFAULT
    FALSE,
    frost_mod
    boolean,
    height_cm
    float8,
    height_cm_mod
    boolean,
    package_price
    double
    precision,
    package_price_mod
    boolean,
    weight_kg
    float8,
    weight_kg_mod
    boolean,
    width_cm
    float8,
    width_cm_mod
    boolean,
    package_class_id
    uuid,
    package_class_mod
    boolean,
    package_package_properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.package_package_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    package_item_id
    uuid,
    package_item_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.package_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    default_value
    text,
    default_value_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.package_routes_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    order_legs_id
    uuid,
    order_legs_mod
    boolean,
    package_item_id
    uuid,
    package_item_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.package_tracking_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    comment
    text,
    comment_mod
    boolean,
    status
    varchar
(
    255
), status_mod boolean, car_id uuid, car_mod boolean, location_id uuid, location_mod boolean, package_item_id uuid, package_item_mod boolean, supplier_id uuid, supplier_mod boolean, warehouse_id uuid, warehouse_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.package_classes
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    class_name
    text,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.package_items
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    comment
    text,
    deep_cm
    float8
    not
    null,
    explosive
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    frost
    BOOLEAN
    DEFAULT
    FALSE
    not
    null,
    height_cm
    float8
    not
    null,
    package_price
    double
    precision
    not
    null,
    weight_kg
    float8
    not
    null,
    width_cm
    float8
    not
    null,
    package_class_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.package_package_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    package_item_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.package_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_value
    text,
    key
    text,
    type
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.package_routes
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    order_legs_id
    uuid,
    package_item_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.package_tracking
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    comment
    text,
    status
    varchar
(
    255
), car_id uuid, location_id uuid, package_item_id uuid, supplier_id uuid, warehouse_id uuid, primary key
(
    id
));
create table IF NOT EXISTS public.payment_default_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    default_value
    text,
    default_value_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.payment_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    payment_id
    uuid,
    payment_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.payment_default_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    default_value
    text,
    key
    text,
    type
    text,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.payment_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    payment_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.payments
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    amount
    float8,
    payment_status
    varchar
(
    255
), primary key
(
    id
));
create table IF NOT EXISTS public.payments_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    amount
    float8,
    amount_mod
    boolean,
    payment_status
    varchar
(
    255
), payment_status_mod boolean, payment_properties_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.revinfo
(
    rev
    int4
    not
    null,
    revtstmp
    int8,
    primary
    key
(
    rev
));
create table IF NOT EXISTS public.user_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    text,
    key_mod
    boolean,
    type
    text,
    type_mod
    boolean,
    value
    text,
    value_mod
    boolean,
    user_id
    uuid,
    user_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.user_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    text,
    type
    text,
    value
    text,
    user_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.users
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    job_position
    text,
    keycloak_email
    text,
    keycloak_id
    uuid,
    company_id
    uuid,
    primary
    key
(
    id
));
create table IF NOT EXISTS public.users_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    job_position
    text,
    job_position_mod
    boolean,
    keycloak_email
    text,
    keycloak_email_mod
    boolean,
    keycloak_id
    uuid,
    keycloak_id_mod
    boolean,
    company_id
    uuid,
    company_mod
    boolean,
    user_properties_mod
    boolean,
    primary
    key
(
    id,
    rev
));
create table IF NOT EXISTS public.warehouse
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    capacity
    int8,
    name
    varchar
(
    255
), address_id uuid, primary key
(
    id
));
create table IF NOT EXISTS public.warehouse_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    capacity
    int8,
    capacity_mod
    boolean,
    name
    varchar
(
    255
), name_mod boolean, address_id uuid, address_mod boolean, order_legs_mod boolean, warehouse_properties_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.warehouse_properties_aud
(
    id
    uuid
    not
    null,
    rev
    int4
    not
    null,
    revtype
    int2,
    create_date
    timestamp,
    create_date_mod
    boolean,
    created_by
    text,
    created_by_mod
    boolean,
    deleted
    boolean,
    deleted_mod
    boolean,
    last_modified_by
    text,
    last_modified_by_mod
    boolean,
    modify_date
    timestamp,
    modify_date_mod
    boolean,
    key
    varchar
(
    255
), key_mod boolean, type varchar
(
    255
), type_mod boolean, value varchar
(
    255
), value_mod boolean, warehouse_id uuid, warehouse_mod boolean, primary key
(
    id,
    rev
));
create table IF NOT EXISTS public.warehouse_properties
(
    id
    uuid
    not
    null,
    create_date
    timestamp
    not
    null,
    created_by
    text
    not
    null,
    deleted
    boolean
    not
    null,
    last_modified_by
    text
    not
    null,
    modify_date
    timestamp
    not
    null,
    key
    varchar
(
    255
), type varchar
(
    255
), value varchar
(
    255
), warehouse_id uuid, primary key
(
    id
));
alter table public.car_locations
    add constraint UK_hye6h6ubuah6ymwrf4m4ayd8j unique (locations_id);
create index IDXrm5m29soyrdwfb0b15g6e1gxv on public.companydeliveryareas (company_id);
alter table public.logger_properties
    add constraint UK_nu654s12mmfaci9i3tav44hw6 unique (properties_id);
alter table public.order_route_order_legs
    add constraint UK_n3m9tts6yr2f18t36esxdjpsj unique (order_legs_id);
alter table public.orders_order_routes
    add constraint UK_epjm8irae6syjss7dxvytuj1m unique (order_routes_id);
alter table public.orders_order_types
    add constraint UK_33ir2es3j8w0efhv9ui3wqari unique (order_types_id);
alter table public.order_types_order_type_properties
    add constraint UK_138wbd8l6u88u40x4kgmqu4p1 unique (order_type_properties_id);
alter table public.addresses_aud
    add constraint FK_slt1on64rkymbaiekchjf5te0 foreign key (rev) references public.revinfo;
alter table public.car_aud
    add constraint FK_1ntgpdadbifq2o1doy18fjho3 foreign key (rev) references public.revinfo;
alter table public.car_locations
    add constraint FK5xkpuwq37wu85aac45pl6a3gj foreign key (car_id) references public.car;
alter table public.car_locations_aud
    add constraint FKf6ht2pi8087dkuu8f4f604wvu foreign key (rev) references public.revinfo;
alter table public.car_properties_aud
    add constraint FK_77d4kqid8lcg4sgcdcqf9g1ou foreign key (rev) references public.revinfo;
alter table public.car_properties
    add constraint FKpm4l686i678r82qjfba0btrr9 foreign key (car_id) references public.car;
alter table public.chat_entry_aud
    add constraint FK_lx4gb9ep6y81rwusn2bv71hiu foreign key (rev) references public.revinfo;
alter table public.companies_aud
    add constraint FK_b6dw8uukxx71vi2ufpri6t7eu foreign key (rev) references public.revinfo;
alter table public.companiesids_aud
    add constraint FKl6dqodhy3n3t3y14lksg3grdi foreign key (rev) references public.revinfo;
alter table public.company_addresses_aud
    add constraint FK56kpm8wxxadg0e3w9v61sbjfg foreign key (rev) references public.revinfo;
alter table public.company_delivery_area_delivery_area_zips_aud
    add constraint FK5br36wbtd7pxfxfpv53vri711 foreign key (rev) references public.revinfo;
alter table public.company_properties_aud
    add constraint FK_qff0qfi2cag1ihxeo0yximrdl foreign key (rev) references public.revinfo;
alter table public.company_addresses
    add constraint FK335qovqakfvmb4xwfumpbosbc foreign key (address_id) references public.addresses;
alter table public.company_addresses
    add constraint FK710v23c1e02xk6mheanqc6vc0 foreign key (company_id) references public.companies;
alter table public.company_delivery_area_delivery_area_zips
    add constraint FK6sqti8xgvxn92p74tfct2ufht foreign key (company_delivery_area_id) references public.companydeliveryareas;
alter table public.companydeliveryareas
    add constraint FK8nn95509ih6oegoncsqx75l8k foreign key (company_id) references public.companies;
alter table public.companydeliveryareas_aud
    add constraint FK_t60gqvkkx98ssl2tkocrhqjcj foreign key (rev) references public.revinfo;
alter table public.company_properties
    add constraint FK5858rpwqock78bho4945qm3od foreign key (company_id) references public.companies;
alter table public.contact_persons_aud
    add constraint FK_oupg309qkk3py9e0eyldehhgd foreign key (rev) references public.revinfo;
alter table public.cost_aud
    add constraint FK_srya84x4qwsh4eo2f2sg42rh5 foreign key (rev) references public.revinfo;
alter table public.cost_default_properties_aud
    add constraint FK_eiywd4nylcesex62i1hul1rk9 foreign key (rev) references public.revinfo;
alter table public.cost_properties_aud
    add constraint FK_pmjsc3l39p0c1ar0e0t36t2mw foreign key (rev) references public.revinfo;
alter table public.cost_default_properties
    add constraint FKcuk68kkqt526iw0hxv3lndj34 foreign key (company_id) references public.companies;
alter table public.cost_properties
    add constraint FKlrb8dj9ctp0vhpx2jh8va9gca foreign key (cost_id) references public.cost;
alter table public.customers_aud
    add constraint FK_r8uxpo71w06pfpw8orit5gfi6 foreign key (rev) references public.revinfo;
alter table public.default_sharing_rights_aud
    add constraint FKr5542bg3ij45ln1th8san9wnu foreign key (rev) references public.revinfo;
alter table public.delivery_methods_aud
    add constraint FK_i9t5verg9336wgbyv5ukn3ncv foreign key (rev) references public.revinfo;
alter table public.globalcompanyproperties_aud
    add constraint FK_lgjjgdj8ldo4usxvxhvcoj3p5 foreign key (rev) references public.revinfo;
alter table public.locations_aud
    add constraint FK_ilfonbkp0dx01t87thsofunst foreign key (rev) references public.revinfo;
alter table public.logger_aud
    add constraint FK_d3rp713h8xvk5exm4oeksjfld foreign key (rev) references public.revinfo;
alter table public.logger_properties
    add constraint FK3t6sla4ay0t6f3on6qhdt1pgb foreign key (properties_id) references public.logger_propeties;
alter table public.logger_properties
    add constraint FKq8gp3q56ccvubb5vadjepoi5m foreign key (logger_id) references public.logger;
alter table public.logger_properties_aud
    add constraint FKr9soukfut3f5f6p93ap2k2nja foreign key (rev) references public.revinfo;
alter table public.logger_propeties_aud
    add constraint FK_awmb0jslhrrlflnt4g05ypsu5 foreign key (rev) references public.revinfo;
alter table public.order_leg_aud
    add constraint FK_lha29hu0yr60k723m4h378k6q foreign key (rev) references public.revinfo;
alter table public.order_properties_aud
    add constraint FK_dq2atpg9n1roasgxoysl6u7kk foreign key (rev) references public.revinfo;
alter table public.order_route_aud
    add constraint FK_h9dstqi9o211ks82o86mi9jti foreign key (rev) references public.revinfo;
alter table public.order_route_order_legs_aud
    add constraint FKn2nb3kemlg1as1n36qmx8kcn5 foreign key (rev) references public.revinfo;
alter table public.order_type_properties_aud
    add constraint FK_nffcesre0177l46m65rgsaaih foreign key (rev) references public.revinfo;
alter table public.order_types_aud
    add constraint FK_q9c0sq68wbh8ahdqy1intgy17 foreign key (rev) references public.revinfo;
alter table public.order_types_order_type_properties_aud
    add constraint FKaadla3ykis722mki63b7em0q foreign key (rev) references public.revinfo;
alter table public.order_leg
    add constraint FK9lefifdodse1wwsw137np13qo foreign key (car_id) references public.car;
alter table public.order_leg
    add constraint FKfw9nejd3743w174vgx8dj6phr foreign key (order_route_id) references public.order_route;
alter table public.order_leg
    add constraint FKmc40kjqr64bflaob6b493vr4q foreign key (warehouse_id) references public.warehouse;
alter table public.order_properties
    add constraint FKjv386d2rt0atj5sy8htoh4d46 foreign key (order_id) references public.orders;
alter table public.order_route_order_legs
    add constraint FKg967rrmqjtykujrt379k3pclp foreign key (order_legs_id) references public.order_leg;
alter table public.order_route_order_legs
    add constraint FKa9tlnhb4x4plj2xd9hlkubp45 foreign key (order_route_id) references public.order_route;
alter table public.orders
    add constraint FKp990232wm71ge02dpjgx94l4f foreign key (address_billing_id) references public.addresses;
alter table public.orders
    add constraint FKq662334ghv0w1c33xk9hohq5r foreign key (address_from_id) references public.addresses;
alter table public.orders
    add constraint FKqle46huw6by7a8e56kx7uioho foreign key (address_to_id) references public.addresses;
alter table public.orders
    add constraint FK1vldikbqexeu85qvsedncxvs3 foreign key (company_id) references public.companies;
alter table public.orders
    add constraint FK9dcuobjbumc1i158oa50ugcj3 foreign key (contact_person_id) references public.contact_persons;
alter table public.orders
    add constraint FK596ikpxfxod0sb5y2w5sg1fdm foreign key (cost_id) references public.cost;
alter table public.orders
    add constraint FKmkr3t9h9m05ru9uhg30uv7p1o foreign key (delivery_method_id) references public.delivery_methods;
alter table public.orders
    add constraint FKakl1p7xiogdupq1376fttx2xc foreign key (parent_order_id) references public.orders;
alter table public.orders
    add constraint FK8aol9f99s97mtyhij0tvfj41f foreign key (payment_id) references public.payments;
alter table public.orders_aud
    add constraint FK_odf5gxpn0qh60ur00skx2xp5n foreign key (rev) references public.revinfo;
alter table public.orders_order_routes_aud
    add constraint FKta2pw8r6d1g2pqxvsy1ajle8u foreign key (rev) references public.revinfo;
alter table public.orders_order_types_aud
    add constraint FKl9hqny0ngf7u3sag8s0mfm7de foreign key (rev) references public.revinfo;
alter table public.orders_order_routes
    add constraint FKf1uto8avjneheqavudfq95sny foreign key (order_routes_id) references public.order_route;
alter table public.orders_order_routes
    add constraint FKjtfmpffo714yfx58m398s1oi0 foreign key (order_id) references public.orders;
alter table public.orders_order_types
    add constraint FKorsq9hkxo572pd6ivmhatys9l foreign key (order_types_id) references public.order_types;
alter table public.orders_order_types
    add constraint FKegmbvdjcn5dvu5thd2n19p3nn foreign key (order_id) references public.orders;
alter table public.orders_package_items_aud
    add constraint FKdi2qb3gotbn16hsupjumurk46 foreign key (rev) references public.revinfo;
alter table public.orders_package_items
    add constraint FKivuey7schspuup34mxj482il4 foreign key (package_items_id) references public.package_items;
alter table public.orders_package_items
    add constraint FKs57610pys5oscfi4m87sde3y5 foreign key (order_id) references public.orders;
alter table public.order_type_properties
    add constraint FKer93q8r3wkvu13sll90hlp04c foreign key (order_type_id) references public.order_types;
alter table public.order_types_order_type_properties
    add constraint FK77w7494lfe0njq9418yo6qacn foreign key (order_type_properties_id) references public.order_type_properties;
alter table public.order_types_order_type_properties
    add constraint FKslywgnf0rdcwfd7o7htdv3beq foreign key (order_type_id) references public.order_types;
alter table public.package_classes_aud
    add constraint FK_m5dvqom266uqnc4ald1cuuc9s foreign key (rev) references public.revinfo;
alter table public.package_items_aud
    add constraint FK_53n58q3psynwquf0otqcge96n foreign key (rev) references public.revinfo;
alter table public.package_package_properties_aud
    add constraint FK_as2x18jnn83ci0ysa4a0xahry foreign key (rev) references public.revinfo;
alter table public.package_properties_aud
    add constraint FK_ke7bnxwt8fuy4vfiyg9ghppwg foreign key (rev) references public.revinfo;
alter table public.package_routes_aud
    add constraint FK_epkfnoyrm76xt4inmsi0w6qk foreign key (rev) references public.revinfo;
alter table public.package_tracking_aud
    add constraint FK_c4iroyldi9ixqnw4l9ivmoay3 foreign key (rev) references public.revinfo;
alter table public.package_items
    add constraint FKr0eqviug6iscesa889ffy42c2 foreign key (package_class_id) references public.package_classes;
alter table public.package_package_properties
    add constraint FK2m2ucdrdphqfcp541f2x4okk7 foreign key (package_item_id) references public.package_items;
alter table public.package_properties
    add constraint FK26lc41jo29p722xotobqkmo2t foreign key (company_id) references public.companies;
alter table public.package_routes
    add constraint FKg89x9mpfgntfnnijj7dlb7xst foreign key (order_legs_id) references public.order_leg;
alter table public.package_routes
    add constraint FK4hxd8665os0c2c222mdhdd9jw foreign key (package_item_id) references public.package_items;
alter table public.package_tracking
    add constraint FK8i4w7gp0eovox3gwsh8nwikjn foreign key (car_id) references public.car;
alter table public.package_tracking
    add constraint FK66d60jb2e70jhb44vstv1lh43 foreign key (package_item_id) references public.package_items;
alter table public.package_tracking
    add constraint FK53vdkt4bhqbol1hbxqsb99kn foreign key (supplier_id) references public.users;
alter table public.package_tracking
    add constraint FK4dj0t3y5rfo7eqwrro149boh5 foreign key (warehouse_id) references public.warehouse;
alter table public.payment_default_properties_aud
    add constraint FK_tp0c5ss67t4xs4bwpnslkamv foreign key (rev) references public.revinfo;
alter table public.payment_properties_aud
    add constraint FK_f4wv98x51tqfns3wbpwnyachj foreign key (rev) references public.revinfo;
alter table public.payment_default_properties
    add constraint FK5gnsky105r4de9xhb4h16ptqv foreign key (company_id) references public.companies;
alter table public.payment_properties
    add constraint FKt7s9vf90md85h6325gs2ca98k foreign key (payment_id) references public.payments;
alter table public.payments_aud
    add constraint FK_f9ggfwabysa39nm2qk02ls8op foreign key (rev) references public.revinfo;
alter table public.user_properties_aud
    add constraint FK_nmj3nf8xfdcjafg3qxot57g5l foreign key (rev) references public.revinfo;
alter table public.user_properties
    add constraint FKrdwt651fwlcinjkrcfevgbulp foreign key (user_id) references public.users;
alter table public.users
    add constraint FKin8gn4o1hpiwe6qe4ey7ykwq7 foreign key (company_id) references public.companies;
alter table public.users_aud
    add constraint FK_nwv4fh3qmwdrk28v1uxgcfahd foreign key (rev) references public.revinfo;
alter table public.warehouse
    add constraint FKdy3p3bleikxqp19sy0y3pws3t foreign key (address_id) references public.addresses;
alter table public.warehouse_aud
    add constraint FK_kfafq60ltbpfbfmadf1ko0th0 foreign key (rev) references public.revinfo;
alter table public.warehouse_properties_aud
    add constraint FK_7jvpqbpfo8y3ly4x7j8byeh3p foreign key (rev) references public.revinfo;
alter table public.warehouse_properties
    add constraint FKijlcncsvxh60vs8twmnwvcgmb foreign key (warehouse_id) references public.warehouse;
create sequence public.hibernate_sequence start 1 increment 1;
create table public.addresses
(
    id                   uuid      not null,
    create_date          timestamp not null,
    created_by           text      not null,
    deleted              boolean   not null,
    last_modified_by     text      not null,
    modify_date          timestamp not null,
    location_point       GEOMETRY,
    address_extra        varchar(255),
    city                 varchar(255),
    client_name          varchar(255),
    company_name         varchar(255),
    country              varchar(255),
    iso_code             varchar(255),
    show_overview_filter varchar(255) DEFAULT 'SHOW',
    state                varchar(255),
    street               varchar(255),
    zip                  varchar(255),
    primary key (id)
);
create table public.addresses_aud
(
    id                       uuid not null,
    rev                      int4 not null,
    revtype                  int2,
    create_date              timestamp,
    create_date_mod          boolean,
    created_by               text,
    created_by_mod           boolean,
    deleted                  boolean,
    deleted_mod              boolean,
    last_modified_by         text,
    last_modified_by_mod     boolean,
    modify_date              timestamp,
    modify_date_mod          boolean,
    location_point           GEOMETRY,
    location_point_mod       boolean,
    address_extra            varchar(255),
    address_extra_mod        boolean,
    city                     varchar(255),
    city_mod                 boolean,
    client_name              varchar(255),
    client_name_mod          boolean,
    company_name             varchar(255),
    company_name_mod         boolean,
    country                  varchar(255),
    country_mod              boolean,
    iso_code                 varchar(255),
    iso_code_mod             boolean,
    show_overview_filter     varchar(255) DEFAULT 'SHOW',
    show_overview_filter_mod boolean,
    state                    varchar(255),
    state_mod                boolean,
    street                   varchar(255),
    street_mod               boolean,
    zip                      varchar(255),
    zip_mod                  boolean,
    primary key (id, rev)
);
create table public.car
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    capacity         varchar(255),
    plate            varchar(255),
    type             varchar(255),
    weight           varchar(255),
    primary key (id)
);
create table public.car_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    capacity             varchar(255),
    capacity_mod         boolean,
    plate                varchar(255),
    plate_mod            boolean,
    type                 varchar(255),
    type_mod             boolean,
    weight               varchar(255),
    weight_mod           boolean,
    car_properties_mod   boolean,
    locations_mod        boolean,
    order_legs_mod       boolean,
    primary key (id, rev)
);
create table public.car_locations
(
    car_id       uuid not null,
    locations_id uuid not null,
    primary key (car_id, locations_id)
);
create table public.car_locations_aud
(
    rev          int4 not null,
    car_id       uuid not null,
    locations_id uuid not null,
    revtype      int2,
    primary key (rev, car_id, locations_id)
);
create table public.car_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  varchar(255),
    key_mod              boolean,
    type                 varchar(255),
    type_mod             boolean,
    value                varchar(255),
    value_mod            boolean,
    car_id               uuid,
    car_mod              boolean,
    primary key (id, rev)
);
create table public.car_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              varchar(255),
    type             varchar(255),
    value            varchar(255),
    car_id           uuid,
    primary key (id)
);
create table public.chat_entry_aud
(
    id                     uuid not null,
    rev                    int4 not null,
    revtype                int2,
    create_date            timestamp,
    create_date_mod        boolean,
    created_by             text,
    created_by_mod         boolean,
    deleted                boolean,
    deleted_mod            boolean,
    last_modified_by       text,
    last_modified_by_mod   boolean,
    modify_date            timestamp,
    modify_date_mod        boolean,
    company_id             uuid,
    company_id_mod         boolean,
    email_send_already     BOOLEAN DEFAULT FALSE,
    email_send_already_mod boolean,
    order_id               uuid,
    order_id_mod           boolean,
    read_status            BOOLEAN DEFAULT FALSE,
    read_status_mod        boolean,
    sequence_id            int8,
    sequence_id_mod        boolean,
    text                   text,
    text_mod               boolean,
    primary key (id, rev)
);
create table public.chat_entry
(
    id                 uuid                  not null,
    create_date        timestamp             not null,
    created_by         text                  not null,
    deleted            boolean               not null,
    last_modified_by   text                  not null,
    modify_date        timestamp             not null,
    company_id         uuid,
    email_send_already BOOLEAN DEFAULT FALSE not null,
    order_id           uuid,
    read_status        BOOLEAN DEFAULT FALSE not null,
    sequence_id        int8,
    text               text,
    primary key (id)
);
create table public.companies
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    name             text,
    primary key (id)
);
create table public.companies_aud
(
    id                        uuid not null,
    rev                       int4 not null,
    revtype                   int2,
    create_date               timestamp,
    create_date_mod           boolean,
    created_by                text,
    created_by_mod            boolean,
    deleted                   boolean,
    deleted_mod               boolean,
    last_modified_by          text,
    last_modified_by_mod      boolean,
    modify_date               timestamp,
    modify_date_mod           boolean,
    name                      text,
    name_mod                  boolean,
    company_addresses_mod     boolean,
    company_properties_mod    boolean,
    company_users_mod         boolean,
    company_delivery_area_mod boolean,
    primary key (id, rev)
);
create table public.companiesids
(
    id         uuid not null,
    companyoid uuid,
    primary key (id)
);
create table public.companiesids_aud
(
    id             uuid not null,
    rev            int4 not null,
    revtype        int2,
    companyoid     uuid,
    companyoid_mod boolean,
    primary key (id, rev)
);
create table public.company_addresses_aud
(
    address_id       uuid not null,
    company_id       uuid not null,
    rev              int4 not null,
    revtype          int2,
    address_type     varchar(255),
    address_type_mod boolean,
    address_mod      boolean,
    company_mod      boolean,
    primary key (address_id, company_id, rev)
);
create table public.company_delivery_area_delivery_area_zips_aud
(
    rev                      int4         not null,
    company_delivery_area_id uuid         not null,
    delivery_area_zips       varchar(255) not null,
    revtype                  int2,
    primary key (rev, company_delivery_area_id, delivery_area_zips)
);
create table public.company_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    value                text,
    value_mod            boolean,
    company_id           uuid,
    company_mod          boolean,
    primary key (id, rev)
);
create table public.company_addresses
(
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    address_type     varchar(255),
    company_id       uuid      not null,
    address_id       uuid      not null,
    primary key (address_id, company_id)
);
create table public.company_delivery_area_delivery_area_zips
(
    company_delivery_area_id uuid not null,
    delivery_area_zips       varchar(255)
);
create table public.companydeliveryareas
(
    id                     uuid      not null,
    create_date            timestamp not null,
    created_by             text      not null,
    deleted                boolean   not null,
    last_modified_by       text      not null,
    modify_date            timestamp not null,
    delivery_area_geom     GEOMETRY,
    delivery_area_polyline text,
    company_id             uuid,
    primary key (id)
);
create table public.companydeliveryareas_aud
(
    id                         uuid not null,
    rev                        int4 not null,
    revtype                    int2,
    create_date                timestamp,
    create_date_mod            boolean,
    created_by                 text,
    created_by_mod             boolean,
    deleted                    boolean,
    deleted_mod                boolean,
    last_modified_by           text,
    last_modified_by_mod       boolean,
    modify_date                timestamp,
    modify_date_mod            boolean,
    delivery_area_geom         GEOMETRY,
    delivery_area_geom_mod     boolean,
    delivery_area_polyline     text,
    delivery_area_polyline_mod boolean,
    company_id                 uuid,
    company_mod                boolean,
    delivery_area_zips_mod     boolean,
    primary key (id, rev)
);
create table public.company_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              text,
    type             text,
    value            text,
    company_id       uuid,
    primary key (id)
);
create table public.contact_persons_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    company_id           uuid,
    company_id_mod       boolean,
    email                varchar(255),
    email_mod            boolean,
    name                 varchar(255),
    name_mod             boolean,
    phone                varchar(255),
    phone_mod            boolean,
    primary key (id, rev)
);
create table public.contact_persons
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    company_id       uuid,
    email            varchar(255),
    name             varchar(255),
    phone            varchar(255),
    primary key (id)
);
create table public.cost
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    cost_sum         float8,
    primary key (id)
);
create table public.cost_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    cost_sum             float8,
    cost_sum_mod         boolean,
    cost_properties_mod  boolean,
    primary key (id, rev)
);
create table public.cost_default_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    default_value        text,
    default_value_mod    boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    company_id           uuid,
    company_mod          boolean,
    primary key (id, rev)
);
create table public.cost_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    value                text,
    value_mod            boolean,
    cost_id              uuid,
    cost_mod             boolean,
    primary key (id, rev)
);
create table public.cost_default_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    default_value    text,
    key              text,
    type             text,
    company_id       uuid,
    primary key (id)
);
create table public.cost_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              text,
    type             text,
    value            text,
    cost_id          uuid,
    primary key (id)
);
create table public.customers
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    address_id       uuid,
    company_id       uuid,
    email            varchar(255),
    name             varchar(255),
    tel              varchar(255),
    primary key (id)
);
create table public.customers_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    address_id           uuid,
    address_id_mod       boolean,
    company_id           uuid,
    company_id_mod       boolean,
    email                varchar(255),
    email_mod            boolean,
    name                 varchar(255),
    name_mod             boolean,
    tel                  varchar(255),
    tel_mod              boolean,
    primary key (id, rev)
);
create table public.default_sharing_rights_aud
(
    id                         uuid not null,
    rev                        int4 not null,
    revtype                    int2,
    default_sharing_rights     text,
    default_sharing_rights_mod boolean,
    primary key (id, rev)
);
create table public.default_sharing_rights
(
    id                     uuid      not null,
    create_date            timestamp not null,
    created_by             text      not null,
    deleted                boolean   not null,
    last_modified_by       text      not null,
    modify_date            timestamp not null,
    default_sharing_rights text,
    primary key (id)
);
create table public.delivery_methods_aud
(
    id                       uuid not null,
    rev                      int4 not null,
    revtype                  int2,
    create_date              timestamp,
    create_date_mod          boolean,
    created_by               text,
    created_by_mod           boolean,
    deleted                  boolean,
    deleted_mod              boolean,
    last_modified_by         text,
    last_modified_by_mod     boolean,
    modify_date              timestamp,
    modify_date_mod          boolean,
    delivery_method_name     text,
    delivery_method_name_mod boolean,
    primary key (id, rev)
);
create table public.delivery_methods
(
    id                   uuid      not null,
    create_date          timestamp not null,
    created_by           text      not null,
    deleted              boolean   not null,
    last_modified_by     text      not null,
    modify_date          timestamp not null,
    delivery_method_name text,
    primary key (id)
);
create table public.globalcompanyproperties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    name             varchar(255),
    type             varchar(255),
    primary key (id)
);
create table public.globalcompanyproperties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    name                 varchar(255),
    name_mod             boolean,
    type                 varchar(255),
    type_mod             boolean,
    primary key (id, rev)
);
create table public.locations
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    location_point   GEOMETRY,
    primary key (id)
);
create table public.locations_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    location_point       GEOMETRY,
    location_point_mod   boolean,
    primary key (id, rev)
);
create table public.logger
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    primary key (id)
);
create table public.logger_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    properties_mod       boolean,
    primary key (id, rev)
);
create table public.logger_properties
(
    logger_id     uuid not null,
    properties_id uuid not null,
    primary key (logger_id, properties_id)
);
create table public.logger_properties_aud
(
    rev           int4 not null,
    logger_id     uuid not null,
    properties_id uuid not null,
    revtype       int2,
    primary key (rev, logger_id, properties_id)
);
create table public.logger_propeties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  varchar(255),
    key_mod              boolean,
    value                varchar(255),
    value_mod            boolean,
    primary key (id, rev)
);
create table public.logger_propeties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              varchar(255),
    value            varchar(255),
    primary key (id)
);
create table public.order_leg_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    status               varchar(255),
    status_mod           boolean,
    type                 varchar(255),
    type_mod             boolean,
    car_id               uuid,
    car_mod              boolean,
    order_route_id       uuid,
    order_route_mod      boolean,
    warehouse_id         uuid,
    warehouse_mod        boolean,
    primary key (id, rev)
);
create table public.order_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    value                text,
    value_mod            boolean,
    order_id             uuid,
    order_mod            boolean,
    primary key (id, rev)
);
create table public.order_route_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    order_legs_mod       boolean,
    primary key (id, rev)
);
create table public.order_route_order_legs_aud
(
    rev            int4 not null,
    order_route_id uuid not null,
    order_legs_id  uuid not null,
    revtype        int2,
    primary key (rev, order_route_id, order_legs_id)
);
create table public.order_type_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    value                text,
    value_mod            boolean,
    order_type_id        uuid,
    order_type_mod       boolean,
    primary key (id, rev)
);
create table public.order_types_aud
(
    id                        uuid not null,
    rev                       int4 not null,
    revtype                   int2,
    create_date               timestamp,
    create_date_mod           boolean,
    created_by                text,
    created_by_mod            boolean,
    deleted                   boolean,
    deleted_mod               boolean,
    last_modified_by          text,
    last_modified_by_mod      boolean,
    modify_date               timestamp,
    modify_date_mod           boolean,
    typename                  varchar(255),
    typename_mod              boolean,
    order_type_properties_mod boolean,
    primary key (id, rev)
);
create table public.order_types_order_type_properties_aud
(
    rev                      int4 not null,
    order_type_id            uuid not null,
    order_type_properties_id uuid not null,
    revtype                  int2,
    primary key (rev, order_type_id, order_type_properties_id)
);
create table public.order_leg
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    status           varchar(255),
    type             varchar(255),
    car_id           uuid,
    order_route_id   uuid,
    warehouse_id     uuid,
    primary key (id)
);
create table public.order_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              text,
    type             text,
    value            text,
    order_id         uuid,
    primary key (id)
);
create table public.order_route
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    primary key (id)
);
create table public.order_route_order_legs
(
    order_route_id uuid not null,
    order_legs_id  uuid not null
);
create table public.orders
(
    id                 uuid                  not null,
    create_date        timestamp             not null,
    created_by         text                  not null,
    deleted            boolean               not null,
    last_modified_by   text                  not null,
    modify_date        timestamp             not null,
    attention_flag     BOOLEAN DEFAULT FALSE not null,
    comment            text,
    customer_id        uuid,
    delivery_person    text,
    delivery_timestamp timestamp,
    destination_date   timestamp,
    id_string          varchar(255),
    message_counter    BIGINT  DEFAULT 0     not null,
    new_order_id       uuid,
    old_order_id       uuid,
    order_alt_price    double precision      not null,
    order_status       varchar(255),
    outsource_cost     double precision      not null,
    packages_price     double precision      not null,
    pick_up_date       timestamp,
    price              double precision      not null,
    reason_for_cancel  text,
    suborder_type      BOOLEAN DEFAULT FALSE not null,
    address_billing_id uuid,
    address_from_id    uuid,
    address_to_id      uuid,
    company_id         uuid,
    contact_person_id  uuid,
    cost_id            uuid,
    delivery_method_id uuid,
    parent_order_id    uuid,
    payment_id         uuid,
    primary key (id)
);
create table public.orders_aud
(
    id                     uuid not null,
    rev                    int4 not null,
    revtype                int2,
    create_date            timestamp,
    create_date_mod        boolean,
    created_by             text,
    created_by_mod         boolean,
    deleted                boolean,
    deleted_mod            boolean,
    last_modified_by       text,
    last_modified_by_mod   boolean,
    modify_date            timestamp,
    modify_date_mod        boolean,
    attention_flag         BOOLEAN DEFAULT FALSE,
    attention_flag_mod     boolean,
    comment                text,
    comment_mod            boolean,
    customer_id            uuid,
    customer_id_mod        boolean,
    delivery_person        text,
    delivery_person_mod    boolean,
    delivery_timestamp     timestamp,
    delivery_timestamp_mod boolean,
    destination_date       timestamp,
    destination_date_mod   boolean,
    id_string              varchar(255),
    id_string_mod          boolean,
    message_counter        BIGINT  DEFAULT 0,
    message_counter_mod    boolean,
    new_order_id           uuid,
    new_order_id_mod       boolean,
    old_order_id           uuid,
    old_order_id_mod       boolean,
    order_alt_price        double precision,
    order_alt_price_mod    boolean,
    order_status           varchar(255),
    order_status_mod       boolean,
    outsource_cost         double precision,
    outsource_cost_mod     boolean,
    packages_price         double precision,
    packages_price_mod     boolean,
    pick_up_date           timestamp,
    pick_up_date_mod       boolean,
    price                  double precision,
    price_mod              boolean,
    reason_for_cancel      text,
    reason_for_cancel_mod  boolean,
    suborder_type          BOOLEAN DEFAULT FALSE,
    suborder_type_mod      boolean,
    address_billing_id     uuid,
    address_billing_mod    boolean,
    address_from_id        uuid,
    address_from_mod       boolean,
    address_to_id          uuid,
    address_to_mod         boolean,
    company_id             uuid,
    company_mod            boolean,
    contact_person_id      uuid,
    contact_person_mod     boolean,
    cost_id                uuid,
    cost_mod               boolean,
    delivery_method_id     uuid,
    delivery_method_mod    boolean,
    order_properties_mod   boolean,
    order_routes_mod       boolean,
    order_types_mod        boolean,
    package_items_mod      boolean,
    parent_order_id        uuid,
    parent_order_mod       boolean,
    payment_id             uuid,
    payment_mod            boolean,
    suborders_mod          boolean,
    primary key (id, rev)
);
create table public.orders_order_routes_aud
(
    rev             int4 not null,
    order_id        uuid not null,
    order_routes_id uuid not null,
    revtype         int2,
    primary key (rev, order_id, order_routes_id)
);
create table public.orders_order_types_aud
(
    rev            int4 not null,
    order_id       uuid not null,
    order_types_id uuid not null,
    revtype        int2,
    primary key (rev, order_id, order_types_id)
);
create table public.orders_order_routes
(
    order_id        uuid not null,
    order_routes_id uuid not null
);
create table public.orders_order_types
(
    order_id       uuid not null,
    order_types_id uuid not null,
    primary key (order_id, order_types_id)
);
create table public.orders_package_items_aud
(
    rev              int4 not null,
    order_id         uuid not null,
    package_items_id uuid not null,
    revtype          int2,
    primary key (rev, order_id, package_items_id)
);
create table public.orders_package_items
(
    order_id         uuid not null,
    package_items_id uuid not null,
    primary key (order_id, package_items_id)
);
create table public.order_type_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              text,
    type             text,
    value            text,
    order_type_id    uuid,
    primary key (id)
);
create table public.order_types
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    typename         varchar(255),
    primary key (id)
);
create table public.order_types_order_type_properties
(
    order_type_id            uuid not null,
    order_type_properties_id uuid not null,
    primary key (order_type_id, order_type_properties_id)
);
create table public.package_classes_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    class_name           text,
    name_mod             boolean,
    package_items_mod    boolean,
    primary key (id, rev)
);
create table public.package_items_aud
(
    id                             uuid not null,
    rev                            int4 not null,
    revtype                        int2,
    create_date                    timestamp,
    create_date_mod                boolean,
    created_by                     text,
    created_by_mod                 boolean,
    deleted                        boolean,
    deleted_mod                    boolean,
    last_modified_by               text,
    last_modified_by_mod           boolean,
    modify_date                    timestamp,
    modify_date_mod                boolean,
    comment                        text,
    comment_mod                    boolean,
    deep_cm                        float8,
    deep_cm_mod                    boolean,
    explosive                      BOOLEAN DEFAULT FALSE,
    explosive_mod                  boolean,
    frost                          BOOLEAN DEFAULT FALSE,
    frost_mod                      boolean,
    height_cm                      float8,
    height_cm_mod                  boolean,
    package_price                  double precision,
    package_price_mod              boolean,
    weight_kg                      float8,
    weight_kg_mod                  boolean,
    width_cm                       float8,
    width_cm_mod                   boolean,
    package_class_id               uuid,
    package_class_mod              boolean,
    package_package_properties_mod boolean,
    primary key (id, rev)
);
create table public.package_package_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    value                text,
    value_mod            boolean,
    package_item_id      uuid,
    package_item_mod     boolean,
    primary key (id, rev)
);
create table public.package_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    default_value        text,
    default_value_mod    boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    company_id           uuid,
    company_mod          boolean,
    primary key (id, rev)
);
create table public.package_routes_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    order_legs_id        uuid,
    order_legs_mod       boolean,
    package_item_id      uuid,
    package_item_mod     boolean,
    primary key (id, rev)
);
create table public.package_tracking_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    comment              text,
    comment_mod          boolean,
    status               varchar(255),
    status_mod           boolean,
    car_id               uuid,
    car_mod              boolean,
    location_id          uuid,
    location_mod         boolean,
    package_item_id      uuid,
    package_item_mod     boolean,
    supplier_id          uuid,
    supplier_mod         boolean,
    warehouse_id         uuid,
    warehouse_mod        boolean,
    primary key (id, rev)
);
create table public.package_classes
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    class_name       text,
    primary key (id)
);
create table public.package_items
(
    id               uuid                  not null,
    create_date      timestamp             not null,
    created_by       text                  not null,
    deleted          boolean               not null,
    last_modified_by text                  not null,
    modify_date      timestamp             not null,
    comment          text,
    deep_cm          float8                not null,
    explosive        BOOLEAN DEFAULT FALSE not null,
    frost            BOOLEAN DEFAULT FALSE not null,
    height_cm        float8                not null,
    package_price    double precision      not null,
    weight_kg        float8                not null,
    width_cm         float8                not null,
    package_class_id uuid,
    primary key (id)
);
create table public.package_package_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              text,
    type             text,
    value            text,
    package_item_id  uuid,
    primary key (id)
);
create table public.package_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    default_value    text,
    key              text,
    type             text,
    company_id       uuid,
    primary key (id)
);
create table public.package_routes
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    order_legs_id    uuid,
    package_item_id  uuid,
    primary key (id)
);
create table public.package_tracking
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    comment          text,
    status           varchar(255),
    car_id           uuid,
    location_id      uuid,
    package_item_id  uuid,
    supplier_id      uuid,
    warehouse_id     uuid,
    primary key (id)
);
create table public.payment_default_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    default_value        text,
    default_value_mod    boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    company_id           uuid,
    company_mod          boolean,
    primary key (id, rev)
);
create table public.payment_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    value                text,
    value_mod            boolean,
    payment_id           uuid,
    payment_mod          boolean,
    primary key (id, rev)
);
create table public.payment_default_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    default_value    text,
    key              text,
    type             text,
    company_id       uuid,
    primary key (id)
);
create table public.payment_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              text,
    type             text,
    value            text,
    payment_id       uuid,
    primary key (id)
);
create table public.payments
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    amount           float8,
    payment_status   varchar(255),
    primary key (id)
);
create table public.payments_aud
(
    id                     uuid not null,
    rev                    int4 not null,
    revtype                int2,
    create_date            timestamp,
    create_date_mod        boolean,
    created_by             text,
    created_by_mod         boolean,
    deleted                boolean,
    deleted_mod            boolean,
    last_modified_by       text,
    last_modified_by_mod   boolean,
    modify_date            timestamp,
    modify_date_mod        boolean,
    amount                 float8,
    amount_mod             boolean,
    payment_status         varchar(255),
    payment_status_mod     boolean,
    payment_properties_mod boolean,
    primary key (id, rev)
);
create table public.revinfo
(
    rev      int4 not null,
    revtstmp int8,
    primary key (rev)
);
create table public.user_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  text,
    key_mod              boolean,
    type                 text,
    type_mod             boolean,
    value                text,
    value_mod            boolean,
    user_id              uuid,
    user_mod             boolean,
    primary key (id, rev)
);
create table public.user_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              text,
    type             text,
    value            text,
    user_id          uuid,
    primary key (id)
);
create table public.users
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    job_position     text,
    keycloak_email   text,
    keycloak_id      uuid,
    company_id       uuid,
    primary key (id)
);
create table public.users_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    job_position         text,
    job_position_mod     boolean,
    keycloak_email       text,
    keycloak_email_mod   boolean,
    keycloak_id          uuid,
    keycloak_id_mod      boolean,
    company_id           uuid,
    company_mod          boolean,
    user_properties_mod  boolean,
    primary key (id, rev)
);
create table public.warehouse
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    capacity         int8,
    name             varchar(255),
    address_id       uuid,
    primary key (id)
);
create table public.warehouse_aud
(
    id                       uuid not null,
    rev                      int4 not null,
    revtype                  int2,
    create_date              timestamp,
    create_date_mod          boolean,
    created_by               text,
    created_by_mod           boolean,
    deleted                  boolean,
    deleted_mod              boolean,
    last_modified_by         text,
    last_modified_by_mod     boolean,
    modify_date              timestamp,
    modify_date_mod          boolean,
    capacity                 int8,
    capacity_mod             boolean,
    name                     varchar(255),
    name_mod                 boolean,
    address_id               uuid,
    address_mod              boolean,
    order_legs_mod           boolean,
    warehouse_properties_mod boolean,
    primary key (id, rev)
);
create table public.warehouse_properties_aud
(
    id                   uuid not null,
    rev                  int4 not null,
    revtype              int2,
    create_date          timestamp,
    create_date_mod      boolean,
    created_by           text,
    created_by_mod       boolean,
    deleted              boolean,
    deleted_mod          boolean,
    last_modified_by     text,
    last_modified_by_mod boolean,
    modify_date          timestamp,
    modify_date_mod      boolean,
    key                  varchar(255),
    key_mod              boolean,
    type                 varchar(255),
    type_mod             boolean,
    value                varchar(255),
    value_mod            boolean,
    warehouse_id         uuid,
    warehouse_mod        boolean,
    primary key (id, rev)
);
create table public.warehouse_properties
(
    id               uuid      not null,
    create_date      timestamp not null,
    created_by       text      not null,
    deleted          boolean   not null,
    last_modified_by text      not null,
    modify_date      timestamp not null,
    key              varchar(255),
    type             varchar(255),
    value            varchar(255),
    warehouse_id     uuid,
    primary key (id)
);
alter table public.car_locations
    add constraint UK_hye6h6ubuah6ymwrf4m4ayd8j unique (locations_id);
create index IDXrm5m29soyrdwfb0b15g6e1gxv on public.companydeliveryareas (company_id);
alter table public.logger_properties
    add constraint UK_nu654s12mmfaci9i3tav44hw6 unique (properties_id);
alter table public.order_route_order_legs
    add constraint UK_n3m9tts6yr2f18t36esxdjpsj unique (order_legs_id);
alter table public.orders_order_routes
    add constraint UK_epjm8irae6syjss7dxvytuj1m unique (order_routes_id);
alter table public.orders_order_types
    add constraint UK_33ir2es3j8w0efhv9ui3wqari unique (order_types_id);
alter table public.order_types_order_type_properties
    add constraint UK_138wbd8l6u88u40x4kgmqu4p1 unique (order_type_properties_id);
alter table public.addresses_aud
    add constraint FK_slt1on64rkymbaiekchjf5te0 foreign key (rev) references public.revinfo;
alter table public.car_aud
    add constraint FK_1ntgpdadbifq2o1doy18fjho3 foreign key (rev) references public.revinfo;
alter table public.car_locations
    add constraint FK5xkpuwq37wu85aac45pl6a3gj foreign key (car_id) references public.car;
alter table public.car_locations_aud
    add constraint FKf6ht2pi8087dkuu8f4f604wvu foreign key (rev) references public.revinfo;
alter table public.car_properties_aud
    add constraint FK_77d4kqid8lcg4sgcdcqf9g1ou foreign key (rev) references public.revinfo;
alter table public.car_properties
    add constraint FKpm4l686i678r82qjfba0btrr9 foreign key (car_id) references public.car;
alter table public.chat_entry_aud
    add constraint FK_lx4gb9ep6y81rwusn2bv71hiu foreign key (rev) references public.revinfo;
alter table public.companies_aud
    add constraint FK_b6dw8uukxx71vi2ufpri6t7eu foreign key (rev) references public.revinfo;
alter table public.companiesids_aud
    add constraint FKl6dqodhy3n3t3y14lksg3grdi foreign key (rev) references public.revinfo;
alter table public.company_addresses_aud
    add constraint FK56kpm8wxxadg0e3w9v61sbjfg foreign key (rev) references public.revinfo;
alter table public.company_delivery_area_delivery_area_zips_aud
    add constraint FK5br36wbtd7pxfxfpv53vri711 foreign key (rev) references public.revinfo;
alter table public.company_properties_aud
    add constraint FK_qff0qfi2cag1ihxeo0yximrdl foreign key (rev) references public.revinfo;
alter table public.company_addresses
    add constraint FK335qovqakfvmb4xwfumpbosbc foreign key (address_id) references public.addresses;
alter table public.company_addresses
    add constraint FK710v23c1e02xk6mheanqc6vc0 foreign key (company_id) references public.companies;
alter table public.company_delivery_area_delivery_area_zips
    add constraint FK6sqti8xgvxn92p74tfct2ufht foreign key (company_delivery_area_id) references public.companydeliveryareas;
alter table public.companydeliveryareas
    add constraint FK8nn95509ih6oegoncsqx75l8k foreign key (company_id) references public.companies;
alter table public.companydeliveryareas_aud
    add constraint FK_t60gqvkkx98ssl2tkocrhqjcj foreign key (rev) references public.revinfo;
alter table public.company_properties
    add constraint FK5858rpwqock78bho4945qm3od foreign key (company_id) references public.companies;
alter table public.contact_persons_aud
    add constraint FK_oupg309qkk3py9e0eyldehhgd foreign key (rev) references public.revinfo;
alter table public.cost_aud
    add constraint FK_srya84x4qwsh4eo2f2sg42rh5 foreign key (rev) references public.revinfo;
alter table public.cost_default_properties_aud
    add constraint FK_eiywd4nylcesex62i1hul1rk9 foreign key (rev) references public.revinfo;
alter table public.cost_properties_aud
    add constraint FK_pmjsc3l39p0c1ar0e0t36t2mw foreign key (rev) references public.revinfo;
alter table public.cost_default_properties
    add constraint FKcuk68kkqt526iw0hxv3lndj34 foreign key (company_id) references public.companies;
alter table public.cost_properties
    add constraint FKlrb8dj9ctp0vhpx2jh8va9gca foreign key (cost_id) references public.cost;
alter table public.customers_aud
    add constraint FK_r8uxpo71w06pfpw8orit5gfi6 foreign key (rev) references public.revinfo;
alter table public.default_sharing_rights_aud
    add constraint FKr5542bg3ij45ln1th8san9wnu foreign key (rev) references public.revinfo;
alter table public.delivery_methods_aud
    add constraint FK_i9t5verg9336wgbyv5ukn3ncv foreign key (rev) references public.revinfo;
alter table public.globalcompanyproperties_aud
    add constraint FK_lgjjgdj8ldo4usxvxhvcoj3p5 foreign key (rev) references public.revinfo;
alter table public.locations_aud
    add constraint FK_ilfonbkp0dx01t87thsofunst foreign key (rev) references public.revinfo;
alter table public.logger_aud
    add constraint FK_d3rp713h8xvk5exm4oeksjfld foreign key (rev) references public.revinfo;
alter table public.logger_properties
    add constraint FK3t6sla4ay0t6f3on6qhdt1pgb foreign key (properties_id) references public.logger_propeties;
alter table public.logger_properties
    add constraint FKq8gp3q56ccvubb5vadjepoi5m foreign key (logger_id) references public.logger;
alter table public.logger_properties_aud
    add constraint FKr9soukfut3f5f6p93ap2k2nja foreign key (rev) references public.revinfo;
alter table public.logger_propeties_aud
    add constraint FK_awmb0jslhrrlflnt4g05ypsu5 foreign key (rev) references public.revinfo;
alter table public.order_leg_aud
    add constraint FK_lha29hu0yr60k723m4h378k6q foreign key (rev) references public.revinfo;
alter table public.order_properties_aud
    add constraint FK_dq2atpg9n1roasgxoysl6u7kk foreign key (rev) references public.revinfo;
alter table public.order_route_aud
    add constraint FK_h9dstqi9o211ks82o86mi9jti foreign key (rev) references public.revinfo;
alter table public.order_route_order_legs_aud
    add constraint FKn2nb3kemlg1as1n36qmx8kcn5 foreign key (rev) references public.revinfo;
alter table public.order_type_properties_aud
    add constraint FK_nffcesre0177l46m65rgsaaih foreign key (rev) references public.revinfo;
alter table public.order_types_aud
    add constraint FK_q9c0sq68wbh8ahdqy1intgy17 foreign key (rev) references public.revinfo;
alter table public.order_types_order_type_properties_aud
    add constraint FKaadla3ykis722mki63b7em0q foreign key (rev) references public.revinfo;
alter table public.order_leg
    add constraint FK9lefifdodse1wwsw137np13qo foreign key (car_id) references public.car;
alter table public.order_leg
    add constraint FKfw9nejd3743w174vgx8dj6phr foreign key (order_route_id) references public.order_route;
alter table public.order_leg
    add constraint FKmc40kjqr64bflaob6b493vr4q foreign key (warehouse_id) references public.warehouse;
alter table public.order_properties
    add constraint FKjv386d2rt0atj5sy8htoh4d46 foreign key (order_id) references public.orders;
alter table public.order_route_order_legs
    add constraint FKg967rrmqjtykujrt379k3pclp foreign key (order_legs_id) references public.order_leg;
alter table public.order_route_order_legs
    add constraint FKa9tlnhb4x4plj2xd9hlkubp45 foreign key (order_route_id) references public.order_route;
alter table public.orders
    add constraint FKp990232wm71ge02dpjgx94l4f foreign key (address_billing_id) references public.addresses;
alter table public.orders
    add constraint FKq662334ghv0w1c33xk9hohq5r foreign key (address_from_id) references public.addresses;
alter table public.orders
    add constraint FKqle46huw6by7a8e56kx7uioho foreign key (address_to_id) references public.addresses;
alter table public.orders
    add constraint FK1vldikbqexeu85qvsedncxvs3 foreign key (company_id) references public.companies;
alter table public.orders
    add constraint FK9dcuobjbumc1i158oa50ugcj3 foreign key (contact_person_id) references public.contact_persons;
alter table public.orders
    add constraint FK596ikpxfxod0sb5y2w5sg1fdm foreign key (cost_id) references public.cost;
alter table public.orders
    add constraint FKmkr3t9h9m05ru9uhg30uv7p1o foreign key (delivery_method_id) references public.delivery_methods;
alter table public.orders
    add constraint FKakl1p7xiogdupq1376fttx2xc foreign key (parent_order_id) references public.orders;
alter table public.orders
    add constraint FK8aol9f99s97mtyhij0tvfj41f foreign key (payment_id) references public.payments;
alter table public.orders_aud
    add constraint FK_odf5gxpn0qh60ur00skx2xp5n foreign key (rev) references public.revinfo;
alter table public.orders_order_routes_aud
    add constraint FKta2pw8r6d1g2pqxvsy1ajle8u foreign key (rev) references public.revinfo;
alter table public.orders_order_types_aud
    add constraint FKl9hqny0ngf7u3sag8s0mfm7de foreign key (rev) references public.revinfo;
alter table public.orders_order_routes
    add constraint FKf1uto8avjneheqavudfq95sny foreign key (order_routes_id) references public.order_route;
alter table public.orders_order_routes
    add constraint FKjtfmpffo714yfx58m398s1oi0 foreign key (order_id) references public.orders;
alter table public.orders_order_types
    add constraint FKorsq9hkxo572pd6ivmhatys9l foreign key (order_types_id) references public.order_types;
alter table public.orders_order_types
    add constraint FKegmbvdjcn5dvu5thd2n19p3nn foreign key (order_id) references public.orders;
alter table public.orders_package_items_aud
    add constraint FKdi2qb3gotbn16hsupjumurk46 foreign key (rev) references public.revinfo;
alter table public.orders_package_items
    add constraint FKivuey7schspuup34mxj482il4 foreign key (package_items_id) references public.package_items;
alter table public.orders_package_items
    add constraint FKs57610pys5oscfi4m87sde3y5 foreign key (order_id) references public.orders;
alter table public.order_type_properties
    add constraint FKer93q8r3wkvu13sll90hlp04c foreign key (order_type_id) references public.order_types;
alter table public.order_types_order_type_properties
    add constraint FK77w7494lfe0njq9418yo6qacn foreign key (order_type_properties_id) references public.order_type_properties;
alter table public.order_types_order_type_properties
    add constraint FKslywgnf0rdcwfd7o7htdv3beq foreign key (order_type_id) references public.order_types;
alter table public.package_classes_aud
    add constraint FK_m5dvqom266uqnc4ald1cuuc9s foreign key (rev) references public.revinfo;
alter table public.package_items_aud
    add constraint FK_53n58q3psynwquf0otqcge96n foreign key (rev) references public.revinfo;
alter table public.package_package_properties_aud
    add constraint FK_as2x18jnn83ci0ysa4a0xahry foreign key (rev) references public.revinfo;
alter table public.package_properties_aud
    add constraint FK_ke7bnxwt8fuy4vfiyg9ghppwg foreign key (rev) references public.revinfo;
alter table public.package_routes_aud
    add constraint FK_epkfnoyrm76xt4inmsi0w6qk foreign key (rev) references public.revinfo;
alter table public.package_tracking_aud
    add constraint FK_c4iroyldi9ixqnw4l9ivmoay3 foreign key (rev) references public.revinfo;
alter table public.package_items
    add constraint FKr0eqviug6iscesa889ffy42c2 foreign key (package_class_id) references public.package_classes;
alter table public.package_package_properties
    add constraint FK2m2ucdrdphqfcp541f2x4okk7 foreign key (package_item_id) references public.package_items;
alter table public.package_properties
    add constraint FK26lc41jo29p722xotobqkmo2t foreign key (company_id) references public.companies;
alter table public.package_routes
    add constraint FKg89x9mpfgntfnnijj7dlb7xst foreign key (order_legs_id) references public.order_leg;
alter table public.package_routes
    add constraint FK4hxd8665os0c2c222mdhdd9jw foreign key (package_item_id) references public.package_items;
alter table public.package_tracking
    add constraint FK8i4w7gp0eovox3gwsh8nwikjn foreign key (car_id) references public.car;
alter table public.package_tracking
    add constraint FK66d60jb2e70jhb44vstv1lh43 foreign key (package_item_id) references public.package_items;
alter table public.package_tracking
    add constraint FK53vdkt4bhqbol1hbxqsb99kn foreign key (supplier_id) references public.users;
alter table public.package_tracking
    add constraint FK4dj0t3y5rfo7eqwrro149boh5 foreign key (warehouse_id) references public.warehouse;
alter table public.payment_default_properties_aud
    add constraint FK_tp0c5ss67t4xs4bwpnslkamv foreign key (rev) references public.revinfo;
alter table public.payment_properties_aud
    add constraint FK_f4wv98x51tqfns3wbpwnyachj foreign key (rev) references public.revinfo;
alter table public.payment_default_properties
    add constraint FK5gnsky105r4de9xhb4h16ptqv foreign key (company_id) references public.companies;
alter table public.payment_properties
    add constraint FKt7s9vf90md85h6325gs2ca98k foreign key (payment_id) references public.payments;
alter table public.payments_aud
    add constraint FK_f9ggfwabysa39nm2qk02ls8op foreign key (rev) references public.revinfo;
alter table public.user_properties_aud
    add constraint FK_nmj3nf8xfdcjafg3qxot57g5l foreign key (rev) references public.revinfo;
alter table public.user_properties
    add constraint FKrdwt651fwlcinjkrcfevgbulp foreign key (user_id) references public.users;
alter table public.users
    add constraint FKin8gn4o1hpiwe6qe4ey7ykwq7 foreign key (company_id) references public.companies;
alter table public.users_aud
    add constraint FK_nwv4fh3qmwdrk28v1uxgcfahd foreign key (rev) references public.revinfo;
alter table public.warehouse
    add constraint FKdy3p3bleikxqp19sy0y3pws3t foreign key (address_id) references public.addresses;
alter table public.warehouse_aud
    add constraint FK_kfafq60ltbpfbfmadf1ko0th0 foreign key (rev) references public.revinfo;
alter table public.warehouse_properties_aud
    add constraint FK_7jvpqbpfo8y3ly4x7j8byeh3p foreign key (rev) references public.revinfo;
alter table public.warehouse_properties
    add constraint FKijlcncsvxh60vs8twmnwvcgmb foreign key (warehouse_id) references public.warehouse;
create table public.order_comment_chat_aud (id uuid not null, rev int4 not null, revtype int2, create_date timestamp, create_date_mod boolean, created_by text, created_by_mod boolean, deleted boolean, deleted_mod boolean, last_modified_by text, last_modified_by_mod boolean, modify_date timestamp, modify_date_mod boolean, company_id uuid, company_id_mod boolean, email_send_already BOOLEAN DEFAULT FALSE, email_send_already_mod boolean, order_id uuid, order_id_mod boolean, read_status BOOLEAN DEFAULT FALSE, read_status_mod boolean, sequence_id int8, sequence_id_mod boolean, text text, text_mod boolean, primary key (id, rev));
create table public.order_comment_chat (id uuid not null, create_date timestamp not null, created_by text not null, deleted boolean not null, last_modified_by text not null, modify_date timestamp not null, company_id uuid, email_send_already BOOLEAN DEFAULT FALSE not null, order_id uuid, read_status BOOLEAN DEFAULT FALSE not null, sequence_id int8, text text, primary key (id));
alter table public.orders add column delivery_timestamp_arrive timestamp;
alter table public.orders add column delivery_timestamp_leave timestamp;
alter table public.orders add column internal_comment text;
alter table public.orders add column pick_up_timestamp_arrive timestamp;
alter table public.orders add column pick_up_timestamp_leave timestamp;
alter table public.orders_aud add column delivery_timestamp_arrive timestamp;
alter table public.orders_aud add column delivery_timestamp_arrive_mod boolean;
alter table public.orders_aud add column delivery_timestamp_leave timestamp;
alter table public.orders_aud add column delivery_timestamp_leave_mod boolean;
alter table public.orders_aud add column internal_comment text;
alter table public.orders_aud add column internal_comment_mod boolean;
alter table public.orders_aud add column pick_up_timestamp_arrive timestamp;
alter table public.orders_aud add column pick_up_timestamp_arrive_mod boolean;
alter table public.orders_aud add column pick_up_timestamp_leave timestamp;
alter table public.orders_aud add column pick_up_timestamp_leave_mod boolean;
alter table public.order_comment_chat_aud add constraint FK_hrenhcqj0db65k14rofeqdc88 foreign key (rev) references public.revinfo;
create table public.company_favorite_aud (id uuid not null, rev int4 not null, revtype int2, create_date timestamp, create_date_mod boolean, created_by text, created_by_mod boolean, deleted boolean, deleted_mod boolean, last_modified_by text, last_modified_by_mod boolean, modify_date timestamp, modify_date_mod boolean, company_id uuid, company_id_mod boolean, name text, name_mod boolean, company_ids_mod boolean, primary key (id, rev));
create table public.company_id_aud (rev int4 not null, company_favorite_id uuid not null, company_ids uuid not null, revtype int2, primary key (rev, company_favorite_id, company_ids));
create table public.company_favorite (id uuid not null, create_date timestamp not null, created_by text not null, deleted boolean not null, last_modified_by text not null, modify_date timestamp not null, company_id uuid, name text, primary key (id));
create table public.company_id (company_favorite_id uuid not null, company_ids uuid);
alter table public.company_favorite_aud add constraint FK_5o4xcnyee37nhikpxma48hvmf foreign key (rev) references public.revinfo;
alter table public.company_id_aud add constraint FK5nl0wtvsvfkhkq0hjthhw7xp6 foreign key (rev) references public.revinfo;
alter table public.company_id add constraint FKn6db8jvsl032chc8cufkl5x5 foreign key (company_favorite_id) references public.company_favorite;
create table public.company_favorite_aud (id uuid not null, rev int4 not null, revtype int2, create_date timestamp, create_date_mod boolean, created_by text, created_by_mod boolean, deleted boolean, deleted_mod boolean, last_modified_by text, last_modified_by_mod boolean, modify_date timestamp, modify_date_mod boolean, company_id uuid, company_id_mod boolean, name text, name_mod boolean, company_ids_mod boolean, primary key (id, rev));
create table public.company_favorite_company_ids_aud (rev int4 not null, company_favorite_id uuid not null, company_ids uuid not null, revtype int2, primary key (rev, company_favorite_id, company_ids));
create table public.company_favorite (id uuid not null, create_date timestamp not null, created_by text not null, deleted boolean not null, last_modified_by text not null, modify_date timestamp not null, company_id uuid, name text, primary key (id));
create table public.company_favorite_company_ids (company_favorite_id uuid not null, company_ids uuid);
alter table public.company_favorite_aud add constraint FK_5o4xcnyee37nhikpxma48hvmf foreign key (rev) references public.revinfo;
alter table public.company_favorite_company_ids_aud add constraint FKkjce8a2qsd0437a1qvl3sm45m foreign key (rev) references public.revinfo;
alter table public.company_favorite_company_ids add constraint FKdqr1asj2s7yc1tp9x5c2ihnol foreign key (company_favorite_id) references public.company_favorite;
create table public.company_favorite_aud (id uuid not null, rev int4 not null, revtype int2, create_date timestamp, create_date_mod boolean, created_by text, created_by_mod boolean, deleted boolean, deleted_mod boolean, last_modified_by text, last_modified_by_mod boolean, modify_date timestamp, modify_date_mod boolean, company_id uuid, company_id_mod boolean, name text, name_mod boolean, company_list_mod boolean, primary key (id, rev));
create table public.company_favorite_company_list_aud (rev int4 not null, company_favorite_id uuid not null, company_list uuid not null, revtype int2, primary key (rev, company_favorite_id, company_list));
create table public.company_favorite (id uuid not null, create_date timestamp not null, created_by text not null, deleted boolean not null, last_modified_by text not null, modify_date timestamp not null, company_id uuid, name text, primary key (id));
create table public.company_favorite_company_list (company_favorite_id uuid not null, company_list uuid);
alter table public.company_favorite_aud add constraint FK_5o4xcnyee37nhikpxma48hvmf foreign key (rev) references public.revinfo;
alter table public.company_favorite_company_list_aud add constraint FKnemblbejqtfwu34qt82r00tlt foreign key (rev) references public.revinfo;
alter table public.company_favorite_company_list add constraint FKsn1qmja2ee6tmlebac9db53pe foreign key (company_favorite_id) references public.company_favorite;
alter table public.order_comment_chat_aud add column comment text;
alter table public.order_comment_chat_aud add column comment_mod boolean;
alter table public.order_comment_chat_aud add column post_child BOOLEAN DEFAULT FALSE;
alter table public.order_comment_chat_aud add column post_child_mod boolean;
alter table public.order_comment_chat_aud add column post_parent BOOLEAN DEFAULT FALSE;
alter table public.order_comment_chat_aud add column post_parent_mod boolean;
alter table public.order_comment_chat add column comment text;
alter table public.order_comment_chat add column post_child BOOLEAN DEFAULT FALSE not null;
alter table public.order_comment_chat add column post_parent BOOLEAN DEFAULT FALSE not null;
