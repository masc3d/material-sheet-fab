import { Component, OnDestroy, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import 'rxjs/add/observable/timer';

import { SelectItem } from 'primeng/primeng';

import { Driver } from '../driver.model';
import { TourService } from '../tour.service';
import { DriverService } from '../driver.service';
import { RoleGuard } from '../../../core/auth/role.guard';
import { UserService } from '../../user/user.service';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

interface CallbackArguments {
  driver: Driver;
  tourService: TourService;
  interval: string;
}

@Component( {
  selector: 'app-tour-driver-list',
  template: `
    <div class="ui-fluid" style="margin-bottom: 10px">
      <div class="ui-g">
        <div class="ui-g-12 no-pad">
          <button *ngIf="isPermitted" pButton type="button" (click)="allUsers()" label="{{'allusers' | translate}}"
                  style="width:190px"></button>
          <button *ngIf="isPermitted" pButton type="button" (click)="allDrivers()" label="{{'alldrivers' | translate}}"
                  style="width:190px"></button>
          <button pButton type="button" (click)="toggleVisibility()" label="{{'showlist' | translate}}"
                  style="width:190px"></button>
        </div>

        <div class="ui-g-12 ui-lg-5 no-pad">
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
        <div class="ui-g-12 ui-lg-4 no-pad">
          <div style="width:130px;" *ngIf="calendarIsVisible">
            <p-calendar [showIcon]="true" [readonlyInput]="true" [dateFormat]="dateFormatPrimeng" [locale]="locale"
                        [(ngModel)]="tourDate" (onSelect)="changeTourDate()" [maxDate]="maxDateValue"
                        [selectOtherMonths]="true"></p-calendar>
          </div>
        </div>
        <div class="ui-g-12 ui-lg-5 no-pad">
          {{'latestRefresh' | translate}}: {{latestRefresh | dateMomentjs:dateFormatEvenLonger}}
        </div>
        <div class="ui-g-12 ui-lg-7 no-pad">
          {{'selectedformap' | translate}}: {{displayedMapmode | translate}}
        </div>
      </div>
    </div>
    <p-dataTable *ngIf="tableIsVisible" [value]="drivers | async | driverfilter: [filterName]" resizableColumns="true"
                 [responsive]="true" sortField="lastName" [sortOrder]="1">
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
  selectedInterval = 24;
  selectedRefresh = 10;
  tourDate = null;

  maxDateValue: Date;
  latestRefresh: Date;
  drivers: Observable<Driver[]>;
  isPermitted: boolean;
  filterName: string;
  tableIsVisible: boolean;
  calendarIsVisible: boolean;
  dateFormatPrimeng: string;
  locale: any;

  private refreshTimer: Observable<number>;
  private subscription: Subscription;
  private periodicallyUsedDriver: Driver;
  private periodicallyUsedCallback: Function;
  private periodicallyUsedFilter: string;
  private displayedMapmode: string;
  private selectedLocationFlag: string;

  constructor( private driverService: DriverService,
               private tourService: TourService,
               private userService: UserService,
               protected translate: TranslateService,
               private roleGuard: RoleGuard ) {
    super( translate, () => {
      this.intervalOptions = this.createIntervalOptions();
      if (this.tourDate) {
        this.tourDate = new Date( this.tourDate );
      }
    } );
  }

  createIntervalOptions() {
    return [
      { label: '1', value: 1 },
      { label: '2', value: 2 },
      { label: '6', value: 6 },
      { label: '12', value: 12 },
      { label: '24', value: 24 },
      { label: this.translate.instant( 'day' ), value: 0 } ];
  }

  ngOnInit() {
    super.ngOnInit();
    this.intervalOptions = this.createIntervalOptions();
    this.maxDateValue = new Date();

    this.refreshOptions = [
      { label: '1', value: 1 },
      { label: '5', value: 5 },
      { label: '10', value: 10 },
      { label: '15', value: 15 },
      { label: '30', value: 30 },
      { label: '60', value: 60 } ];

    this.drivers = this.driverService.drivers;
    this.isPermitted = (this.roleGuard.isPoweruser() || this.roleGuard.isUser());
    this.tourService.resetMarkerAndRoute();
    this.tableIsVisible = false;
    this.calendarIsVisible = false;
    if (this.isPermitted) {
      this.allDrivers();
    } else if (this.roleGuard.isDriver()) {
      this.driverService.currentDriver
        .takeUntil( this.ngUnsubscribe )
        .subscribe( ( currentDriver: Driver ) => {
          // empty Driver object could be returned
          if (currentDriver.role) {
            this.showPositionPeriodically( currentDriver );
          }
        } );
      this.driverService.getDrivers();
    }
  }

  ngOnDestroy() {
    super.ngOnDestroy();
    this.tourService.resetMsgs();
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  allDrivers() {
    this.tableIsVisible = false;
    this.driverService.getDrivers();
    this.filterName = 'driverfilter';
    this.displayedMapmode = 'alldrivers';
    this.selectedLocationFlag = null;
    if (this.selectedInterval > 0) {
      this.clearTimerMapdisplay();
      this.showAllPositionsPeriodically( 'alldrivers' );
    } else {
      this.changeTourDate();
    }
  }

  toggleVisibility(): void {
    this.tableIsVisible = !this.tableIsVisible;
  }

  allUsers() {
    this.tableIsVisible = false;
    this.userService.getUsers();
    this.filterName = 'userfilter';
    this.displayedMapmode = 'allusers';
    this.selectedLocationFlag = null;
    if (this.selectedInterval > 0) {
      this.clearTimerMapdisplay();
      this.showAllPositionsPeriodically( 'allusers' );
    } else {
      this.changeTourDate();
    }
  }

  private clearTimerMapdisplay() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    this.latestRefresh = null;
    this.periodicallyUsedDriver = null;
    this.periodicallyUsedCallback = null;
    this.periodicallyUsedFilter = null;
    this.tourService.resetDisplay();
    this.tourService.resetMarkerAndRoute();
  }

  changeInterval() {
    if (this.selectedInterval > 0) {
      this.calendarIsVisible = false;
      this.tourDate = null;
      this.restartShowPeriodically();
    } else {
      if (this.subscription) {
        this.subscription.unsubscribe();
      }
      this.calendarIsVisible = true;
    }
  }

  changeTourDate() {
    if (this.tourDate) {
      if (this.subscription) {
        this.subscription.unsubscribe();
      }
      this.latestRefresh = new Date();
      if (this.displayedMapmode === 'alldrivers' || this.displayedMapmode === 'allusers') {
        this.tourService.fetchAllPositions( this.displayedMapmode, 0, this.tourDate );
      } else {
        if (this.selectedLocationFlag === 'position') {
          this.tourService.changeActiveMarker( this.periodicallyUsedDriver, 0, this.tourDate );
        } else {
          this.tourService.changeActiveRoute( this.periodicallyUsedDriver, 0, this.tourDate );
        }
      }
    }
  }

  changeRefreshRate() {
    if (this.selectedInterval > 0) {
      this.restartShowPeriodically();
    }
  }

  private restartShowPeriodically() {
    if ((this.periodicallyUsedDriver && this.periodicallyUsedCallback)
      || (this.periodicallyUsedFilter && this.periodicallyUsedCallback)) {
      this.showPeriodically();
    }
  }

  private showPeriodically() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    this.refreshTimer = Observable.timer( 0, this.selectedRefresh * 60 * 1000 );
    this.subscription = this.refreshTimer.subscribe( ( tick: number ) => {
      this.periodicallyUsedCallback( {
        driver: this.periodicallyUsedDriver,
        tourService: this.tourService,
        interval: this.selectedInterval
      } );
      this.latestRefresh = new Date();
    } );
  }

  showAllPositionsPeriodically( filterUserType: string ) {
    this.periodicallyUsedFilter = filterUserType;
    this.periodicallyUsedDriver = null;
    this.periodicallyUsedCallback = this.showAllPositions;
    this.showPeriodically();
  }

  showPositionPeriodically( driver: Driver ) {
    this.toggleVisibility();
    this.periodicallyUsedFilter = null;
    this.periodicallyUsedDriver = driver;
    this.periodicallyUsedCallback = this.showPosition;
    this.showPeriodically();
    this.displayedMapmode = `${this.periodicallyUsedDriver.firstName} ${this.periodicallyUsedDriver.lastName}`;
    this.selectedLocationFlag = 'position';
  }

  showRoutePeriodically( driver: Driver ) {
    this.toggleVisibility();
    this.periodicallyUsedFilter = null;
    this.periodicallyUsedDriver = driver;
    this.periodicallyUsedCallback = this.showRoute;
    this.showPeriodically();
    this.displayedMapmode = `${this.periodicallyUsedDriver.firstName} ${this.periodicallyUsedDriver.lastName}`;
    this.selectedLocationFlag = 'route';
  }

  showAllPositions( args: CallbackArguments ) {
    args.tourService.fetchAllPositions( this.periodicallyUsedFilter, this.selectedInterval * 60, this.tourDate );
  }

  showPosition( args: CallbackArguments ) {
    args.tourService.changeActiveMarker( args.driver, this.selectedInterval * 60, this.tourDate );
  }

  showRoute( args: CallbackArguments ) {
    args.tourService.changeActiveRoute( args.driver, this.selectedInterval * 60, this.tourDate );
  }
}
