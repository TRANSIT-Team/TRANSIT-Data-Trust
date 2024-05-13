import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Observable, Subscriber} from 'rxjs';
import {environment} from '../../environments/environment';
import {MapService, NgxMapboxGLModule} from 'ngx-mapbox-gl';
import {DirectionService} from '../map/shared/direction.service';
import mapboxgl from 'mapbox-gl';
import {GeoJsonProperties, Geometry, FeatureCollection, Feature} from 'geojson';
import {Direction, Route} from '../map/shared/direction';
import {HttpClient} from '@angular/common/http';


declare var require: any
var polyline = require('@mapbox/polyline');

export interface DeliveryRoute {
  name: string;
  id: string;
  zip: string;
  city: string;
  street: string;
  dropdown: string;
  dateExample: string;
  description?: string;
  status?:string;
}

const ELEMENT_DATA: DeliveryRoute[] = [
  {
    id: '1',
    name: 'FIRMA A',
    zip: "04109",
    city: 'Leipzig',
    street: 'Grimmaische Str. 12',
    dropdown: '1',
    dateExample: '2022-07-18T12:12:59.749Z',
    status: 'Abgeholt'
  },
  {
    id: '2',
    name: 'FIRMA B',
    zip: "01157 ",
    city: 'Dresden',
    street: 'Wilhelm-Müller-Straße 9A',
    dropdown: '3',
    dateExample: '2022-07-18T12:12:59.749Z',
    status: 'wartend auf Abholung'
  },
];

@Component({
  selector: 'app-package-route',
  templateUrl: './package-route.component.html',
  styleUrls: ['./package-route.component.css'],
  providers: [MapService, DirectionService]
})

export class PackageRouteComponent implements OnInit {
  style = 'mapbox://styles/kobers/cl6dfwk59000i15pm9h1nhslt';
  lat = 51.340907;
  lng = 12.375927;
  points: Feature[] = [];
  private map: mapboxgl.Map;

  data: DeliveryRoute[] = ELEMENT_DATA;

  constructor(public mapbox: NgxMapboxGLModule, private directionService: DirectionService, private http: HttpClient) {
  }

  //https://angular.io/guide/observables
  ngOnInit(): void {

    this.data.forEach(async (value) => {

        let popupContent = '<div style="font-family: Roboto;padding: 5px;"><strong>' + value.name + '</strong><p>' +
          '<table>' +
          '<tr><td><strong>' + 'Status:' + '</strong></td><td>' + value.status + '</td></tr> ' +
          '<tr rowspan="2"><td><strong>' + 'Adresse:' + '</strong></td><td>' + value.street + ", " + value.zip + ", " + value.city + '</td></tr> ' +
          '<tr><td colspan="2"><strong>' + '<a href="#">weiter Informationen</a>' + '</strong></td></tr> ' +
          '</p></div>';

        this.directionService.getCoordinatesFromAddresse(value.street + "," + value.zip + "," + value.city).subscribe({
          next: data => {
            let featureCollection: FeatureCollection = data;
            if (featureCollection.features.length > 0) {
              if (featureCollection.features[0].geometry.type == "Point") {

                let f: Feature = featureCollection.features[0];
                let tmpProperties: any = {};

                if (f.properties) {
                  tmpProperties.accuracy = f.properties["accuracy"];
                }
                tmpProperties.description = popupContent;
                tmpProperties.name = value.name + " " + value.street + ", " + value.zip + ", " + value.city;
                f.properties = tmpProperties;
                this.points.push(f);
              }
            }
          }
        })
      }
    )
    this.buildMap();
  }


  private getCurrentPosition(): any {
    return new Observable((observer: Subscriber<any>) => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((position: any) => {
          observer.next({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          });
          observer.complete();
        });
      } else {
        observer.error();
      }
    });
  }


  buildMap() {

    this.map = new mapboxgl.Map({
      container: 'map',
      accessToken: environment.mapbox.accessToken,
      style: this.style,
      zoom: 8,
      center: [this.lng, this.lat]
    });

    this.map.on('load', (event) => {
      this.map.loadImage(
        'assets/img/transit-marker-round.png',
        (error, image: any) => {
          if (error) throw error;
          // Add the image to the map style.
          this.map.addImage('transit-marker', image);

          this.map.addSource('waypoints', {
            type: 'geojson',
            data: {
              type: 'FeatureCollection',
              features: this.points
            }
          });

          this.getDirectionRoute();
        });

      this.map.on('click', 'waypoints', (e) => {
        let description = "";
        let coordinates = [0, 0];

        // Copy coordinates array.
        if (e.features) {
          if (e.features[0].geometry.type == "Point") {
            coordinates = e.features[0].geometry.coordinates.slice();
          }
          if (e.features[0].properties) {
            description = e.features[0].properties['description'];
          }
        }
        // Ensure that if the map is zoomed out such that multiple
        // copies of the feature are visible, the popup appears
        // over the copy being pointed to.
        while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
          coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
        }
        new mapboxgl.Popup()
          // @ts-ignore
          .setLngLat(coordinates)
          .setHTML(description)
          .addTo(this.map);
      });
    })
  }

  getDirectionRoute() {

    let apiUrl = this.directionService.getDirectionApiUrl(this.points, true);
    this.directionService.getDirectionRoute(apiUrl).subscribe({
      next: data => {

        let positionDataCoords: GeoJSON.Position[] = this.directionService.getDirectionRouteCoords(data);
        this.map.addSource('routing', {
          type: "geojson",
          data: {
            type: 'Feature',
            properties: {},
            geometry: {
              type: 'LineString',
              coordinates: positionDataCoords
            }
          }
        });
        this.addLayerRouting();
        this.addLayerMarker();
      }
    })
  }


  addLayerRouting() {
    /// create map layers with realtime data
    this.map.addLayer({
      id: 'routing',
      source: 'routing',
      type: 'line',
      "layout": {
        "line-join": "round",
        "line-cap": "round"

      },
      "paint": {
        "line-color": "#304ffe",
        "line-width": 8,
        "line-opacity": 0.7,
      }
    });

  }

  addLayerMarker() {
    this.map.addLayer({
      id: 'waypoints',
      source: 'waypoints',
      type: 'symbol',
      layout: {
        'icon-image': 'transit-marker', // reference the image
        'icon-size': 0.10,
        'text-field': '{name}',
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
  }
}
