import { Component, OnInit } from '@angular/core';
import { TourService } from '../tour.service';
import { Position } from '../position.model';

@Component( {
  selector: 'app-tour-map',
  templateUrl: './tour-map.component.html'
} )
export class TourMapComponent implements OnInit {

  latitude: number;
  longitude: number;
  displayMarker: boolean;
  name: string;

  constructor( private tourService: TourService ) {
  }

  ngOnInit() {
    this.latitude = 48.57;
    this.longitude = 13.26;
    this.displayMarker = false;
    // this.name = '';

    this.tourService.activeMarker.subscribe( ( activeMarker: Position ) => {
      // console.log( '-------- activeMarker', activeMarker );
      this.displayMarker = activeMarker.latitude > 0;
      if (this.displayMarker) {
        // this.name = activeDriverMarker.lastName;
        this.latitude = activeMarker.latitude;
        this.longitude = activeMarker.longitude;
      }
    } );
  }
}
