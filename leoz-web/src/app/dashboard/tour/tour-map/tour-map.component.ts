import { Component, OnInit } from '@angular/core';
import { TourService } from '../tour.service';
import { Position } from '../position.model';

@Component( {
  selector: 'app-tour-map',
  templateUrl: './tour-map.component.html'
} )
export class TourMapComponent implements OnInit {

  lat: number;
  long: number;
  displayMarker: boolean;
  name: string;

  constructor( private tourService: TourService ) {
  }

  ngOnInit() {
    this.lat = 48.57;
    this.long = 13.26;
    this.displayMarker = false;
    // this.name = '';

    this.tourService.activeMarker.subscribe( ( activeMarker: Position ) => {
      console.log( '-------- activeMarker', activeMarker );
      this.displayMarker = activeMarker.lat > 0;
      if (this.displayMarker) {
        // this.name = activeDriverMarker.lastName;
        this.lat = activeMarker.lat;
        this.long = activeMarker.long;
      }
    } );
  }
}
