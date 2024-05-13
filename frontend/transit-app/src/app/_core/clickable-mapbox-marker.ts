import * as mapboxgl from 'mapbox-gl';
// extend mapboxGL Marker so we can pass in an onClick handler
export class ClickableMarker extends mapboxgl.Marker {

  // new method onClick, sets _handleClick to a function you pass in
  onClick(handleClick:any) {
    // @ts-ignore
    this._handleClick = handleClick;
    return this;
  }

  // the existing _onMapClick was there to trigger a popup
  // but we are hijacking it to run a function we define
  _onMapClick(e:any) {
    const targetElement = e.originalEvent.target;
    // @ts-ignore
    const element = this._element;
    // @ts-ignore
    if (this._handleClick && (targetElement === element || element.contains((targetElement)))) {
      // @ts-ignore
      this._handleClick();
    }
  }
};
