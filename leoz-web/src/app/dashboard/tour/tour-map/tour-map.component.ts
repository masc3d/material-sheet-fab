import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import 'rxjs/add/operator/takeUntil';

import { MapComponent } from '@yaga/leaflet-ng2';
import * as L from 'leaflet';

import { TourService } from '../tour.service';
import { Position } from '../position.model';
import { MarkerModel } from './marker.model';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { DateMomentjsPipe } from '../../../core/translate/date-momentjs.pipe';
import { GeoJsonTypes } from 'geojson';
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-tour-map',
  template: `
    <yaga-map #yagaMap [lat]="50.8645" [lng]="9.6917" [zoom]="11">
      <yaga-zoom-control></yaga-zoom-control>
      <yaga-scale-control [metric]="true" [imperial]="false"></yaga-scale-control>
      <yaga-attribution-control></yaga-attribution-control>
      <yaga-tile-layer [url]="'https://tiles.derkurier.de/styles/osm-bright/rendered/{z}/{x}/{y}.png'"
                       [attribution]="'© OpenStreetMap-Mitwirkende'"></yaga-tile-layer>
      <yaga-marker [lat]="markerLat" [lng]="markerLng" [display]="displayMarker">
        <yaga-popup>
          <p>
            {{'name' | translate}}: {{markerName}}<br/>
            {{'vehicle' | translate}}: {{markerVehicle | translate}}<br/>
            {{'phoneoffice' | translate}}: {{markerPhoneoffice}}<br/>
            {{'phonemobile' | translate}}: {{markerPhonemobile}}<br/>
            {{'lastactivity' | translate}}: {{markerLastactivity | dateMomentjs:dateFormatLong}}
          </p>
        </yaga-popup>
        <yaga-icon [iconUrl]="iconUrl" [iconSize]="iconSize"></yaga-icon>
      </yaga-marker>
    </yaga-map>`,
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TourMapComponent extends AbstractTranslateComponent implements OnInit {

  dateFormatLong: string;

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
  iconSize: L.Point;
  iconUrl: string;

  @ViewChild( 'yagaMap' )
  yagaMap: MapComponent;

  private bbox: L.LatLngBounds;
  private allCustomMarkers: L.Marker[];
  private geoJsonLayer: L.GeoJSON;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               private tourService: TourService,
               private datePipe: DateMomentjsPipe ) {
    super( translate, cd, msgService );
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.allCustomMarkers = [];
    this.tourService.displayMarker$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( displayMarker: boolean ) => {
        this.displayMarker = displayMarker;
        if (!displayMarker) {
          this.yagaMap.closePopup();
        }
      } );

    this.tourService.allMarkers$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( allMarkers: MarkerModel[] ) => {
        this.removeAllCustomMarkers();
        if (allMarkers.length > 0) {
          let latMin;
          let latMax;
          let lngMin;
          let lngMax;
          allMarkers.forEach(
            ( markerModel: MarkerModel ) => {
              this.addMarkerToMap( markerModel );
              latMin = !latMin || markerModel.position.latitude < latMin ? markerModel.position.latitude : latMin;
              latMax = !latMax || markerModel.position.latitude > latMax ? markerModel.position.latitude : latMax;
              lngMin = !lngMin || markerModel.position.longitude < lngMin ? markerModel.position.longitude : lngMin;
              lngMax = !lngMax || markerModel.position.longitude > lngMax ? markerModel.position.longitude : lngMax;

            }
          );
          this.bbox = L.latLngBounds( [ latMin, lngMin ], [ latMax, lngMax ] );
          this.yagaMap.fitBounds( this.bbox );
        }
      } );

    this.tourService.displayRoute$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( displayRoute: boolean ) => {
        if (!displayRoute) {
          this.routeGeoJson = this.createGeoJson( [] );
        }
      } );

    this.tourService.activeMarker$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeMarker: MarkerModel ) => {
        switch (activeMarker.position.vehicleType) {
          case Position.VehicleType.BIKE:
            this.iconUrl = 'assets/css/images/bike-icon.png';
            this.iconSize = new L.Point( 32, 32 );
            this.markerVehicle = 'bike';
            break;
          case Position.VehicleType.CAR:
            this.iconUrl = 'assets/css/images/car-icon.png';
            this.iconSize = new L.Point( 32, 32 );
            this.markerVehicle = 'car';
            break;
          case Position.VehicleType.VAN:
            this.iconUrl = 'assets/css/images/van-icon.png';
            this.iconSize = new L.Point( 32, 32 );
            this.markerVehicle = 'van';
            break;
          case Position.VehicleType.TRUCK:
            this.iconUrl = 'assets/css/images/truck-icon.png';
            this.iconSize = new L.Point( 32, 32 );
            this.markerVehicle = 'truck';
            break;
          default:
            this.iconUrl = 'assets/css/images/marker-icon.png';
            this.iconSize = new L.Point( 25, 41 );
            this.markerVehicle = 'unknown';
            break;
        }
        this.markerLat = activeMarker.position.latitude;
        this.markerLng = activeMarker.position.longitude;
        this.markerName = `${activeMarker.driver.firstName} ${activeMarker.driver.lastName}`;
        this.markerPhoneoffice = activeMarker.driver.phone;
        this.markerPhonemobile = activeMarker.driver.phoneMobile;
        this.markerLastactivity = activeMarker.position.time;
        this.yagaMap.flyTo( L.latLng( activeMarker.position.latitude, activeMarker.position.longitude ) );
      } );

    this.tourService.activeRoute$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeRoute: Position[] ) => {
        this.routeGeoJson = this.createGeoJson( activeRoute );
        if (this.bbox) {
          this.yagaMap.fitBounds( this.bbox );
        }
      } );
  }

  addMarkerToMap( markerModel: MarkerModel ) {
    let customIcon;
    let markerVehicle;
    switch (markerModel.position.vehicleType) {
      case Position.VehicleType.BIKE:
        customIcon = L.icon( {
          iconUrl: 'assets/css/images/bike-icon.png',
          iconSize: [ 32, 32 ]
        } );
        markerVehicle = 'bike';
        break;
      case Position.VehicleType.CAR:
        customIcon = L.icon( {
          iconUrl: 'assets/css/images/car-icon.png',
          iconSize: [ 32, 32 ]
        } );
        markerVehicle = 'car';
        break;
      case Position.VehicleType.VAN:
        customIcon = L.icon( {
          iconUrl: 'assets/css/images/van-icon.png',
          iconSize: [ 32, 32 ]
        } );
        markerVehicle = 'van';
        break;
      case Position.VehicleType.TRUCK:
        customIcon = L.icon( {
          iconUrl: 'assets/css/images/truck-icon.png',
          iconSize: [ 32, 32 ]
        } );
        markerVehicle = 'truck';
        break;
      default:
        customIcon = L.icon( {
          iconUrl: 'assets/css/images/marker-icon.png',
          iconSize: [ 32, 32 ]
        } );
        markerVehicle = 'unknown';
        break;
    }
    const popupContent = `
    <p>
      ${this.translate.instant( 'name' )}: ${markerModel.driver.firstName} ${markerModel.driver.lastName}<br/>
      ${this.translate.instant( 'vehicle' )}: ${this.translate.instant( markerVehicle )}<br/>
      ${this.translate.instant( 'phoneoffice' )}: ${markerModel.driver.phone}<br/>
      ${this.translate.instant( 'phonemobile' )}: ${markerModel.driver.phoneMobile}<br/>
      ${this.translate.instant( 'lastactivity' )}: ${this.datePipe.transform( markerModel.position.time, this.dateFormatLong )}
    </p>
    `;

    const customMarker = new L.Marker( [ markerModel.position.latitude, markerModel.position.longitude ], { icon: customIcon } );
    customMarker.bindPopup( popupContent ).addTo( this.yagaMap );
    this.allCustomMarkers.push( customMarker );
  }

  removeAllCustomMarkers() {
    this.allCustomMarkers.forEach(
      ( customMarker: L.Marker ) => this.yagaMap.removeLayer( customMarker )
    );
    this.allCustomMarkers = [];
  }

  private createGeoJson( activeRoute: Position[] ): any {
    // remove previous geojson layer
    if (this.geoJsonLayer) {
      this.yagaMap.removeLayer( this.geoJsonLayer );
    }
    let geoJson = {
      'type': <GeoJsonTypes>'FeatureCollection',
      'features': []
    };
    if (activeRoute && activeRoute.length > 0) {
      const coordinates = [];
      const marker = [];
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
        marker.push( {
          'type': 'Feature',
          'properties': {
            'name': 'start',
            'popupContent': this.datePipe.transform( waypoint.time, this.dateFormatLong )
          },
          'geometry': {
            'type': 'Point',
            'coordinates': [ waypoint.longitude, waypoint.latitude, 0.0 ]
          }
        } )
      }
      this.bbox = L.latLngBounds( [ latMin, lngMin ], [ latMax, lngMax ] );
      const features = [ {
        'type': 'Feature',
        'geometry': {
          'type': 'LineString',
          'coordinates': coordinates
        }
      }, ...marker ];
      geoJson = {
        'type': <GeoJsonTypes>'FeatureCollection',
        'features': features
      };

      this.geoJsonLayer = L.geoJSON( geoJson, {
        onEachFeature: function ( feature, layer ) {
          if (feature.properties && feature.properties[ 'popupContent' ]) {
            layer.bindPopup( feature.properties[ 'popupContent' ], { closeButton: false, offset: L.point( 0, -20 ) } );
            layer.on( 'mouseover', function () {
              layer.openPopup();
            } );
            layer.on( 'mouseout', function () {
              layer.closePopup();
            } );
          }
        },

        pointToLayer: function ( feature, latlng ) {
          return L.circleMarker( latlng, { radius: 2 } );
        }
      } );

      this.yagaMap.addLayer( this.geoJsonLayer );
      this.yagaMap.fitBounds( this.geoJsonLayer.getBounds(), { padding: [ 0, 0 ] } );
    }
    // return geoJson;
  }
}
