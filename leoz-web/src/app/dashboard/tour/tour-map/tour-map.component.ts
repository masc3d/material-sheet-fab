import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { TourService } from '../tour.service';
import { Position } from '../position.model';
import { Subscription } from 'rxjs/Subscription';
import { GeoJSONDirective, MapComponent } from '@yaga/leaflet-ng2';
import { advanceActivatedRoute } from '@angular/router/src/router_state';
import { element } from 'protractor';

@Component( {
  selector: 'app-tour-map',
  template: `
    <yaga-map #yagaMap [lat]="50.8645" [lng]="9.6917" [zoom]="11">
      <yaga-zoom-control></yaga-zoom-control>
      <yaga-scale-control [metric]="true" [imperial]="false"></yaga-scale-control>
      <yaga-attribution-control></yaga-attribution-control>
      <!--<yaga-tile-layer [url]="'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'"-->
      <yaga-tile-layer [url]="'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'"
                       [attribution]="'© OpenStreetMap-Mitwirkende'"></yaga-tile-layer>
      <!--<yaga-tile-layer [url]="'http://192.168.161.202:8080/styles/osm-bright/rendered/{z}/{x}/{y}.png'"-->
      <!--[attribution]="'© OpenStreetMap-Mitwirkende'"></yaga-tile-layer>-->
      <yaga-geojson [data]="routeGeoJson"></yaga-geojson>
      <yaga-marker [lat]="markerLat" [lng]="markerLng" [display]="displayMarker">
        <yaga-popup>
          <p>
            Latitude: {{markerLat}}<br/>
            Longitude: {{markerLng}}
          </p>
        </yaga-popup>
      </yaga-marker>
    </yaga-map>`
} )
export class TourMapComponent implements OnInit, OnDestroy {

  markerLat: number;
  markerLng: number;
  displayMarker: boolean;
  name: string;
  routeGeoJson: any;

  @ViewChild( 'yagaMap' )
  yagaMap: MapComponent;

  private subscriptionDisplay: Subscription;
  private subscriptionMarker: Subscription;

  private subscriptionDisplayRoute: Subscription;
  private subscriptionRoute: Subscription;
  private bbox: L.LatLngBounds;

  constructor( private tourService: TourService ) {
  }

  ngOnInit(): void {
    console.log( 'yagaMap', this.yagaMap );

    this.subscriptionDisplay = this.tourService.displayMarker.subscribe( ( displayMarker: boolean ) => {
      this.displayMarker = displayMarker;
    } );

    this.subscriptionDisplayRoute = this.tourService.displayRoute.subscribe( ( displayRoute: boolean ) => {
      if (!displayRoute) {
        this.routeGeoJson = this.createGeoJson( [] );
      }
    } );

    this.subscriptionMarker = this.tourService.activeMarker.subscribe( ( activeMarker: Position ) => {
      this.markerLat = activeMarker.latitude;
      this.markerLng = activeMarker.longitude;
      this.yagaMap.flyTo( L.latLng( activeMarker.latitude, activeMarker.longitude ) );
    } );

    this.subscriptionRoute = this.tourService.activeRoute.subscribe( ( activeRoute: Position[] ) => {
      this.routeGeoJson = this.createGeoJson( activeRoute );
      if (this.bbox) {
        this.yagaMap.fitBounds( this.bbox );
      }
    } );
  }

  ngOnDestroy(): void {
    if (this.subscriptionDisplay) {
      this.subscriptionDisplay.unsubscribe();
    }
    if (this.subscriptionMarker) {
      this.subscriptionMarker.unsubscribe();
    }
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
