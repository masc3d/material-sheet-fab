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

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService,
               protected msgService: MsgService,
               protected printingService: PrintingService,
               protected reportingService: StoplistReportingService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    this.toursOrderCount = 0;
    this.toursParcelCount = 0;
    this.toursTotalWeight = 0;
    this.toursQuota = 0;
    // this.deliverylists = [];

    this.tours = [];
    this.touroptimizingService.tours$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( tours: Tour[] ) => {
        this.tours = this.processTourchanges( this.tours, tours );
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

  private processTourchanges( prevTours: Tour[], newTours: Tour[] ) {
    // new tours
    // deleted tours
    // changed tours
    return newTours;
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

  printStopLists() {
    const listsToPrint = this.tours.filter( tour => tour.selected );

    const filename = 'sl_' + listsToPrint.map( tour => tour.id ).join( '_' );
    this.printingService.printReports( this.reportingService
        .generateReports( listsToPrint ),
      filename, false );
  }
}
