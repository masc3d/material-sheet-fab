import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { TourService } from '../tour.service';
import { Position } from '../position.model';
import { Subscription } from 'rxjs/Subscription';
import { MapComponent } from '@yaga/leaflet-ng2';

@Component( {
  selector: 'app-tour-map',
  template: `
    <yaga-map #yagaMap [lat]="50.8645" [lng]="9.6917" [zoom]="11">
      <yaga-zoom-control></yaga-zoom-control>
      <yaga-scale-control [metric]="true" [imperial]="false"></yaga-scale-control>
      <yaga-attribution-control></yaga-attribution-control>
      <!--<yaga-tile-layer [url]="'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'"-->
      <yaga-tile-layer [url]="'http://192.168.161.202:8080/styles/osm-bright/rendered/{z}/{x}/{y}.png'"
                       [attribution]="'© OpenStreetMap-Mitwirkende'"></yaga-tile-layer>
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

  @ViewChild( 'yagaMap' )
  yagaMap: MapComponent;

  private subscriptionDisplay: Subscription;
  private subscriptionMarker: Subscription;

  constructor( private tourService: TourService ) {
  }

  ngOnInit(): void {
    console.log( 'yagaMap', this.yagaMap );

    this.subscriptionDisplay = this.tourService.displayMarker.subscribe( ( displayMarker: boolean ) => {
      this.displayMarker = displayMarker;
    } );

    this.subscriptionMarker = this.tourService.activeMarker.subscribe( ( activeMarker: Position ) => {
      this.markerLat = activeMarker.latitude;
      this.markerLng = activeMarker.longitude;
      this.yagaMap.flyTo( L.latLng( activeMarker.latitude, activeMarker.longitude ) );
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
}
