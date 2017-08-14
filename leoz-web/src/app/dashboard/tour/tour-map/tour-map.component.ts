import { Component, OnInit, ViewChild } from '@angular/core';
import 'rxjs/add/operator/takeUntil';

import { MapComponent } from '@yaga/leaflet-ng2';
import Point = L.Point;

import { TourService } from '../tour.service';
import { Position } from '../position.model';
import { Marker } from './marker.model';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

@Component( {
  selector: 'app-tour-map',
  template: `
    <yaga-map #yagaMap [lat]="50.8645" [lng]="9.6917" [zoom]="11">
      <yaga-zoom-control></yaga-zoom-control>
      <yaga-scale-control [metric]="true" [imperial]="false"></yaga-scale-control>
      <yaga-attribution-control></yaga-attribution-control>
      <!-- <yaga-tile-layer [url]="'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'"
                        [attribution]="'© OpenStreetMap-Mitwirkende'"></yaga-tile-layer>-->
      <yaga-tile-layer [url]="'http://tiles.derkurier.de/styles/osm-bright/rendered/{z}/{x}/{y}.png'"
                       [attribution]="'© OpenStreetMap-Mitwirkende'"></yaga-tile-layer>
      <yaga-geojson [data]="routeGeoJson"></yaga-geojson>
      <yaga-marker [lat]="markerLat" [lng]="markerLng" [display]="displayMarker">
        <yaga-popup>
          <p>
            {{'name' | translate}}: {{markerName}}<br/>
            {{'vehicle' | translate}}: {{markerVehicle | translate}}<br/>
            {{'phoneoffice' | translate}}: {{markerPhoneoffice}}<br/>
            {{'phonemobile' | translate}}: {{markerPhonemobile}}<br/>
            {{'lastactivity' | translate}}: {{markerLastactivity | date:dateFormatLong}}
          </p>
        </yaga-popup>
        <yaga-icon [iconUrl]="iconUrl" [iconSize]="iconSize"></yaga-icon>
      </yaga-marker>
    </yaga-map>`
} )
export class TourMapComponent extends AbstractTranslateComponent implements OnInit {

  markerLat: number;
  markerLng: number;
  markerName: string;
  markerVehicle: string;
  markerPhoneoffice: string;
  markerPhonemobile: string;
  markerLastactivity: string;

  displayMarker: boolean;
  name: string;
  routeGeoJson: any;
  iconSize: Point;
  iconUrl: string;

  @ViewChild( 'yagaMap' )
  yagaMap: MapComponent;

  private bbox: L.LatLngBounds;

  constructor( protected translate: TranslateService,
               private tourService: TourService ) {
    super( translate );
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.tourService.displayMarker
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( displayMarker: boolean ) => {
        this.displayMarker = displayMarker;
        if (!displayMarker) {
          this.yagaMap.closePopup();
        }
      } );

    this.tourService.displayRoute
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( displayRoute: boolean ) => {
        if (!displayRoute) {
          this.routeGeoJson = this.createGeoJson( [] );
        }
      } );

    this.tourService.activeMarker
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeMarker: Marker ) => {
        switch (activeMarker.position.vehicleType) {
          case Position.VehicleType.BIKE:
            this.iconUrl = 'assets/css/images/bike-icon.png';
            this.iconSize = new Point( 32, 32 );
            this.markerVehicle = 'bike';
            break;
          case Position.VehicleType.CAR:
            this.iconUrl = 'assets/css/images/car-icon.png';
            this.iconSize = new Point( 32, 32 );
            this.markerVehicle = 'car';
            break;
          case Position.VehicleType.VAN:
            this.iconUrl = 'assets/css/images/van-icon.png';
            this.iconSize = new Point( 32, 32 );
            this.markerVehicle = 'van';
            break;
          case Position.VehicleType.TRUCK:
            this.iconUrl = 'assets/css/images/truck-icon.png';
            this.iconSize = new Point( 32, 32 );
            this.markerVehicle = 'truck';
            break;
          default:
            this.iconUrl = 'assets/css/images/marker-icon.png';
            this.iconSize = new Point( 25, 41 );
            this.markerVehicle = 'unknown';
            break;
        }
        this.markerLat = activeMarker.position.latitude;
        this.markerLng = activeMarker.position.longitude;
        this.markerName = `${activeMarker.driver.firstName} ${activeMarker.driver.lastName}`;
        this.markerPhoneoffice = activeMarker.driver.phone;
        this.markerPhonemobile = activeMarker.driver.mobile;
        this.markerLastactivity = activeMarker.position.time;
        this.yagaMap.flyTo( L.latLng( activeMarker.position.latitude, activeMarker.position.longitude ) );
      } );

    this.tourService.activeRoute
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeRoute: Position[] ) => {
        this.routeGeoJson = this.createGeoJson( activeRoute );
        if (this.bbox) {
          this.yagaMap.fitBounds( this.bbox );
        }
      } );
  }

  private createGeoJson( activeRoute: Position[] ): any {
    let geoJson = {
      'type': 'FeatureCollection',
      'features': []
    };
    if (activeRoute && activeRoute.length > 0) {
      const coordinates = [];
      let latMin;
      let latMax;
      let lngMin;
      let lngMax;
      for (const waypoint of activeRoute) {
        latMin = !latMin || waypoint.latitude < latMin ? waypoint.latitude : latMin;
        latMax = !latMax || waypoint.latitude > latMax ? waypoint.latitude : latMax;
        lngMin = !lngMin || waypoint.longitude < lngMin ? waypoint.longitude : lngMin;
        lngMax = !lngMax || waypoint.longitude > lngMax ? waypoint.longitude : lngMax;
        coordinates.push( [ waypoint.longitude, waypoint.latitude ] );
      }
      this.bbox = L.latLngBounds( [ latMin, lngMin ], [ latMax, lngMax ] );
      geoJson = {
        'type': 'FeatureCollection',
        'features': [ {
          'type': 'Feature',
          'geometry': {
            'type': 'LineString',
            'coordinates': coordinates
          }
        } ]
      };
    }
    return geoJson;
  }
}
