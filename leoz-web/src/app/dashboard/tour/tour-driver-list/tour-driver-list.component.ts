import { Component, OnInit } from '@angular/core';
import { Driver } from '../driver.model';
import { TourService } from '../tour.service';
import { Observable } from 'rxjs/Observable';
import { DriverService } from '../driver.service';

@Component({
  selector: 'app-tour-driver-list',
  templateUrl: './tour-driver-list.component.html'
})
export class TourDriverListComponent implements OnInit {
  drivers: Observable<Driver[]>;

  constructor(private driverService: DriverService,
              private tourService: TourService) {
  }

  ngOnInit() {
    this.drivers = this.driverService.drivers;
    this.driverService.getDrivers();
  }

  showPosition(driver: Driver) {
    this.tourService.changeActiveMarker(driver);
    console.log('showPosition', driver);
  }
}
