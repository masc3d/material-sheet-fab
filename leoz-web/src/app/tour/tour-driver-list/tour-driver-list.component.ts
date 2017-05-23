import { Component, OnInit } from '@angular/core';
import { DriverService } from '../driver.service';
import { Driver } from '../driver.model';
import { TourService } from '../tour.service';
import { Observable } from 'rxjs/Observable';
import { TranslateService } from '../../translate/translate.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-tour-driver-list',
  templateUrl: './tour-driver-list.component.html'
})
export class TourDriverListComponent implements OnInit {
  drivers: Observable<Driver[]>;

  constructor(private driverService: DriverService,
              private tourService: TourService,
              private translateService: TranslateService) {
  }

  ngOnInit() {
    this.drivers = this.driverService.getDrivers();
    this.translateService.use(`${environment.defLang}`);
  }

  showPosition(driver: Driver) {
    this.tourService.changeActiveDriverMarker(driver);
  }
}
