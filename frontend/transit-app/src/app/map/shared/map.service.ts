import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {GeoJsonFeatureWithCoordinates} from './map';

import * as mapboxgl from 'mapbox-gl';
import {Marker} from "leaflet";
import {Observable} from 'rxjs';
import {Feature, FeatureCollection} from "geojson";
import {DirectionService} from './direction.service';

declare var require: any
var polyline = require('@mapbox/polyline');

@Injectable({
  providedIn: 'root'
})


export class MapService {

  features: Feature[] = []
  markers: Feature[] = []
  map: mapboxgl.Map;
  points: Feature[] = [];
  optimizedRoute: boolean = false;
  roundTripRoute: boolean = false;
  featureCollection: FeatureCollection;
  distanceMap: any = "";

  constructor(private directionService: DirectionService) {
    //let newGeoJson:GeoJson=new GeoJson({ coordinates: [12.373075, 51.339695]});
    // this.markers.push(newGeoJson);
    let feature: Feature;

    feature = this.createFeature([12.373075, 51.339695], "Waypoint 1")
    this.features.push(feature);

    feature = this.createFeature([12.573075, 51.359695], "Waypoint 2")
    this.features.push(feature);
  }


  getMarkers(): any {
    return null;
  }

  createFeature(coordinates: number[], propertyName?: string): Feature {
    let feature: Feature;
    // feature= new GeoJsonFeature(coordinates,propertyName);
    feature = {
      type: "Feature",
      geometry: {
        type: "Point",
        coordinates: coordinates
      },
      properties: {
        name: propertyName
      }
    };
    return feature;
  }

  getFeatures(): Feature[] {
    return this.features;
  }


  getFeatureCollection(features: Feature[]): FeatureCollection {
    let featurecollection: FeatureCollection;
    featurecollection = {
      type: "FeatureCollection",
      features: features
    };
    return featurecollection;
  }

  createMarker(coordinates: number[], propertyName?: string) {
    return this.features.push(this.createFeature(coordinates, propertyName));
  }

  removeMarker(index: number) {
    return delete this.features[index];
  }




  buildMap(mapId: string, points: Feature[], letState: boolean = false, style: any = "style") {

    //console.log(points);
    this.points = points;
    this.featureCollection = this.getFeatureCollection(this.points);
    // @ts-ignore
    // console.log(this.points[0].center);
    let centerCoord1: any;
    let centerCoord2: any;

    if (this.points.length == 0) {
      //take Frankfurt Center or Germany
      centerCoord1 = [51.1642292, 10.4541194];
      centerCoord2 = [51.1642292, 10.4541194];
    } else {
      // @ts-ignore
      if (this.points[0].center == undefined && this.points[1].center == undefined) {
        // @ts-ignore
        centerCoord1 = this.points[0].geometry.coordinates;
        // @ts-ignore
        centerCoord2 = this.points[1].geometry.coordinates;
      } else {
        // @ts-ignore
        centerCoord1 = this.points[0].center;
        // @ts-ignore
        centerCoord2 = this.points[1].center;
      }
    }

    // @ts-ignore
    let s: string = environment.mapbox.style;


    this.map = new mapboxgl.Map({
      container: mapId,
      accessToken: environment.mapbox.accessToken,
      style: s,
      zoom: 11,
      // @ts-ignore
      center: centerCoord1
    });


    this.map.fitBounds([
        // @ts-ignore
        centerCoord1,
        // @ts-ignore
        centerCoord2
      ],
      {
        padding: {top: 55, bottom: 55, left: 45, right: 45}
      });


/// Add map controls
    this.map.addControl(new mapboxgl.NavigationControl());


    this.map.on('load', (event) => {
      this.map.loadImage(
        'assets/img/markers/flag-map-circle.png',
        (error, image: any) => {
          if (error) throw error;
          // Add the image to the map style.
          this.map.addImage('transit-marker', image);

        });

      this.loadLayers();
    })


  }

  removeLayerAndSource(mapLayerSourceName: string) {

    let mapLayer = this.map.getLayer(mapLayerSourceName);
    let mapSource: any = this.map.getLayer(mapLayerSourceName);
    if (typeof mapLayer !== 'undefined') {
      this.map.removeLayer(mapLayerSourceName);
    }
    if (typeof mapSource !== 'undefined') {
      this.map.removeSource(mapLayerSourceName);
    }

  }

