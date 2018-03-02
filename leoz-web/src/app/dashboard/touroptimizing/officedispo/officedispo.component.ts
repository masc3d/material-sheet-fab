import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';
import { TouroptimizingService } from '../touroptimizing.service';
import { Observable } from 'rxjs/Observable';
import { Message } from 'primeng/components/common/api';
import { Tour } from '../../../core/models/tour.model';
import { roundDecimals } from '../../../core/math/roundDecimals';
import { BrowserCheck } from '../../../core/auth/browser-check';
import { PrintingService } from '../../../core/printing/printing.service';
import { StoplistReportingService } from '../../../core/reporting/stoplist-reporting.service';


@Component( {
  selector: 'app-officedispo',
  templateUrl: './officedispo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OfficedispoComponent extends AbstractTranslateComponent implements OnInit {

  checkAll: boolean;
  toursOrderCount: number;
  toursParcelCount: number;
  toursTotalWeight: number;
  toursQuota: number;
  tours: Tour[];

  toursLoading$: Observable<boolean>;
  msgs$: Observable<Message[]>;

  notMicrodoof: boolean;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService,
               protected msgService: MsgService,
               protected printingService: PrintingService,
               protected reportingService: StoplistReportingService,
               private browserCheck: BrowserCheck ) {
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
    this.touroptimizingService.getTours();

    this.touroptimizingService.initSSEtouroptimization( this.ngUnsubscribe );
    this.touroptimizingService.initSSEtourWhatever( this.ngUnsubscribe );
  }

  getTours() {
    this.touroptimizingService.getTours();
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

  optimizeTours() {
    const selectedTourIds = this.tours
      .filter( tour => tour.selected && !tour.optimized )
      .map( tour => tour.id );
    if (selectedTourIds.length > 0) {
      this.touroptimizingService.optimizeAndReinitTours( selectedTourIds );
    } else {
      this.msgService.info( 'optimizing_optimized_tours_not_possible' );
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

  reporting( saving: boolean ) {
    const listsToPrint = this.tours.filter( tour => tour.selected );

    const filename = 'sl_' + listsToPrint.map( tour => tour.id ).join( '_' );
    this.printingService.printReports( this.reportingService
        .generateReports( listsToPrint ),
      filename, saving );
  }
}



