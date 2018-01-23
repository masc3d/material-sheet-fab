import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { Deliverylist } from '../../core/models/deliverylist.model';
import { TouroptimizingService } from './touroptimizing.service';
import { TourListItem } from '../../core/models/tour-list-item.model';
import { PrintingService } from '../../core/printing/printing.service';
import { StoplistReportingService } from '../../core/reporting/stoplist-reporting.service';
import { HttpParams } from '@angular/common/http';
import { Tour } from '../../core/models/tour.model';

@Component( {
  selector: 'app-touroptimizing',
  templateUrl: './touroptimizing.component.html',
  styleUrls: [ './touroptimizing.css' ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TouroptimizingComponent extends AbstractTranslateComponent implements OnInit {

  // deliverylists: Deliverylist[];
  toursOrderCount: number;
  toursParcelCount: number;
  toursTotalWeight: number;
  // vehicleCount: number;
  tours: Tour[];

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService,
               protected printingService: PrintingService,
               protected reportingService: StoplistReportingService) {
    super( translate, cd );
  }

  ngOnInit() {
    super.ngOnInit();

    this.toursOrderCount = 0;
    this.toursParcelCount = 0;
    this.toursTotalWeight = 0;
    // this.deliverylists = [];

    this.tours = [];
    this.touroptimizingService.tours$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( tours: Tour[] ) => {
        this.tours = tours;
        this.toursOrderCount = this.countOrders( tours );
        this.toursParcelCount = this.countParcels( tours );
        this.toursTotalWeight = this.sumWeights( tours );
        this.cd.markForCheck();
      } );
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    const activeStation = JSON.parse( localStorage.getItem( 'activeStation' ) );
    this.touroptimizingService.getTours(currUser.user.debitorId, activeStation.stationNo );
  }

  private sumWeights( tours: Tour[] ) {
    return tours.length > 0
      ? tours.map( ( d ) => d.totalWeight )
        .reduce( ( a, b ) => a + b )
      : 0;
  }

  private countParcels( tours: Tour[] ) {
    return tours.length > 0
      ? tours.map( ( d ) => d.totalPackages )
        .reduce( ( a, b ) => a + b )
      : 0;
  }

  private countOrders( tours: Tour[] ) {
    return tours.length > 0
      ? tours.map( ( d ) => d.totalShipments )
        .reduce( ( a, b ) => a + b )
      : 0;
  }

  changeCheckAllTours( evt: { checked: boolean } ) {
      this.touroptimizingService.switchSelectionAllTours(evt.checked);
  }

  optimizeTours() {
    console.log( 'optimizeTours...' );
    // console.log( 'vehicleCount:', this.vehicleCount );
    console.log( 'selectedIds:', this.tours
      .filter( d => d.selected)
      .map(d => d.id) );
  }

  printStopLists() {
    console.log( 'printDeliveryLists...' );
  //   const listsToPrint = this.deliverylists.filter( d => d.selected);
  //   console.log( 'selected:', listsToPrint);
  //
  //   const filename = 'sl_' + listsToPrint.map( d => d.id ).join( '_' );
  //  this.printingService.printReports( this.reportingService
  //                 .generateReports( listsToPrint ),
  //               filename, false );
  }

}
