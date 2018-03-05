import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Message } from 'primeng/components/common/api';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';
import { TouroptimizingService } from '../touroptimizing.service';
import { Tour } from '../../../core/models/tour.model';
import { roundDecimals } from '../../../core/math/roundDecimals';
import { BrowserCheck } from '../../../core/auth/browser-check';
import { PrintingService } from '../../../core/printing/printing.service';
import { StoplistReportingService } from '../../../core/reporting/stoplist-reporting.service';


@Component( {
  selector: 'app-dispo',
  template: 'to override',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class DispoComponent extends AbstractTranslateComponent implements OnInit {

  withInitialGeneration = true;

  checkAll: boolean;
  toursOrderCount: number;
  toursParcelCount: number;
  toursTotalWeight: number;
  toursQuota: number;
  tours: Tour[];

  toursLoading$: Observable<boolean>;
  public msgs$: Observable<Message[]>;
  selectedOptimizableTourIds: Tour[] = []; // tours with more than one shipment

  notMicrodoof: boolean;

  displayOptimizationOptions = false;
  optimizeTodayAndFuture = true;
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
    this.toursOrderCount = 0;
    this.toursParcelCount = 0;
    this.toursTotalWeight = 0;
    this.toursQuota = 0;
    this.toursLoading$ = this.touroptimizingService.toursLoading$;

    this.tours = [];
    this.touroptimizingService.tours$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( tours: Tour[] ) => {
        this.tours = this.sortAndGroupTours( tours );
        this.toursOrderCount = this.countOrders( this.tours );
        this.toursParcelCount = this.countParcels( this.tours );
        this.toursTotalWeight = roundDecimals( this.sumWeights( this.tours ), 100 );
        this.toursQuota = this.getToursQuota( this.tours );
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
    this.touroptimizingService.getTours(this.withInitialGeneration);

    this.touroptimizingService.initSSEtouroptimization( this.ngUnsubscribe, this.withInitialGeneration );
    this.touroptimizingService.initSSEtourWhatever( this.ngUnsubscribe, this.withInitialGeneration );
  }

  getTours() {
    this.touroptimizingService.getTours(this.withInitialGeneration);
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
    const parentTours = tours
      .filter( tour => !tour.parentId )
      .sort( sortIdDesc );
    const childTours = tours
      .filter( tour => tour.parentId )
      .sort( sortIdDesc );
    // join arrays => add cildren after parent
    const sortedTours = [];
    parentTours.forEach( parent => {
      sortedTours.push( parent );
      sortedTours.push( ...childTours.filter( child => child.parentId === parent.id ) );
    } );
    return sortedTours;
  }

  private getToursQuota( tours: Tour[] ) {
    return tours.length > 0
      ? roundDecimals( (tours.filter( tour => tour.optimized ).length) / (tours.length) * 100, 1 )
      : 0;
  }

  private sumWeights( tours: Tour[] ) {
    return tours.length > 0
      ? tours.map( ( tour: Tour ) => tour.totalWeight )
        .reduce( ( a: number, b: number ) => a + b )
      : 0;
  }

  private countParcels( tours: Tour[] ) {
    return tours.length > 0
      ? tours.map( ( tour: Tour ) => tour.totalPackages )
        .reduce( ( a: number, b: number ) => a + b )
      : 0;
  }

  private countOrders( tours: Tour[] ) {
    return tours.length > 0
      ? tours.map( ( tour: Tour ) => tour.totalShipments )
        .reduce( ( a: number, b: number ) => a + b )
      : 0;
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
      const selectedNotOptimizedTourIds = this.tours
        .filter( tour => tour.selected && !tour.optimized )
        .map( tour => tour.id );
      if (selectedNotOptimizedTourIds.length > 0) {
        const vehicles = [];
        if (this.optimizeSplitTours) {
          if (this.sprinterMaxKg > 0) {
            vehicles.push( { capacity: 1200 } );
          }
          if (this.caddyMaxKg > 0) {
            vehicles.push( { capacity: 500 } );
          }
          if (this.kombiMaxKg > 0) {
            vehicles.push( { capacity: 350 } );
          }
          if (this.bikeMaxKg > 0) {
            vehicles.push( { capacity: 30 } );
          }
        }
        this.touroptimizingService.optimizeAndReinitTours( selectedNotOptimizedTourIds,
          vehicles.length > 0 ? vehicles : [{}],
          this.optimizeTraffic );
      } else {
        this.msgService.info( 'optimizing_optimized_tours_not_possible' );
      }
    }
    this.checkAll = false;
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
}



