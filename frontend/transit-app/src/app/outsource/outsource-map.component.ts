import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {
  Company,
  CompanyAddress,
  CompanyDeliveryArea,
  LocationPoint,
  OutsourceStategie,
  OutsourceStategieCompany,
  OutsourceStategieCompanyRight,
  OutsourceStategieRight
} from 'src/app/company/shared/company';

import {EntityRight, CanReadWriteCompany} from 'src/app/entity-rights/shared/entity-right';
import {PropertyResource, UniqueResource} from 'src/app/_core/AbstractResource';
import {environment} from '../../environments/environment';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {catchError, forkJoin, iif, map, mergeMap, of, throwError} from 'rxjs';
import {Feature, FeatureCollection} from "geojson";
import * as mapboxgl from 'mapbox-gl';
import {MapService} from '../map/shared/map.service';
import {ZipCodePolygon} from 'src/app/_core/zipCodesPolygon';
import {ClickableMarker} from 'src/app/_core/clickable-mapbox-marker'
import {Order, SubOrder} from '../order/shared/order';
import {OrderService} from '../order/shared/order.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DirectionService} from 'src/app/map/shared/direction.service';
import {Location} from '@angular/common';
import {FunctionsService} from 'src/app/_core/functions.service';
import {ZipCodeState} from 'src/app/_core/zipCodes.service';
import {CompanyService} from 'src/app/company/shared/company.service';
import {GeoJson} from 'src/app/map/shared/map';
import {MatSlideToggleChange} from '@angular/material/slide-toggle';
import {SnackbarService} from 'src/app/_core/snackbar.service';
import {HateoasResourceService, PagedResourceCollection, ResourceCollection} from '@lagoshny/ngx-hateoas-client';
import {Package} from 'src/app/packages/shared/package';
import {EntityRightService} from 'src/app/entity-rights/shared/entity-right.service';
import {Globals} from 'src/app/_core/globals';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {OutsourceService} from "./shared/outsource-component.service";
import {OutsourceCompany} from "./shared/outsource";
import {Address} from "../address/shared/address";

@Component({
  selector: 'app-outsource-map',
  templateUrl: './outsource-map.component.html',
  styleUrls: ['./outsource-map.component.css']
})
export class OutsourceMapComponent implements OnInit {
  @ViewChild("popupContainer") popupContainer: any;
  @Input() dialogComponent: boolean = false;
  @Input() order: Order;
  @Input() outsourceStategieCompanies: OutsourceCompany[];
  @Input() companiesSelected: Company[];
  @Input() outsourceMapCompanies: Company[];
  @Output() forwardToRights = new EventEmitter<any>();
  @Output() directlySendRequests = new EventEmitter<any>();
  @Output() storeOutsourceMapCompanies = new EventEmitter<any>();


  mapLoaded: any = false;
  hideLoader: any = false;

  companies: Company[] = [];
  companiesLive: Company[] = [];

  ownCompany: Company = new Company;
  dataSource: any = [];
  popup: any;
  packagesDatasource: any;
  map: mapboxgl.Map;


  orderRouteData: any = {};
  orderRouteMarkerData: any = {};
  orderComment: string = "";

  constructor(private http: HttpClient,
              private mapService: MapService,
              private orderService: OrderService,
              private outsourceService: OutsourceService,
              private route: ActivatedRoute,
              private directionService: DirectionService,
              private location: Location,
              private companyService: CompanyService,
              private functionsService: FunctionsService,
              private router: Router,
              private snackbarService: SnackbarService,
              private hateoasResourceService: HateoasResourceService,
              private entityRightService: EntityRightService,
              private globals: Globals
  ) {
  }

  ngOnInit(): void {
    this.mapLoaded = true;

    if (this.dialogComponent) {
      this.loadDialogData();
    } else {
      this.loadData();
    }
  }

  saveRequestedCompanies(direct: boolean) {
    this.directlySendRequests.emit(direct);
    this.forwardToRights.emit(this.outsourceStategieCompanies);
  }

  routeBack() {
    this.router.navigate(['../'], {relativeTo: this.route});
  }

  companyAlreadyRequested(companyId: string): boolean {

    let exists: boolean = false;
    this.order.subOrders.filter((sO: Order) => sO.orderStatus == "REQUESTED" || sO.orderStatus == "CANCELED").forEach((sO: Order) => {
      if (companyId == sO!.companyId!.id) {
        exists = true;
      }
    });
    return !exists;
  }

