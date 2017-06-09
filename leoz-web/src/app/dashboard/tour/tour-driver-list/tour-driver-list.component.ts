import { Component, OnInit } from '@angular/core';
import { Driver } from '../driver.model';
import { TourService } from '../tour.service';
import { DriverService } from '../driver.service';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-tour-driver-list',
  template: `
    <p-dataTable [value]="drivers | async | driverfilter" resizableColumns="true">
      <p-column field="firstName" header="{{'firstname' | translate}}"></p-column>
      <p-column field="lastName" header="{{'surname' | translate}}" [sortable]="true"></p-column>
      <p-column field="email" header="{{'email' | translate}}" [sortable]="true"></p-column>
      <p-column header="">
        <ng-template let-driver="rowData" pTemplate="body">
          <i class="fa fa-crosshairs fa-fw" aria-hidden="true" (click)="showPosition(driver)"></i>
          <i class="fa fa-road fa-fw" aria-hidden="true" (click)="showPosition(driver)"></i>
        </ng-template>
      </p-column>
    </p-dataTable>
  `
} )
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
  }
}
