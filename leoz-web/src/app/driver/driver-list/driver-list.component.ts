import { Component, OnInit } from '@angular/core';
import {DriverService} from '../driver.service';
import {Driver} from '../driver.model';
import { Observable } from 'rxjs/Observable';
import { ErrormsgService } from '../../error/errormsg.service';

@Component({
  selector: 'app-driver-list',
  templateUrl: './driver-list.component.html',
  providers: [ErrormsgService]
})
export class DriverListComponent implements OnInit {

  drivers: Observable<Driver[]>;

  constructor(private driverService: DriverService) { }

  ngOnInit() {
    this.drivers = this.driverService.getDrivers();
  }

  selected(selectedDriver: Driver) {
    this.driverService.changeActiveDriver(selectedDriver);
  }

}
