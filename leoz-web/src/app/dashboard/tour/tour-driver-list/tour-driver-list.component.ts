import { Component, OnDestroy, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/timer';

import { Driver } from '../driver.model';
import { TourService } from '../tour.service';
import { DriverService } from '../driver.service';
import { RoleGuard } from '../../../core/auth/role.guard';
import { UserService } from '../../user/user.service';
import { SelectItem } from 'primeng/primeng';
import { Subscription } from 'rxjs/Subscription';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

@Component( {
  selector: 'app-tour-driver-list',
  template: `
    <div class="ui-fluid" *ngIf="isPermitted" style="margin-bottom: 10px">
      <div class="ui-g">
        <div class="ui-g-12 no-pad">
          <button pButton type="button" (click)="allUsers()" label="{{'allusers' | translate}}"
          style="width:150px"></button>
          <button pButton type="button" (click)="justDrivers()" label="{{'drivers' | translate}}"
          style="width:150px"></button>
        </div>
        <div class="ui-g-12 ui-lg-4 no-pad">
          {{'refreshevery' | translate}}
          <p-dropdown [options]="refreshOptions" [(ngModel)]="selectedRefresh"
          (onChange)="changeRefreshRate()"></p-dropdown>
          {{'mins' | translate}}
        </div>
        <div class="ui-g-12 ui-lg-3 no-pad">
          {{'last' | translate}}
          <p-dropdown [options]="intervalOptions" [(ngModel)]="selectedInterval"
          (onChange)="changeInterval()"></p-dropdown>
          {{'hs' | translate}}
        </div>
        <div class="ui-g-12 ui-lg-5 no-pad">
          {{'latestRefresh' | translate}} {{latestRefresh | date:dateFormatEvenLonger}}
        </div>
      </div>
    </div>

    <p-dataTable [value]="drivers | async | driverfilter: [filterName]" resizableColumns="true" [responsive]="true">
      <p-column field="firstName" header="{{'firstname' | translate}}"></p-column>
      <p-column field="lastName" header="{{'surname' | translate}}" [sortable]="true"></p-column>
      <p-column field="phone" header="{{'phoneoffice' | translate}}" [sortable]="true"></p-column>
      <p-column field="mobile" header="{{'phonemobile' | translate}}" [sortable]="true"></p-column>
      <p-column header="">
        <ng-template let-driver="rowData" pTemplate="body">
          <i class="fa fa-crosshairs fa-fw" aria-hidden="true" (click)="showPositionPeriodically(driver)"></i>
          <i class="fa fa-road fa-fw" aria-hidden="true" (click)="showRoutePeriodically(driver)"></i>
        </ng-template>
      </p-column>
    </p-dataTable>
  `
} )
export class TourDriverListComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {
  intervalOptions: SelectItem[];
  refreshOptions: SelectItem[];
  selectedInterval = '>24';
  selectedRefresh = 10;

  latestRefresh: Date;
  drivers: Observable<Driver[]>;
  isPermitted: boolean;
  filterName: string;

  private refreshTimer: Observable<number>;
  private subscription: Subscription;
  private periodicallyUsedDriver: Driver;
  private periodicallyUsedCallback: Function;

  constructor( private driverService: DriverService,
               private tourService: TourService,
               private userService: UserService,
               protected translate: TranslateService,
               private roleGuard: RoleGuard ) {
    super( translate );
  }

  ngOnInit() {
    super.ngOnInit();
    this.intervalOptions = [
      { label: '>24', value: '>24' },
      { label: '1', value: '1' },
      { label: '2', value: '2' },
      { label: '6', value: '6' },
      { label: '12', value: '12' },
      { label: '24', value: '24' } ];

    this.refreshOptions = [
      { label: '1', value: 1 },
      { label: '5', value: 5 },
      { label: '10', value: 10 },
      { label: '15', value: 15 },
      { label: '30', value: 30 },
      { label: '60', value: 60 } ];

    this.drivers = this.driverService.drivers;
    this.driverService.getDrivers();
    this.isPermitted = (this.roleGuard.isPoweruser() || this.roleGuard.isUser());
    this.filterName = 'driverfilter';
  }

  ngOnDestroy() {
    super.ngOnDestroy();
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  justDrivers() {
    this.driverService.getDrivers();
    this.filterName = 'driverfilter';
  }

  allUsers() {
    this.userService.getUsers();
    this.filterName = 'userfilter';
  }

  changeInterval() {
    this.restartShowPeriodically();
  }

  changeRefreshRate() {
    this.restartShowPeriodically();
  }

  private restartShowPeriodically() {
    if (this.periodicallyUsedDriver && this.periodicallyUsedCallback) {
      this.showPeriodically();
    }
  }

  private showPeriodically() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    this.refreshTimer = Observable.timer( 0, this.selectedRefresh * 60 * 1000 );
    this.subscription = this.refreshTimer.subscribe( ( tick: number ) => {
      this.periodicallyUsedCallback( this.periodicallyUsedDriver, this.tourService, this.selectedInterval );
      this.latestRefresh = new Date();
    } );
  }

  showPositionPeriodically( driver: Driver ) {
    this.periodicallyUsedDriver = driver;
    this.periodicallyUsedCallback = this.showPosition;
    this.showPeriodically();
  }

  showRoutePeriodically( driver: Driver ) {
    this.periodicallyUsedDriver = driver;
    this.periodicallyUsedCallback = this.showRoute;
    this.showPeriodically();
  }

  showPosition( driver: Driver, tourService: TourService ) {
    tourService.resetDisplay();
    tourService.changeActiveMarker( driver );
  }

  showRoute( driver: Driver, tourService: TourService, selectedInterval ) {
    const asInt = Number.parseInt( selectedInterval, 10 );
    const duration = asInt ? String( asInt * 60 ) : '300000';
    tourService.resetDisplay();
    tourService.changeActiveRoute( driver, duration );
  }
}