  loadDialogData() {

    let featureCollection: FeatureCollection;
    let points: Feature[] = [];


    this.companyService.getOwnCompany().pipe(
      map((company: Company) => {
        this.ownCompany = company;
      }),
      // mergeMap(() => iif(() => this.outsourceMapCompanies.length > 0, of(this.outsourceMapCompanies), this.outsourceService.getOutsourceMapCompanies())),
      mergeMap(() => this.outsourceService.getOutsourceMapCompanies()),
      map((response: any) => {

        this.companiesLive = response._embedded.companies;
        let ownC: any = this.companiesLive.filter((company: Company) => company.id == this.ownCompany.id)[0];
        if (ownC != undefined) {
          this.ownCompany.deliveryAreaPolyline = ownC.companyDeliveryArea.deliveryAreaPolyline;
        }
        this.companies = this.companiesLive.filter((company: Company) => company.id != this.ownCompany.id);
        this.companies = this.companies.filter((company: Company) => company.name != "Programmierung-TRANSIT");

        if (this.outsourceMapCompanies.length == 0) {
          this.outsourceMapCompanies = this.companies;
          this.storeOutsourceMapCompanies.emit(this.outsourceMapCompanies);
        }
        //don't show a company which is already REQUESTED
        if (this.order.subOrders != undefined) {
          this.order.subOrders.filter((sO: Order) => sO.orderStatus == "REQUESTED").forEach((sO: Order) => {
            this.companies = this.companies.filter((company: Company) => company.id != sO!.companyId!.id);
          })
        }

      }),
      map(() => {
        this.packagesDatasource = this.order.packageItems;
        if (this.order.addressFrom != undefined) {
          if (this.order.addressFrom.locationPoint != undefined) {
            points.push(this.mapService.createFeature(this.order.addressFrom.locationPoint.coordinates!, this.order.addressFrom.locationPoint!.type));
          }
        }

        if (this.order.addressTo != undefined) {
          if (this.order.addressTo.locationPoint != undefined) {
            points.push(this.mapService.createFeature(this.order.addressTo.locationPoint.coordinates!, this.order.addressTo.locationPoint!.type));
          }
        }
        featureCollection = this.mapService.getFeatureCollection(points);

        this.orderRouteMarkerData = featureCollection;
        let apiUrl = "";
        apiUrl = this.directionService.getDirectionApiUrl(points, true, false);
        return apiUrl;
      }),
      mergeMap((apiUrl: any) => this.directionService.getDirectionRoute(apiUrl)),
      map((data: any) => {
        let positionDataCoords: GeoJSON.Position[] = this.directionService.getDirectionRouteCoords(data);
        let datageojson = {
          type: 'Feature',
          properties: {},
          geometry: {
            type: 'LineString',
            coordinates: positionDataCoords
          }
        }
        this.orderRouteData = datageojson;
      }),
      map(() => {

      })
    ).subscribe((e: any) => {
      this.loadMap();
      this.dataSource = this.companies;
    });
  }

  loadData() {
    let orderId: any = this.route.snapshot.paramMap.get('orderId')!;

    let apiUrl = "";
    let featureCollection: FeatureCollection;
    let points: Feature[] = [];

    this.companyService.getOwnCompany().pipe(
      map((company: Company) => {
        this.ownCompany = company;
      }),
      mergeMap((company: any) => this.companyService.getCompaniesWithAddresses()),
      map((companies: Company[]) => {
        this.companiesLive = companies;
      }),
      mergeMap(() => this.ownCompany.getRelation<CompanyDeliveryArea>('companyDeliveryArea')),
      map((companyDeliveryArea: CompanyDeliveryArea) => {
        this.ownCompany.deliveryAreaPolyline = companyDeliveryArea.deliveryAreaPolyline;
      }),
      mergeMap(() => this.orderService.getOrder(orderId)),
      map((order: Order) => {
        this.order = order;
        this.packagesDatasource = this.order.packageItems;
        if (this.order.addressFrom != undefined) {
          if (this.order.addressFrom.locationPoint != undefined) {
            points.push(this.mapService.createFeature(this.order.addressFrom.locationPoint.coordinates!, this.order.addressFrom.locationPoint!.type));
          }
        }

        if (this.order.addressTo != undefined) {
          if (this.order.addressTo.locationPoint != undefined) {
            points.push(this.mapService.createFeature(this.order.addressTo.locationPoint.coordinates!, this.order.addressTo.locationPoint!.type));
          }
        }
        featureCollection = this.mapService.getFeatureCollection(points);

        this.orderRouteMarkerData = featureCollection;

        apiUrl = this.directionService.getDirectionApiUrl(points, true, false);
        return order;
      }),
      mergeMap((order: any) => this.directionService.getDirectionRoute(apiUrl)),
      map((data: any) => {
        let positionDataCoords: GeoJSON.Position[] = this.directionService.getDirectionRouteCoords(data);
        let datageojson = {
          type: 'Feature',
          properties: {},
          geometry: {
            type: 'LineString',
            coordinates: positionDataCoords
          }
        }
        this.orderRouteData = datageojson;
      }),
      map(() => {

      })
    ).subscribe((e: any) => {
      //   console.log(this.ownCompany);
      this.loadMap();
      this.companies = this.companiesLive.filter((company: Company) => company.id != this.ownCompany.id);
      this.dataSource = this.companies;
    });
  }


