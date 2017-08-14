import { Component, OnInit } from '@angular/core';
import { Driver } from '../driver.model';
import { TourService } from '../tour.service';
import { DriverService } from '../driver.service';
import { Observable } from 'rxjs/Observable';
import { RoleGuard } from '../../../core/auth/role.guard';
import { UserService } from '../../user/user.service';
import { SelectItem } from 'primeng/primeng';

@Component( {
  selector: 'app-tour-driver-list',
  template: `
    <div *ngIf="isPermitted" style="margin-bottom: 10px">
      <button pButton type="button" (click)="allUsers()" label="{{'allusers' | translate}}"
              style="width:150px"></button>
      <button pButton type="button" (click)="justDrivers()" label="{{'drivers' | translate}}"
              style="width:150px"></button>
      {{'last' | translate}}
      <p-dropdown [options]="intervalOptions"  [(ngModel)]="selectedInterval" ></p-dropdown>
      {{'hours' | translate}}
    </div>
    <p-dataTable [value]="drivers | async | driverfilter: [filterName]" resizableColumns="true" [responsive]="true">
      <p-column field="firstName" header="{{'firstname' | translate}}"></p-column>
      <p-column field="lastName" header="{{'surname' | translate}}" [sortable]="true"></p-column>
      <p-column field="email" header="{{'email' | translate}}" [sortable]="true"></p-column>
      <p-column header="">
        <ng-template let-driver="rowData" pTemplate="body">
          <i class="fa fa-crosshairs fa-fw" aria-hidden="true" (click)="showPosition(driver)"></i>
          <i class="fa fa-road fa-fw" aria-hidden="true" (click)="showRoute(driver)"></i>
        </ng-template>
      </p-column>
    </p-dataTable>
  `
} )
export class TourDriverListComponent implements OnInit {
  intervalOptions: SelectItem[];
  selectedInterval = '>24';

  drivers: Observable<Driver[]>;
  isPermitted: boolean;
  filterName: string;

  constructor( private driverService: DriverService,
               private tourService: TourService,
               private userService: UserService,
               private roleGuard: RoleGuard ) {
  }

  ngOnInit() {
    this.intervalOptions = [
      { label: '>24', value: '>24' },
      { label: '1', value: '1' },
      { label: '2', value: '2' },
      { label: '6', value: '6' },
      { label: '12', value: '12' },
      { label: '24', value: '24' } ];

    this.drivers = this.driverService.drivers;
    this.driverService.getDrivers();
    this.isPermitted = (this.roleGuard.isPoweruser() || this.roleGuard.isUser());
    this.filterName = 'driverfilter';
  }

  justDrivers() {
    this.driverService.getDrivers();
    this.filterName = 'driverfilter';
  }

  allUsers() {
    this.userService.getUsers();
    this.filterName = 'userfilter';
  }

  showPosition( driver: Driver ) {
    this.tourService.resetDisplay();
    this.tourService.changeActiveMarker( driver );
  }

  showRoute( driver: Driver ) {
    const asInt = Number.parseInt( this.selectedInterval, 10 );
    const duration = asInt ? String( asInt * 60 ) : '300000';
    this.tourService.resetDisplay();
    this.tourService.changeActiveRoute( driver, duration );
  }
}
