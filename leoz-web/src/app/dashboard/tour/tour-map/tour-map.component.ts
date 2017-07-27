import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';

import { MapComponent } from '@yaga/leaflet-ng2';
import Point = L.Point;

import { TourService } from '../tour.service';
import { Position } from '../position.model';

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
            Latitude: {{markerLat}}<br/>
            Longitude: {{markerLng}}
          </p>
        </yaga-popup>
        <yaga-icon [iconUrl]="iconUrl" [iconSize]="iconSize"></yaga-icon>
      </yaga-marker>
    </yaga-map>`
} )
export class TourMapComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<void> = new Subject<void>();

  markerLat: number;
  markerLng: number;
  displayMarker: boolean;
  name: string;
  routeGeoJson: any;
  iconSize: Point;
  iconUrl: string;

  @ViewChild( 'yagaMap' )
  yagaMap: MapComponent;

  private bbox: L.LatLngBounds;

  constructor( private tourService: TourService ) {
  }

  ngOnInit(): void {

    this.tourService.displayMarker
      .takeUntil(this.ngUnsubscribe)
      .subscribe( ( displayMarker: boolean ) => {
      this.displayMarker = displayMarker;
    } );

    this.tourService.displayRoute
      .takeUntil(this.ngUnsubscribe)
      .subscribe( ( displayRoute: boolean ) => {
      if (!displayRoute) {
        this.routeGeoJson = this.createGeoJson( [] );
      }
    } );

    this.tourService.activeMarker
      .takeUntil(this.ngUnsubscribe)
      .subscribe( ( activeMarker: Position ) => {
      switch (activeMarker.vehicleType) {
        case Position.VehicleType.BIKE:
          this.iconUrl = 'assets/css/images/bike-icon.png';
          this.iconSize = new Point( 32, 32 );
          break;
        case Position.VehicleType.CAR:
          this.iconUrl = 'assets/css/images/car-icon.png';
          this.iconSize = new Point( 32, 32 );
          break;
        case Position.VehicleType.VAN:
          this.iconUrl = 'assets/css/images/van-icon.png';
          this.iconSize = new Point( 32, 32 );
          break;
        case Position.VehicleType.TRUCK:
          this.iconUrl = 'assets/css/images/truck-icon.png';
          this.iconSize = new Point( 32, 32 );
          break;
        default:
          this.iconUrl = 'assets/css/images/marker-icon.png';
          this.iconSize = new Point( 25, 41 );
          break;
      }
      this.markerLat = activeMarker.latitude;
      this.markerLng = activeMarker.longitude;
      this.yagaMap.flyTo( L.latLng( activeMarker.latitude, activeMarker.longitude ) );
    } );

    this.tourService.activeRoute
      .takeUntil(this.ngUnsubscribe)
      .subscribe( ( activeRoute: Position[] ) => {
      this.routeGeoJson = this.createGeoJson( activeRoute );
      if (this.bbox) {
        this.yagaMap.fitBounds( this.bbox );
      }
    } );
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
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
