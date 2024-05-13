import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';

import {Feature} from 'geojson';
import {MapService, NgxMapboxGLModule} from 'ngx-mapbox-gl';
import {FeatureCollection, GeoJSON} from 'geojson';

import {environment} from '../../../environments/environment';
import {Observable, Observer, of, tap} from 'rxjs';
import {Direction, Route} from './direction';


declare var require: any
var polyline = require('@mapbox/polyline');


@Injectable({
  providedIn: 'root'
})
export class DirectionService {
  private map: mapboxgl.Map;
  baseGeocodingApiUrl: string = "https://api.mapbox.com/geocoding/v5/mapbox.places";
  baseDirectionApiUrl: string = "";
  baseDirectionMapBoxUrl: string = "https://api.mapbox.com/directions/v5";
  baseDirectionMapBoxOptimizedUrl: string = "https://api.mapbox.com/optimized-trips/v1";
  direction: Direction;
  distance:any="";
  featureCollection: FeatureCollection;
  public dataResponse: any = {};


  constructor(private http: HttpClient, private ngxMapboxGLModule: NgxMapboxGLModule) {

  }

  getCoordinatesFromAddresse(addresse: string): Observable<FeatureCollection> {
    let Url = this.baseGeocodingApiUrl + "/" + addresse + ".json?access_token=" + environment.mapbox.accessToken;

    return this.http.get<FeatureCollection>(Url)
      .pipe(
        tap(data =>
          data
        )
      );
  }

  getDirectionApiUrl(coordinates: GeoJSON[] = [], optimized: boolean = false, roundtrip: boolean = false): string {


    // https://api.mapbox.com/directions/v5/{profile}/{coordinates}
    // coordinates are {longitude},{latitude};{longitude},{latitude};{longitude},{latitude} ...
    let directionApiUrl;
    let directionApiProfil = "mapbox/driving";
    let directionApicoordinates = "";
    let directionParams = "";
    //  console.log(coordinates);

    if (coordinates.length > 0) {


      coordinates.forEach(function (value, index) {
        if (index > 0) {
          directionApicoordinates += ";";
        }
        if (value.type == "Feature") {
          if (value.geometry.type == "Point") {
            directionApicoordinates += value.geometry.coordinates[0] + "," + value.geometry.coordinates[1];
          }
        }
      });
    }


    if (optimized) {
      this.baseDirectionApiUrl = this.baseDirectionMapBoxOptimizedUrl;
      directionParams = "source=first&destination=last&roundtrip=" + roundtrip.toString() + "&";

    } else {
      this.baseDirectionApiUrl = this.baseDirectionMapBoxUrl;
    }
    directionApiUrl = this.baseDirectionApiUrl + "/" + directionApiProfil + "/" + directionApicoordinates + "?" + directionParams + "geometries=polyline&overview=full&access_token=" + environment.mapbox.accessToken;
    return directionApiUrl;

  }


  getDirectionRoute(directionApiUrl: any): Observable<GeoJSON.Position[]> {
    return this.http.get<GeoJSON.Position[]>(directionApiUrl)
      .pipe(
        tap(data =>
          data
        )
      );
  }

  getDirectionRouteCoords(pointsOfRoute: any): GeoJSON.Position[] {


    let directionRoute: Direction;
    let directionApiUrl: string;
    let coordinates: GeoJSON.Position[] = [];
    // directionApiUrl = this.getDirectionApiUrl(pointsOfRoute, true);

    directionRoute = pointsOfRoute;

    let route: Route = {};
    let routeExists: boolean = false;

    if (directionRoute.routes) {
      if (directionRoute.routes.length > 0) {
        route = directionRoute.routes[0];
        routeExists = true;
      }
    }
    if (directionRoute.trips) {
      if (directionRoute.trips.length > 0) {
        route = directionRoute.trips[0];
        routeExists = true;
      }
    }
    if (routeExists = true) {
      if (route.geometry != undefined) {
        let routeDirectionTest = polyline.decode(route.geometry, 6);
        coordinates = polyline.toGeoJSON(route.geometry).coordinates;
      }

    }

    return coordinates;
  }


}