  showCompanyOnMapAction(company: any) {
    this.flyToCompany(company.id, 8);
    this.loadCompanyZipArea(company.id)

    if (this.popup != undefined) {
      this.map.fire('closeAllPopups');
    }
    if (company.companyAddresses[0].locationPoint != undefined) {
      let lat: any = company.companyAddresses[0].locationPoint.coordinates[1];

      let lon: any = company.companyAddresses[0].locationPoint.coordinates[0];

      let latlon: any = {
        lat: lat,
        lng: lon
      }
      this.popup = company;
      new mapboxgl.Popup({className: "order-route-map-popup"})
        .setLngLat(latlon)
        .setDOMContent(this.popupContainer.nativeElement)
        .addTo(this.map);
    }
  }

  companySelectedForOutsource(company: any) {
    this.showCompanyOnMapAction(company);
  }

  companiesSelectedForOutsource(companies: any) {
    companies.forEach((company: Company) => {
      this.routePlannerAddCompany(company.id);
    })
  }

  routePlannerAddCompany(companyId: any) {
    const company: Company = this.companies.filter((c) => c.id === companyId)[0];
    const companyCheck: Company = this.companiesSelected.filter((c) => c.id === companyId)[0];

    if (companyCheck == undefined) {
      this.companiesSelected.push(company);
    }
    const companyCheckOs: OutsourceStategieCompany = this.outsourceStategieCompanies.filter((c) => c.company.id === companyId)[0];

    if (companyCheckOs == undefined) {


      let outsourceStategieCompany: OutsourceStategieCompany = new OutsourceStategieCompany();
      outsourceStategieCompany.company = company;

      let maxValue: any = Math.max.apply(Math, this.outsourceStategieCompanies.map(function (o) {
        return o.sort;
      }))
      if (this.outsourceStategieCompanies.length > 0) {
        outsourceStategieCompany.sort = maxValue + 1;
      } else {
        outsourceStategieCompany.sort = 1;
      }

      this.outsourceStategieCompanies.push(outsourceStategieCompany);
    }
  }


  changeSort(companyId: string, increase: boolean) {

    const outsourceStategieCompany: OutsourceStategieCompany = this.outsourceStategieCompanies.filter((c) => c.company.id === companyId)[0];
    let index = this.outsourceStategieCompanies.indexOf(outsourceStategieCompany);
    let newSort: number = 0;

    if (increase) {
      newSort = outsourceStategieCompany.sort + 1;
    } else {
      if (outsourceStategieCompany.sort == 0) {
        return;
      }
      newSort = outsourceStategieCompany.sort - 1;
    }
    const outsourceStategieCompanyToChange = this.outsourceStategieCompanies.filter((c) => c.sort === newSort)[0];
    let indexToChange = this.outsourceStategieCompanies.indexOf(outsourceStategieCompanyToChange);
    if (outsourceStategieCompanyToChange != undefined) {

      let newSortToChange: number = 0;
      if (increase) {
        newSortToChange = outsourceStategieCompanyToChange.sort - 1;
      } else {
        newSortToChange = outsourceStategieCompanyToChange.sort + 1;
      }
      outsourceStategieCompanyToChange.sort = newSortToChange;
    }

    outsourceStategieCompany.sort = newSort;

    this.outsourceStategieCompanies.sort((a: any, b: any) => a.sort > b.sort ? 1 : -1);
  }

