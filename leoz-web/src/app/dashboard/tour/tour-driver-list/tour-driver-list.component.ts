import { Component, OnInit } from '@angular/core';
import { Driver } from '../driver.model';
import { Subscription } from 'rxjs/Subscription';
import { TourService } from '../tour.service';
import { DriverService } from '../driver.service';
import { RoleGuard } from '../../../core/auth/role.guard';

@Component({
  selector: 'app-tour-driver-list',
  template: `
    <p-dataTable [value]="drivers">
      <p-column field="firstName" header="{{'firstname' | translate}}" [sortable]="true"></p-column>
      <p-column field="lastName" header="{{'surname' | translate}}"></p-column>
      <p-column field="email" header="{{'email' | translate}}"></p-column>
      <p-column header="">
        <ng-template let-user="rowData" pTemplate="body">
          <i class="fa fa-crosshairs fa-fw" aria-hidden="true" (click)="showPosition(driver)"></i>
        </ng-template>
      </p-column>
    </p-dataTable>
  `
} )
export class TourDriverListComponent implements OnInit {
  drivers: Driver[];
  private subscription: Subscription;

  constructor(private driverService: DriverService,
              private tourService: TourService,
              private roleGuard: RoleGuard) {
  }

  ngOnInit() {
     this.subscription = this.driverService.drivers.subscribe((drivers: Driver[]) => {

       if (this.roleGuard.userRole === Driver.RoleEnum.DRIVER) {
         // this.drivers = drivers.filter( item => item.email === currUserEmail );
         this.drivers = drivers.filter( (driver: Driver) => driver.email === 'driver@deku.org' );
       } else {
         this.drivers = drivers.filter( (driver: Driver) => driver.role === Driver.RoleEnum.DRIVER );
       }

       });
    this.driverService.getDrivers();
  }

  showPosition(driver: Driver) {
    this.tourService.changeActiveMarker(driver);
  }
}
