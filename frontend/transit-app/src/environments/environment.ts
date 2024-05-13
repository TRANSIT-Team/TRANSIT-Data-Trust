// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
    mapbox: {
      accessToken:
        'pk.eyJ1Ijoia29iZXJzIiwiYSI6ImNsNjUwNTIzZzAxbmMzYm5vZGsxaTF2OTEifQ.dsJnTEm6J2YYWTN1W7ZvYA',
      style: 'mapbox://styles/kobers/cl6dfwk59000i15pm9h1nhslt',
      altStyle: 'mapbox://styles/kobers/clb3mvxrr001r14pgc5438j7q',
      orderMapStyle: 'mapbox://styles/kobers/clp19ps6h01df01qmeb58bv55',
  },
  geoservice: 'https://api.transit-project.de/geoservice/v1',
  altBackend: 'http://kong.transit-kong-1.rancher.internal:8000/api/v1',
  //  backend: 'http://kong.transit-kong-1.rancher.internal:8000/api/v1',

  backend: 'http://localhost:8080/api/v1',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
