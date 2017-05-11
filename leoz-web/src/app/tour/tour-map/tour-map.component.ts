import { Component, OnInit } from '@angular/core';
import { TourService } from '../tour.service';
import { Driver, Position } from '../../driver/driver.model';

@Component({
  selector: 'app-tour-map',
  templateUrl: './tour-map.component.html'
})
export class TourMapComponent implements OnInit {

  lat: number;
  lng: number;
  displayMarker: boolean;
  name: string;

  constructor(private tourService: TourService) {
  }

  ngOnInit() {
    this.lat = 48.57;
    this.lng = 13.26;
    this.displayMarker = false;
    this.name = '';

    this.tourService.activeDriverMarker.subscribe((activeDriverMarker: Driver) => {
      this.displayMarker = activeDriverMarker.position instanceof Position;
      if (this.displayMarker) {
        this.name = activeDriverMarker.surname;
        this.lat = activeDriverMarker.position.lat;
        this.lng = activeDriverMarker.position.lng;
      }
    });
  }
}