  routePlannerClearCompanies() {
    this.companiesSelected = [];
  }

  routePlannerClearCompany(companyId: any) {
    const company: Company = this.companies.filter((c) => c.id === companyId)[0];
    this.companiesSelected = this.companiesSelected.filter(item => item !== company);
    this.outsourceStategieCompanies = this.outsourceStategieCompanies.filter(item => item.company !== company);
  }


  // Map ------------------------------------------------------------------------------------------------
  // objects for caching and keeping track of HTML marker objects (for performance)
  markers: any = {};
  markersOnScreen: any = {};
  // filters for classifying earthquakes into five categories based on magnitude
  mag1 = ['<', ['get', 'mag'], 2];

  loadMap() {

    this.map = new mapboxgl.Map({
      container: "map-route",
      accessToken: environment.mapbox.accessToken,
      style: "mapbox://styles/kobers/clb88rmiq001114n0x3vkgu4s",
      zoom: 6.0,
      // @ts-ignore
      center: [10.087587, 51.283556]
    });

    this.mapLoaded = false;

    /// Add map controls
    this.map.addControl(new mapboxgl.NavigationControl());


    this.map.on('load', () => {

      this.map.loadImage(
        'assets/img/markers/flag-map-circle.png',
        (error, image: any) => {
          if (error) throw error;
          // Add the image to the map style.
          this.map.addImage('transit-marker', image);

        });

      this.map.loadImage(
        'assets/img/markers/flag-map-circle-company.png',
        (error, image: any) => {
          if (error) throw error;
          // Add the image to the map style.
          this.map.addImage('transit-marker-company', image);

        });


      // ziparea polygon source and layer
      this.map.addSource('ziparea-polygon', {
        type: 'geojson',
        // @ts-ignore
        'data': {
          'type': 'Feature',
          'geometry': {
            'type': 'MultiPolygon',
            // @ts-ignore
            'coordinates': "",
          }
        }
      });

      this.map.addLayer({
        'id': 'ziparea-polygon',
        'type': 'fill',
        'source': 'ziparea-polygon', // reference the data source
        'layout': {},
        'paint': {
          'fill-color': '#0080ff', // blue color fill
          'fill-opacity': 0.5
        }
      });

      // ziparea polygon source and layer for own company (should be always visible)
      this.map.addSource('ziparea-polygon-owncompany', {
        type: 'geojson',
        // @ts-ignore
        'data': {
          'type': 'Feature',
          'geometry': {
            'type': 'MultiPolygon',
            // @ts-ignore
            'coordinates': "",
          }
        }
      });

      this.map.addLayer({
        'id': 'ziparea-polygon-owncompany',
        'type': 'fill',
        'source': 'ziparea-polygon-owncompany', // reference the data source
        'layout': {},
        'paint': {
          'fill-color': '#ffd400', // blue color fill
          'fill-opacity': 0.5
        }
      });
      this.map.addSource('owncompany-marker', {
        type: 'geojson',
        data: {
          type: 'Feature',
          properties: {},
          geometry: {
            type: 'Point',
            // @ts-ignore
            coordinates: ""
          }
        }
      });

      this.map.addLayer({
        id: 'owncompany-marker',
        source: 'owncompany-marker',
        type: 'symbol',
        layout: {
          'icon-image': 'transit-marker-company', // reference the image
          'icon-size': 0.075,
          'text-size': 14,
          'text-transform': 'uppercase',
          'text-offset': [0, 3.5]
        },
        paint: {
          'text-color': '#000',
          'text-halo-color': '#fff',
          'text-halo-width': 2,
        }
      });
      // @ts-ignore
      //  this.map.getSource('order-route').setData(this.orderRouteData);

      this.map.addSource('order-route', {
        type: "geojson",
        data: this.orderRouteData
      });


      this.map.addLayer({
        id: 'order-route',
        source: 'order-route',
        type: 'line',
        "layout": {
          "line-join": "round",
          "line-cap": "round"
        },
        "paint": {
          "line-color": "#304ffe",
          "line-width": 8,
          "line-opacity": 0.9,
        }
      });
      // @ts-ignore
      //this.map.getSource('order-route-marker').setData(this.orderRouteMarkerData);

      this.map.addSource('order-route-marker', {
        type: 'geojson',
        data: this.orderRouteMarkerData
      });

      this.map.addLayer({
        id: 'order-route-marker',
        source: 'order-route-marker',
        type: 'symbol',
        layout: {
          'icon-image': 'transit-marker', // reference the image
          'icon-size': 0.075,
          'text-size': 14,
          'text-transform': 'uppercase',
          'text-offset': [0, 3.5]
        },
        paint: {
          'text-color': '#000',
          'text-halo-color': '#fff',
          'text-halo-width': 2,
        }
      });


      this.addCompaniesDataToMap();

      this.map.on('click', 'companiesCluster_label', (e: any) => {

        this.popup = e.features[0].properties;
        //  console.log(e);

        new mapboxgl.Popup({className: "order-route-map-popup"})
          .setLngLat(e.lngLat)
          .setDOMContent(this.popupContainer.nativeElement)
          .addTo(this.map);

        this.loadCompanyZipArea(e.features[0].properties.id);

      });


      // Add a custom event listener to the map
      this.map.on('closeAllPopups', () => {
        const popup = document.getElementsByClassName('mapboxgl-popup');
        if (popup.length) {
          popup[0].remove();
        }
      });

      // after the GeoJSON data is loaded, update markers on the screen on every frame
      this.map.on('render', () => {


        if (!this.map.isSourceLoaded('companiesCluster')) {

          return;

        }
        this.updateMarkers('companiesCluster');


        this.mapLoaded = true;
        this.hideLoader = this.mapLoaded;

      });

      //this.getOrder();
      this.loadOwnCompanyArea();
    });
  }


