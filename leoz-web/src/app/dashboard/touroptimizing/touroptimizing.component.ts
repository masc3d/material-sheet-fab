import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Tour } from '../../core/models/touroptimizing.model';

import { Shipment } from '../../core/models/shipment.model';
import { Deliverylist } from '../../core/models/deliverylist.model';
import { TouroptimizingService } from './touroptimizing.service';

@Component( {
  selector: 'app-touroptimizing',
  templateUrl: './touroptimizing.component.html',
  styleUrls: [ './touroptimizing.css' ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TouroptimizingComponent extends AbstractTranslateComponent implements OnInit {

  touroptimizingForm: FormGroup;
  deliverylists: Deliverylist[];
  deliverylistsOrderCount: number;
  deliverylistsParcelCount: number;
  deliverylistsTotalWeight: number;
  tours: Tour[];

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected touroptimizingService: TouroptimizingService ) {
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
    this.touroptimizingForm = this.fb.group( {} );
    this.tours = [
      {
        deliverylistNumber: 123456789,
        shipments: 33,
        packages: 49,
        weight: 234,
        time: '3:15',
        distance: '96',
        optimized: true,
        tourNo: 568,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: false
      },
      {
        deliverylistNumber: 678912345,
        shipments: 45,
        packages: 98,
        weight: 436,
        time: '5:35',
        distance: '126',
        optimized: false,
        tourNo: 547,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75,
        time: '1:22',
        distance: '55',
        optimized: false,
        tourNo: 125,
        tourWeight: 423,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108',
        tourOptimized: true
      },
      {
        deliverylistNumber: 123894567,
        shipments: 5,
        packages: 18,
        weight: 75.10,
        time: '1:22',
        distance: '55.65',
        optimized: false,
        tourNo: 125,
        tourWeight: 423.80,
        tourShipments: 38,
        tourPackages: 47,
        tourTime: '3:45',
        tourDistance: '108.40',
        tourOptimized: true
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
}
