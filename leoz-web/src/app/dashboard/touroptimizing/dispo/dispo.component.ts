import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Message } from 'primeng/components/common/api';

import * as moment from 'moment';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';
import { TouroptimizingService } from '../touroptimizing.service';
import { Tour } from '../../../core/models/tour.model';
import { roundDecimals } from '../../../core/math/roundDecimals';
import { BrowserCheck } from '../../../core/auth/browser-check';
import { PrintingService } from '../../../core/printing/printing.service';
import { StoplistReportingService } from '../../../core/reporting/stoplist-reporting.service';
import { Vehicle } from '../../../core/models/vehicle.model';


@Component( {
  selector: 'app-dispo',
  templateUrl: './dispo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class DispoComponent extends AbstractTranslateComponent implements OnInit {

  @Input()
  withInitialGeneration: boolean;

  checkAll: boolean;
  tours: Tour[];

  toursLoading$: Observable<boolean>;
  public msgs$: Observable<Message[]>;
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

    this.notMicrodoof = this.browserCheck.browser === 'handsome Browser';
    this.toursLoading$ = this.touroptimizingService.toursLoading$;

    this.tours = [];
    this.touroptimizingService.tours$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( tours: Tour[] ) => {
        this.tours = this.sortAndGroupTours( tours );
        this.cd.markForCheck();
      } );
    this.touroptimizingService.latestDeliverylistModification$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( someTimestamp: number ) => {
        if (this.tours && this.tours.length > 0
          && new Date( this.tours[ 0 ].created ).getTime() < someTimestamp) {
          this.msgService.info( 'tours-most-likely-outdated' );
        }
      } );
    this.touroptimizingService.getTours( this.withInitialGeneration );

    this.touroptimizingService.initSSEtouroptimization( this.ngUnsubscribe, this.withInitialGeneration );
    this.touroptimizingService.initSSEtourWhatever( this.ngUnsubscribe, this.withInitialGeneration );
  }

  getTours() {
    this.touroptimizingService.getTours( this.withInitialGeneration );
  }

  resetTours() {
    this.msgService.clear();
    const tourIds = this.tours.map( tour => tour.id );
    this.touroptimizingService.deleteTours( tourIds );
    this.checkAll = false;
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
      childTours.forEach( child => sortedTours.push( child ));
    }
    return sortedTours;
  }

  changeCheckAllTours( evt: { checked: boolean } ) {
    this.touroptimizingService.switchSelectionAllTours( evt.checked );
  }

  protected optimizeTours() {
    /**
     * ALEX: Values should come from the service
     */
    // this.optimizeTodayAndFuture
    // this.optimizeTraffic => traffic: true
    // this.optimizeExistingtours = => vehicles[{capacity: 0}]
    // this.optimizeSplitTours
    // this.sprinterMaxKg => vehicles[{capacity: 1200}]
    // this.caddyMaxKg => vehicles[{capacity: 500}]
    // this.kombiMaxKg => vehicles[{capacity: 350}]
    // this.bikeMaxKg => vehicles[{capacity: 30}]
    this.selectedOptimizableTourIds = this.tours
      .filter( tour => tour.selected && tour.orders.length > 1 );
    if (this.selectedOptimizableTourIds.length === 0) {
      this.msgService.info( 'no_optimizable_tours_selected' );
    } else {
      const selectedTourIds = this.tours
        .filter( tour => tour.selected )
        .map( tour => tour.id );
      // const selectedNotOptimizedTourIds = this.tours
      //   .filter( tour => tour.selected && !tour.optimized )
      //   .map( tour => tour.id );
      // if (selectedNotOptimizedTourIds.length > 0) {
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
      // } else {
      //   this.msgService.info( 'optimizing_optimized_tours_not_possible' );
      // }
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
      this.msgService.info( 'no_optimizable_tours_selected' );
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

  formatTourCreationDate( created: string ) {
    return moment( created ).format( 'DD.MM.YY' );
  }

  formatTourDurationTime( duration: number){
    if ( duration > 0 ) {
      return moment.utc( duration * 1000 ).format( 'HH:mm:ss' )
    }
  }

  roundTourWeight( weight: number){
    if ( weight > 0 ) {
      return roundDecimals(  weight , 10 );
    }
  }

  roundTourDistance( distance: number){
    if ( distance > 0 ) {
      return roundDecimals(  distance , 10 );
    }
  }
}