  loadCompanyZipArea(companyId: string, flyTo: boolean = false) {

    if (companyId != "") {
      let zipsFilter = ",";
      let company: any = this.companies.filter((c) => c.id === companyId)[0];


      let geodata: any = company.companyDeliveryArea.deliveryAreaPolyline;
      let polygonData = {
        'type': 'Feature',
        'geometry': JSON.parse(geodata)
      }
      // @ts-ignore
      this.map.getSource('ziparea-polygon').setData(polygonData);
      if (flyTo) {
        this.flyToCompany(companyId, 9);
      }

    }
  }


  loadOwnCompanyArea() {
    let geodata: any = this.ownCompany.deliveryAreaPolyline;
    let polygonData: any = {
      type: 'Feature',
      geometry: JSON.parse(geodata)
    }
    let polygonDataClass: GeoJson = new GeoJson();
    polygonDataClass.type = 'Feature';
    polygonDataClass.geometry = geodata;
    // @ts-ignore
    this.map.getSource('ziparea-polygon-owncompany').setData(polygonData);
    let newLP: any = this.ownCompany!.companyAddresses![0].locationPoint;
    let newCoord: number[] = [newLP.coordinates[0], newLP.coordinates[1]];
    newLP.coordinates = newCoord;
    // @ts-ignore
    this.map.getSource('owncompany-marker').setData(newLP);
  }


  flyToCompany(companyId: any, zoom: any) {
    const company: Company = this.companies.filter((c) => c.id === companyId)[0];
    if (company != undefined) {
      if (company.companyAddresses != undefined) {
        if (company.companyAddresses[0] != undefined) {
          const latlon = company.companyAddresses[0].locationPoint;
          this.mapFlyTo(latlon?.coordinates, zoom);
        }
      }
    }
  }

  mapFlyTo(center: any, zoom: any) {
    this.map.flyTo({center: center, zoom: zoom});
  }

  updateMarkers(nameCluster: string) {

    //cluster markers!
    const newMarkers: any = {};
    const features = this.map.querySourceFeatures(nameCluster);

    // for every cluster on the screen, create an HTML marker for it (if we didn't yet),
    // and add it to the map if it's not there already
    for (const feature of features) {
      if (feature.geometry) {
      }
      // @ts-ignore
      const coords = feature.geometry.coordinates;

      const props = feature.properties;
      if (props) {
        if (!props['cluster']) {
          continue;
        }

        const id = props['cluster_id'];
        //  console.log("creatClusterPoint");
        let marker = this.markers[id];
        if (!marker) {
          const el = this.creatClusterPoint(props);
          // create the popup
          const popup = new mapboxgl.Popup({offset: 25}).setText(
            'Construction on the Washington Monument began in 1848.'
          );
          // @ts-ignore
          marker = this.markers[id] = new ClickableMarker({element: el})
            .setLngLat(coords)
          //.setPopup(popup)
          // .onClick(this.onClusterClick.bind(this));
          ;

        }

        marker.onClick(function () {
          // @ts-ignore
          this.onClusterClick(marker);

        }.bind(this));
        newMarkers[id] = marker;

        if (!this.markersOnScreen[id]) {
          marker.addTo(this.map);
        }
      }
    }
// for every marker we've added previously, remove those that are no longer visible
    for (const id in this.markersOnScreen) {
      if (!newMarkers[id]) {
        this.markersOnScreen[id].remove();
      }
    }
    this.markersOnScreen = newMarkers;
  }