  loadLayers() {
    this.addLayerRouting();
    this.addLayerMarker();

    this.loadDirectionRoute();
    this.loadMarkers();
  }


  loadMarkers() {

    // @ts-ignore
    this.map.getSource('waypoints').setData(this.featureCollection);
  }


  loadDirectionRoute() {

    let apiUrl = this.directionService.getDirectionApiUrl(this.points, this.optimizedRoute, this.roundTripRoute);
    //  console.log("api",apiUrl);
    this.directionService.getDirectionRoute(apiUrl).subscribe({
      next: (data: any) => {

        //console.log("getDirectionRoute", data);
        let positionDataCoords: GeoJSON.Position[] = this.directionService.getDirectionRouteCoords(data);


        let dataGeometry: any = {
          type: 'Feature',
          properties: {},
          geometry: {
            type: 'LineString',
            coordinates: positionDataCoords
          }
        }

        if (data.routes != undefined) {
          if (data.routes[0] != undefined) {

            this.distanceMap = Math.round(data.routes[0].distance / 1000).toString();

          }
        }

        // @ts-ignore
        this.map.getSource('routing').setData(dataGeometry);


      }
    })
  }

  addLayerRouting() {

    this.map.addSource('routing', {
      type: "geojson",
      data: {
        type: 'Feature',
        properties: {},
        geometry: {
          type: 'LineString',
          coordinates: []
        }
      }
    });


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
        "line-opacity": 0.9,
      }
    });
  }

  addLayerMarker() {


    this.map.addSource('waypoints', {
        type: 'geojson',
        data: {
          type: "Feature",
          geometry: {
            type: "Point",
            coordinates: []
          },
          "properties": {}
        }
      }
    );


    this.map.addLayer({
      id: 'waypoints',
      source: 'waypoints',
      type: 'symbol',
      layout: {
        'icon-image': 'transit-marker', // reference the image
        'icon-size': 0.075,
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


  convertPolylineToGeoJson(gData
                             :
                             any
  ):
    any {


    let geodata: any = "";
    if (gData != null) {


      let multipolygon: Array<number[]> = [];
      let splitter: string = String.fromCharCode(8224); //defined Splitter by the framework
      let polydata: any = gData.split(splitter);
      let gDataDecoded: any = polyline.toGeoJSON(gData);

      polydata.forEach((singlePolyline: any) => {
        let d: any = polyline.toGeoJSON(singlePolyline);
        let dtmp: any = polyline.toGeoJSON(singlePolyline);
        dtmp.coordinates = [];

        d.coordinates.forEach((coordinate: any) => {

          if ((coordinate[0] < 56 && coordinate[1] < 16) || (coordinate[1] < 56 && coordinate[0] < 16)) {
            dtmp.coordinates.push(coordinate);
          }

        });
        multipolygon.push(dtmp.coordinates);
      });

      geodata = {
        type: "MultiLineString",
        coordinates: multipolygon
      };
    }

    return geodata;

  }


  convertMutliLineToGeoJson(gData
                              :
                              any
  ):
    any {
    let geodata: any = '';

    let multipolygon: Array<number[]> = [];
    let splitter: string = String.fromCharCode(8224);
    let splitterIntern: string = String.fromCharCode(8225);
    let polydata: any = gData.split(splitter);

    if (polydata.length == 1) {
      let rings = polydata[0].split(splitterIntern);
      for (const element of rings) {
        let d: any = polyline.toGeoJSON(element);
        multipolygon.push(d.coordinates);
      }
      geodata = {
        type: 'MultiLineString',
        coordinates: multipolygon,
      };
    } else {
      console.log(polydata);
      polydata.forEach((singlePolyline: any) => {
        let rings = singlePolyline.split(splitterIntern);
        for (const element of rings) {
          let d: any = polyline.toGeoJSON(element);
          multipolygon.push(d.coordinates);
        }
      });
      geodata = {
        type: 'MultiLineString',
        coordinates: multipolygon,
      };
    }


    return geodata;
  }


}

