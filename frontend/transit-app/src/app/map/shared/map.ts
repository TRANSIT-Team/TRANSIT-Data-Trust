import GeoJSON, { Feature, FeatureCollection} from "geojson";

export interface Geometry {
  type: string;
  coordinates: number[];
}

export interface IGeoJson{
  type:string;
  geometry: Geometry;
  properties?:any;
  $key?:string;
}

export interface MapMarker{
  latitude:number;
  longitude:number;
}


export interface IGeoJson {
  // @ts-ignore
  type: "Feature";
  geometry: Geometry;
  properties?: any;
  $key?: string;
}

export class GeoJson implements IGeoJson {

  type = 'Feature';
  geometry: Geometry;


}



export class GeoJsonFeature implements Feature {
    type: "Feature";
    geometry: GeoJSON.Geometry;
    id?: string | number | undefined;
    properties: GeoJSON.GeoJsonProperties;
    bbox?: GeoJSON.BBox | undefined;


  constructor(coordinates: any, public propertyName?: string){


    this.geometry= {
      type: 'Point',
      coordinates: coordinates,
    }
    this.properties = {
      name: propertyName

    }
  }
}
export class GeoJsonFeatureWithCoordinates  {
  feature: Feature;
  coordinates: number[];


  constructor(coordinates: any, propertyName?: any) {

    this.feature= new GeoJsonFeature(coordinates,propertyName);
    this.coordinates=coordinates;

  }

}
/*export class FeatureCollection {
  type = 'FeatureCollection'

  constructor(public features: Array<GeoJson>) {
  }

}
*/
