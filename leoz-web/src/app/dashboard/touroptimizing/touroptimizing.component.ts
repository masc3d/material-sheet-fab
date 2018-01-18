import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { Deliverylist } from '../../core/models/deliverylist.model';
import { TouroptimizingService } from './touroptimizing.service';
import { TourListItem } from '../../core/models/tour-list-item.model';
import { PrintingService } from '../../core/printing/printing.service';
import { StoplistReportingService } from '../../core/reporting/stoplist-reporting.service';

@Component( {
  selector: 'app-touroptimizing',
  templateUrl: './touroptimizing.component.html',
  styleUrls: [ './touroptimizing.css' ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TouroptimizingComponent extends AbstractTranslateComponent implements OnInit {

  deliverylists: Deliverylist[];
  deliverylistsOrderCount: number;
  deliverylistsParcelCount: number;
  deliverylistsTotalWeight: number;
  vehicleCount: number;
  tours: TourListItem[];

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService,
               protected printingService: PrintingService,
               protected reportingService: StoplistReportingService) {
    super( translate, cd );
  }

  ngOnInit() {
    super.ngOnInit();

    this.deliverylistsOrderCount = 0;
    this.deliverylistsParcelCount = 0;
    this.deliverylistsTotalWeight = 0;
    this.deliverylists = [];
    this.touroptimizingService.deliverylists$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( deliverylists: Deliverylist[] ) => {
        this.deliverylists = deliverylists;
        this.deliverylistsOrderCount = this.countOrders( deliverylists );
        this.deliverylistsParcelCount = this.countParcels( deliverylists );
        this.deliverylistsTotalWeight = this.sumWeights( deliverylists );
        this.cd.markForCheck();
      } );
    this.tours = [
      <TourListItem> {
        id: 123456789,
        totalShipments: 33,
        totalPackages: 49,
        totalWeight: 234,
        optimized: true,
        time: '13:12',
        distance: 99
      }
    ];
    this.touroptimizingService.getDeliverylists();
  }

  private sumWeights( deliverylists: Deliverylist[] ) {
    return deliverylists.length > 0
      ? deliverylists.map( ( d ) => d.totalWeight )
        .reduce( ( a, b ) => a + b )
      : 0;
  }

  private countParcels( deliverylists: Deliverylist[] ) {
    return deliverylists.length > 0
      ? deliverylists.map( ( d ) => d.totalPackages )
        .reduce( ( a, b ) => a + b )
      : 0;
  }

  private countOrders( deliverylists: Deliverylist[] ) {
    return deliverylists.length > 0
      ? deliverylists.map( ( d ) => d.totalShipments )
        .reduce( ( a, b ) => a + b )
      : 0;
  }

  changeCheckAllDeliverylists( evt: { checked: boolean } ) {
      this.touroptimizingService.switchSelectionAllDeliverylists(evt.checked);
  }

  optimizeDeliverylists() {
    console.log( 'optimizeDeliverylists...' );
    console.log( 'vehicleCount:', this.vehicleCount );
    console.log( 'selectedIds:', this.deliverylists
      .filter( d => d.selected)
      .map(d => d.id) );
  }

  printDeliveryLists() {
    console.log( 'printDeliveryLists...' );
    const listsToPrint = this.deliverylists.filter( d => d.selected);
    console.log( 'selected:', listsToPrint);

    const filename = 'sl_' + listsToPrint.map( d => d.id ).join( '_' );
   this.printingService.printReports( this.reportingService
                  .generateReports( listsToPrint ),
                filename, false );
  }

}