  onClusterClick(e: any) {
    var zoom: number = this.map.getZoom();
    if (zoom > 0 && zoom < 7) {
      zoom = zoom + 2;
    }
    if (zoom > 7) {
      zoom = zoom + 1;
    }

    this.mapFlyTo(e._lngLat, zoom);

  }

  clusters: string[] = [];

  // code for creating an SVG donut chart from feature properties
  creatClusterPoint(props: any) {
    const offsets = [];
    const counts = [
      props.status,
    ];
    // console.log("props");
    let total = 0;
    for (const count of counts) {
      // offsets.push(total);
      total += count;
    }
    let html = `<div>`;
    for (let i = 0; i < counts.length; i++) {
      html += '';
    }
    let id = 'cl_' + props.cluster_id;
    this.clusters.push(id);

    let bg = "#f4433682";
    if (total > 9) {
      bg = "#f44336c2";
    }
    if (total > 14) {
      bg = "#f44336e6";
    }
    html += '<div id="' + id + '" style="cursor:pointer;background:' + bg + ';border-radius:50%;height:54px;width:54px;padding:14px;display:block;border: 3px solid #304ffe00;text-align: center;color: #000;font-size:14px;font-weight: 700;">' +
      total.toLocaleString() +
      '</div>' +
      '</div>';

    const el = document.createElement('div');
    el.innerHTML = html;
    return el.firstChild;
  }


  addCompaniesDataToMap() {

    let companiesPoints: Feature[] = [];

    this.companies.forEach((company: Company) => {

      if (company != undefined) {
        if (company.companyAddresses != undefined) {
          if (company.companyAddresses[0] != undefined) {
            if (company.companyAddresses[0].locationPoint != undefined) {
              let f: Feature;
              //swap lat and lon because mapbox wants it her in this order [lon,lat]!
              let tempCoords: any = company.companyAddresses[0].locationPoint.coordinates;
              company.companyAddresses[0].fullAddresse = company.companyAddresses[0].street! + ', ' + company.companyAddresses[0].zip + ' ' + company.companyAddresses[0].city;
              /*  if (tempCoords != undefined) {
                  const tmp = tempCoords[1]
                  tempCoords[1] = tempCoords[0];
                  tempCoords[0] = tmp;
                }*/

              f = {
                "type": "Feature",
                "geometry": {
                  "type": "Point",
                  "coordinates": tempCoords
                },
                "properties": {
                  "name": company.name,
                  "id": company.id,
                  "mag": 1
                }
              }
              companiesPoints.push(f);
            }
          }
        }
      }
    });


    let companiesPointsCollection = this.mapService.getFeatureCollection(companiesPoints);

    // console.log("clusering");
    // add a clustered GeoJSON source for a sample set of earthquakes
    this.map.addSource('companiesCluster', {
      'type': 'geojson',
      'data': companiesPointsCollection,
      'cluster': true,
      'clusterRadius': 80,
      'clusterProperties': {
        // keep separate counts for each magnitude category in a cluster
        'status': ['+', ['get', 'mag']]
      }
    });
    // circle and symbol layers for rendering individual earthquakes (unclustered points)
    this.map.addLayer({
      'id': 'companiesCluster_circle',
      'type': 'circle',
      'source': 'companiesCluster',
      'filter': ['!=', 'cluster', true],
      'paint': {
        'circle-color': "#f44336",
        'circle-opacity': 0.8,
        'circle-radius': 13
      }
    });
    this.map.addLayer({
      'id': 'companiesCluster_label',
      'type': 'symbol',
      'source': 'companiesCluster',
      'filter': ['!=', 'cluster', true],
      'layout': {
        'text-field': [
          'format',
          ['get', 'name']
        ],
        'text-font': ['Open Sans Semibold', 'Arial Unicode MS Bold'],
        'text-size': 10
      },
      'paint': {
        'text-color': [
          'case',
          ['<', ['get', 'status'], 3],
          'black',
          'white'
        ]
      }
    });

  }


}
