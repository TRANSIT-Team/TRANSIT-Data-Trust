import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {NgxMapboxGLModule, Position} from 'ngx-mapbox-gl';

import * as MapBox from 'mapbox-gl';

import {Subscription} from 'rxjs';
import {IGeoJson} from './shared/map';
import mapboxgl from 'mapbox-gl';
import {GeoJsonProperties, Geometry, FeatureCollection, Feature} from 'geojson';

import {HttpClient} from '@angular/common/http';
import {DirectionService} from './shared/direction.service';
import {Direction, Route} from './shared/direction';
import {environment} from '../../environments/environment';

import {MapService} from './shared/map.service';

declare var require: any
var polyline = require('@mapbox/polyline');

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  distanceRoute: number;
  durationRoute: number;
  optimizedRoute: boolean = false;
  roundTripRoute: boolean = false;
  mapOptions = {
    zoom: 12,
    centerLon: 12.375927,
    centerLat: 51.340907,
    mode: '2D',
    style: 'mapbox://styles/kobers/cl6dfwk59000i15pm9h1nhslt'
  }

  style = 'mapbox://styles/kobers/cl6dfwk59000i15pm9h1nhslt';
  lat = 51.340907;
  lng = 12.375927;
  message = 'Hello World!';

  points: Feature[];
  fatureCollection: FeatureCollection;
  source: any;

  features: Feature[] = [];

  map: MapBox.Map;

  constructor(public mapbox: NgxMapboxGLModule, private mapService: MapService, private directionService: DirectionService, private http: HttpClient) {


  }

  ngOnInit() {


    this.points = this.mapService.getFeatures();
    this.fatureCollection = this.mapService.getFeatureCollection(this.points);
    this.initializeMap();

  }

  private initializeMap() {
    /// locate the user
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(position => {
        this.lat = position.coords.latitude;
        this.lng = position.coords.longitude;
        this.map.flyTo({
          center: [this.lng, this.lat]
        })
      });
    }
    this.buildMap()
  }

  addMarker(coordinates:any) {

    let newFeature: Feature = this.mapService.createFeature(coordinates, "Waypoint " + (this.fatureCollection.features.length + 1));
    this.points.push(newFeature);
    this.fatureCollection.features = this.points;

  }

  deleteMarker(index:number) {

   // delete this.points[index];
    this.points.splice(index,1)

    this.fatureCollection.features = this.points;
    this.reloadLayers();
  }

  buildMap() {

    this.map = new mapboxgl.Map({
      container: 'map-div',
      accessToken: environment.mapbox.accessToken,
      style: this.style,
      zoom: 13,
      center: [this.lng, this.lat]
    });

    /// Add map controls
    this.map.addControl(new mapboxgl.NavigationControl());

    //// Add Marker on Click
    this.map.on('click', (event) => {

      this.addMarker([event.lngLat.lng, event.lngLat.lat]);
      // @ts-ignore
      //this.map.getSource('waypoints').setData(this.fatureCollection);
      this.reloadLayers();

    })

    this.map.on('load', (event) => {
      this.map.loadImage(
        'assets/img/transit-marker-round.png',
        (error, image: any) => {
          if (error) throw error;
          // Add the image to the map style.
          this.map.addImage('transit-marker', image);

          this.reloadLayers();
        });
    })
  }

  removeLayerAndSource(mapLayerSourceName:string){

    let mapLayer = this.map.getLayer(mapLayerSourceName);
    let mapSource:any = this.map.getLayer(mapLayerSourceName);
    if (typeof mapLayer !== 'undefined') {
      this.map.removeLayer(mapLayerSourceName);
    }
    if (typeof mapSource !== 'undefined') {
      this.map.removeSource(mapLayerSourceName);
    }

  }

  reloadLayers() {
    this.removeLayerAndSource('routing');
    this.removeLayerAndSource('waypoints');
    this.addMarkers();
    this.addDirectionRoute();
  }


  addMarkers() {
    this.map.addSource('waypoints', {
      type: 'geojson',
      data: this.fatureCollection
    });
  }

  addDirectionRoute() {

    let apiUrl = this.directionService.getDirectionApiUrl(this.points, this.optimizedRoute, this.roundTripRoute);

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
