import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { TouroptimizingService } from './touroptimizing.service';
import { PrintingService } from '../../core/printing/printing.service';
import { StoplistReportingService } from '../../core/reporting/stoplist-reporting.service';
import { Tour } from '../../core/models/tour.model';
import { MsgService } from '../../shared/msg/msg.service';
import { Observable } from 'rxjs/Observable';
import { Message } from 'primeng/primeng';
import { roundDecimals } from '../../core/math/roundDecimals';
import { BrowserCheck } from '../../core/auth/browser-check';

@Component( {
  selector: 'app-touroptimizing',
  templateUrl: './touroptimizing.component.html',
  styleUrls: [ './touroptimizing.css' ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class TouroptimizingComponent extends AbstractTranslateComponent implements OnInit {

  checkAll: boolean;
  // deliverylists: Deliverylist[];
  toursOrderCount: number;
  toursParcelCount: number;
  toursTotalWeight: number;
  toursQuota: number;
  // vehicleCount: number;
  tours: Tour[];

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
    // this.deliverylists = [];

    this.tours = [];
    this.touroptimizingService.tours$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( tours: Tour[] ) => {
        this.tours = tours;
        this.tours.sort( ( a, b ) => a.id > b.id ? -1 : 1 );
        this.toursOrderCount = this.countOrders( tours );
        this.toursParcelCount = this.countParcels( tours );
        this.toursTotalWeight = roundDecimals( this.sumWeights( tours ), 100 );
        this.toursQuota = this.getToursQuota( tours );
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

    // ALEX: better solution would be SSE
    // this.touroptimizingService.repeatCheckLatestModDate();
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
      .filter( tour => tour.selected )
      .map( tour => tour.id );
    this.touroptimizingService.optimizeAndReinitTours( selectedTourIds );
    this.checkAll = false;
  }

  getTours() {
    this.touroptimizingService.getTours();
  }

  resetTours() {
    this.msgService.clear();
    const tourIds = this.tours.map( tour => tour.id );
    this.touroptimizingService.deleteAndReinitTours( tourIds );
    this.checkAll = false;
  }

  deleteTour( tourId ) {
    this.touroptimizingService.deleteAndReinitTours( [ tourId ] );
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
