import { Component, OnInit } from '@angular/core';
import { DriverService } from '../../driver/driver.service';
import { Driver } from '../../driver/driver.model';
import { TourService } from '../tour.service';
import { Observable } from 'rxjs/Observable';

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
    this.drivers = this.driverService.getDrivers();
  }

  showPosition(driver: Driver) {
    this.tourService.changeActiveDriverMarker(driver);
  }
}
