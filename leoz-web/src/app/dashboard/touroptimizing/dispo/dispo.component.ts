import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { takeUntil } from 'rxjs/operators';

import { Message } from 'primeng/components/common/api';
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
import { SortEvent } from 'primeng/api';
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
  toursLoading$: Observable<boolean>;

  public msgs$: Observable<Message[]>;
  public sticky$: Observable<boolean>;

  selectedOptimizableTourIds: Tour[] = []; // tours with more than one shipment

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
  aktualTourDate = null;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService,
               protected msgService: MsgService,
               protected printingService: PrintingService,
               protected reportingService: StoplistReportingService,
               protected browserCheck: BrowserCheck ) {
    super( translate, cd, msgService, () => {
      this.aktualTourDate = this.initAktualTourDate();
    } );
  }


  ngOnInit() {
    super.ngOnInit();

    this.aktualTourDate = this.initAktualTourDate();

    this.notMicrodoof = this.browserCheck.browser === 'handsome Browser';
    this.toursLoading$ = this.touroptimizingService.toursLoading$;

    this.tours = [];
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
          this.tours.push( ...this.sortAndGroupTours( tours ) );
        }
        this.cd.markForCheck();
      } );
    this.touroptimizingService.getTours();

    this.touroptimizingService.initSSEtouroptimization( this.ngUnsubscribe );
    this.touroptimizingService.initSSEtourWhatever( this.ngUnsubscribe );
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
    const parentTours = tours
      .filter( tour => !tour.parentId || tour.id === tour.parentId )
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
    this.selectedOptimizableTourIds = this.tours
      .filter( tour => tour.selected && tour.orders.length > 1 );
    if (this.selectedOptimizableTourIds.length === 0) {
      this.msgService.info( 'no_optimizable_tours_selected', false, false );
    } else {
      const selectedTourIds = this.tours
        .filter( tour => tour.selected )
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
    const listsToPrint = this.tours.filter( tour => tour.selected );

    const filename = 'sl_' + listsToPrint.map( tour => tour.id ).join( '_' );
    this.printingService.printReports( this.reportingService
        .generateReports( listsToPrint ),
      filename, saving );
  }

  optimizeDialog() {
    this.selectedOptimizableTourIds = this.tours
      .filter( tour => tour.selected && tour.orders.length > 1 );
    if (this.selectedOptimizableTourIds.length === 0) {
      this.msgService.info( 'no_optimizable_tours_selected', false, false );
    } else {
      this.displayOptimizationOptions = true;
    }
  }

  acceptOptimizationOptions() {
    this.optimizeTours();
    this.displayOptimizationOptions = false;
  }

  rejectOptimizationOptions() {
    this.displayOptimizationOptions = false;
  }

  formatTourCreationDate( created: string ): string {
    return moment( created ).format( 'DD.MM. HH:mm' );
  }

  formatTourDurationTime( duration: number ): string {
    if (duration > 0) {
      return moment.utc( duration * 1000 ).format( 'HH:mm:ss' )
    }
    return '';
  }

  roundDecimalsAsStringWrapper( input: number ) {
    return roundDecimalsAsString( input, 10, true );
  }

  private initAktualTourDate() {
    const d = new Date();
    return d;
  }

  private switchDay(timeline: number) {
    const d = new Date(this.aktualTourDate);
    d.setDate( d.getDate() + (timeline) );
    this.aktualTourDate = d;
  }
}



