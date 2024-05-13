export interface ZipCodePolygon {
  plzName?:  string;
  lanName?:  string;
  plzCode?:  string;
  geometry?: Geometry;
}

export interface Geometry {
  coordinates?: Array<Array<number[]>>;
  type?:        string;
}
