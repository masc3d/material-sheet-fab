import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { takeUntil } from 'rxjs/operators';

import { Message } from 'primeng/components/common/api';
import { SortEvent } from 'primeng/api';
import * as moment from 'moment';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';
import { TouroptimizingService } from '../touroptimizing.service';
import { Tour } from '../../../core/models/tour.model';
import { BrowserCheck } from '../../../core/auth/browser-check';
import { PrintingService } from '../../../core/printing/printing.service';
import { StoplistReportingService } from '../../../core/reporting/stoplist-reporting.service';
import { Vehicle } from '../../../core/models/vehicle.model';
import { compareCustom } from '../../../core/compare-fn/custom-compare';
import { roundDecimalsAsString } from '../../../core/math/roundDecimals';


@Component( {
  selector: 'app-dispo',
  templateUrl: './dispo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class DispoComponent extends AbstractTranslateComponent implements OnInit {

  checkAll: boolean;
  tours: Tour[];
  filteredTours: Tour[];
  toursLoading$: Observable<boolean>;

  public msgs$: Observable<Message[]>;
  public sticky$: Observable<boolean>;

  selectedTours: Tour[] = [];
  selectedOptimizableTours: Tour[] = []; // tours with more than one shipment

  notMicrodoof: boolean;

  displayOptimizationOptions = false;
  dontShiftOneDayFromNow = true;
  optimizeTraffic = true;
  optimizeExistingtours = true;
  optimizeSplitTours = false;
  sprinterMaxKg: number;
  caddyMaxKg: number;
  kombiMaxKg: number;
  bikeMaxKg: number;

  latestSortField: string = null;
  latestSortOrder = 0;

  dateFormatMedium: string;

  dateFormat: string;
  tourDateFilter: Date;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService,
               protected msgService: MsgService,
               protected printingService: PrintingService,
               protected reportingService: StoplistReportingService,
               protected browserCheck: BrowserCheck ) {
    super( translate, cd, msgService );
  }


  ngOnInit() {
    super.ngOnInit();

    this.tourDateFilter = new Date();

    this.notMicrodoof = this.browserCheck.browser === 'handsome Browser';
    this.toursLoading$ = this.touroptimizingService.toursLoading$;

    this.tours = [];
    this.filteredTours = [];
    this.touroptimizingService.tours$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( tours: Tour[] ) => {
        this.tours.length = 0;
        if (this.latestSortField !== null) {
          this.tours.push( ...tours.sort( ( data1, data2 ) => {
            return compareCustom( this.latestSortOrder, data1[ this.latestSortField ], data2[ this.latestSortField ] );
          } ) );
        } else {
          const sortedAndGrouped = this.sortAndGroupTours( tours );
          this.tours.push( ...sortedAndGrouped );
        }
        this.filterToursByDeliveryDate();
        this.cd.markForCheck();
      } );
    this.touroptimizingService.getTours();

    this.touroptimizingService.initSSEtouroptimization( this.ngUnsubscribe );
    this.touroptimizingService.initSSEtourChanges( this.ngUnsubscribe );
  }

  private filterToursByDeliveryDate() {
    this.filteredTours.length = 0;
    this.filteredTours.push( ...this.tours.filter( tour => moment( tour.date ).isSame( this.tourDateFilter, 'day' ) ) );
  }

  customSort( event: SortEvent ) {
    if (event.field) {
      this.latestSortField = event.field;
      this.latestSortOrder = event.order;
      event.data.sort( ( data1, data2 ) => {
        return compareCustom( event.order, data1[ event.field ], data2[ event.field ] );
      } );
    }
  }

  getTours() {
    this.touroptimizingService.showSpinner();
    this.touroptimizingService.getTours();
  }

  private sortAndGroupTours( tours: Tour[] ): Tour[] {
    // split in parent and child tours and sort both arrays id descending
    const sortIdDesc = function ( t1: Tour, t2: Tour ) {
      return t1.id > t2.id ? -1 : 1;
    };
    // tours that are optimized within themselves have their own id as parentId
    // reoptimized tours are parent and child
    // const allTourIds = tours.map(tour => tour.id);
    const parentTours = tours
      .filter( tour => !tour.parentId || tour.id === tour.parentId )
      // || allTourIds.indexOf(tour.parentId) >= 0 )
      .sort( sortIdDesc );
    const childTours = tours
      .filter( tour => tour.parentId && tour.id !== tour.parentId )
      .sort( sortIdDesc );
    // join arrays => add cildren after parent
    const sortedTours = [];
    parentTours.forEach( parent => {
      sortedTours.push( parent );
      sortedTours.push( ...childTours.filter( child => child.parentId === parent.id ) );
    } );
    if (parentTours.length === 0 && childTours.length > 0) {
      childTours.forEach( child => sortedTours.push( child ) );
    }
    return sortedTours;
  }

  changeCheckAllTours( evt: { checked: boolean } ) {
    this.touroptimizingService.switchSelectionAllTours( evt.checked );
  }

  protected optimizeTours() {
    this.selectedTours = this.filteredTours
      .filter( tour => tour.selected );
    this.selectedOptimizableTours = this.selectedTours
      .filter( tour => tour.orders.length > 1 );
    if (this.selectedOptimizableTours.length === 0) {
      this.msgService.info( 'no_optimizable_tours_selected', false, false );
    } else {
      const selectedTourIds = this.selectedOptimizableTours
        .map( tour => tour.id );
      let vehicles = [];
      if (this.optimizeSplitTours) {
        vehicles = this.addVehicles( this.sprinterMaxKg, Vehicle.SPRINTER, vehicles );
        vehicles = this.addVehicles( this.caddyMaxKg, Vehicle.CADDY, vehicles );
        vehicles = this.addVehicles( this.kombiMaxKg, Vehicle.STATION_WAGON, vehicles );
        vehicles = this.addVehicles( this.bikeMaxKg, Vehicle.BIKE, vehicles );
      }
      this.touroptimizingService.optimizeAndReinitTours( selectedTourIds,
        vehicles.length > 0 ? vehicles : [ Vehicle.SPRINTER ],
        this.optimizeTraffic, this.optimizeExistingtours, this.dontShiftOneDayFromNow );
    }
    this.checkAll = false;
  }

  private addVehicles( amount: number, type: Vehicle, vehicles: Vehicle[] ): Vehicle[] {
    if (amount > 0) {
      for (let i = 0; i < amount; i += 1) {
        vehicles.push( type );
      }
    }
    return vehicles;
  }

  deleteTour( tourId ) {
    this.touroptimizingService.deleteTours( [ tourId ] );
  }

  preview() {
    this.reporting( false );
  }

  saving() {
    this.reporting( true );
  }

  protected reporting( saving: boolean ) {
    const listsToPrint = this.filteredTours.filter( tour => tour.selected );

    const filename = 'sl_' + listsToPrint.map( tour => tour.id ).join( '_' );
    this.printingService.printReports( this.reportingService
        .generateReports( listsToPrint ),
      filename, saving );
  }

  optimizeDialog() {
    this.displayOptimizationOptions = false;
    this.selectedTours = this.filteredTours
      .filter( tour => tour.selected );
    this.selectedOptimizableTours = this.selectedTours
      .filter( tour => tour.orders.length > 1 );
    if (this.selectedOptimizableTours.length === 0) {
      this.msgService.info( 'no_optimizable_tours_selected', false, false );
    } else {
      this.displayOptimizationOptions = true;
    }
    this.cd.markForCheck();
  }

  acceptOptimizationOptions() {
    this.optimizeTours();
    this.displayOptimizationOptions = false;
    this.cd.markForCheck();
  }

  rejectOptimizationOptions() {
    this.displayOptimizationOptions = false;
    this.cd.markForCheck();
  }

  formatTourDurationTime( duration: number ): string {
    if (duration > 0) {
      return moment.utc( duration * 1000 ).format( 'HH:mm:ss' )
    }
    return '';
  }

  roundDecimalsAsStringWrapper( input: number ): string {
    return roundDecimalsAsString( input, 10, true );
  }

  tourDateFilterPrevDay() {
    this.tourDateFilter = moment( this.tourDateFilter )
      .subtract( 1, 'days' )
      .toDate();
    this.filterToursByDeliveryDate();
  }

  tourDateFilterNextDay() {
    this.tourDateFilter = moment( this.tourDateFilter )
      .add( 1, 'days' )
      .toDate();
    this.filterToursByDeliveryDate();
  }
}



