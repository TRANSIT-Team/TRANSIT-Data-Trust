export interface Direction{
  routes?:    Route[];
  trips?:    Route[];
  waypoints?: Waypoint[];
  code?:      string;
  uuid?:      string;
}

export interface Geometry {
  coordinates?: Array<number[]>;
  type?:        string;
}

export interface Route {
  //geometry?:   Geometry;
  geometry?:   string;
  legs?:       Leg[];
  weightName?: string;
  weight?:     number;
  duration?:   number;
  distance?:   number;
}

export interface Leg {
  summary?:  string;
  weight?:   number;
  duration?: number;
  steps?:    Step[];
  distance?: number;
}

export interface Step {
  intersections?:      Intersection[];
  drivingSide?:        string;
  geometry?:           string;
  mode?:               string;
  maneuver?:           Maneuver;
  ref?:                string;
  weight?:             number;
  duration?:           number;
  name?:               string;
  distance?:           number;
  voiceInstructions?:  VoiceInstruction[];
  bannerInstructions?: BannerInstruction[];
}

export interface BannerInstruction {
  distanceAlongGeometry?: number;
  primary?:               Ary;
  secondary?:             Ary | null;
  sub?:                   null;
}

export interface Ary {
  text?:       string;
  components?: Component[];
  type?:       string;
  modifier?:   string;
}

export interface Component {
  text?: string;
}

export interface Intersection {
  out?:      number;
  entry?:    boolean[];
  bearings?: number[];
  location?: number[];
  in?:       number;
}

export interface Maneuver {
  bearingAfter?:  number;
  bearingBefore?: number;
  location?:      number[];
  modifier?:      string;
  type?:          string;
  instruction?:   string;
}

export interface VoiceInstruction {
  distanceAlongGeometry?: number;
  announcement?:          string;
  ssmlAnnouncement?:      string;
}

export interface Waypoint {
  name?:     string;
  location?: number[];
}
