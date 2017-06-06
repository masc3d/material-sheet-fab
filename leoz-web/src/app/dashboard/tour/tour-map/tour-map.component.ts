import { Component, OnDestroy, OnInit } from '@angular/core';
import { TourService } from '../tour.service';
import { Position } from '../position.model';
import { Subscription } from 'rxjs/Subscription';

@Component( {
  selector: 'app-tour-map',
  template: `
    <yaga-map [lat]="latitude" [lng]="longitude" [zoom]="11">
      <yaga-zoom-control></yaga-zoom-control>
      <yaga-scale-control [metric]="true" [imperial]="false"></yaga-scale-control>
      <yaga-attribution-control></yaga-attribution-control>
      <yaga-tile-layer [url]="'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'"
                       [attribution]="'© OpenStreetMap-Mitwirkende'"></yaga-tile-layer>
      <yaga-marker [lat]="latitude" [lng]="longitude" [display]="displayMarker">
        <yaga-popup>
          <p>
            Latitude: {{latitude}}<br/>
            Longitude: {{longitude}}
          </p>
        </yaga-popup>
      </yaga-marker>
    </yaga-map>`
} )
export class TourMapComponent implements OnInit, OnDestroy {
  latitude: number;

  longitude: number;
  displayMarker: boolean;
  name: string;

  private subscriptionDisplay: Subscription;
  private subscriptionMarker: Subscription;

  constructor( private tourService: TourService ) {
  }

  ngOnInit(): void {
    this.latitude = 50.8645;
    this.longitude = 9.6917;

    this.subscriptionDisplay = this.tourService.displayMarker.subscribe( ( displayMarker: boolean ) => this.displayMarker = displayMarker );

    this.subscriptionMarker = this.tourService.activeMarker.subscribe( ( activeMarker: Position ) => {
      this.latitude = activeMarker.latitude;
      this.longitude = activeMarker.longitude;
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
