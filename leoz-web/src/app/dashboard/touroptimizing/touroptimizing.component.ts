import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Tour } from '../../core/models/touroptimizing.model';

import { Shipment } from '../../core/models/shipment.model';

@Component( {
  selector: 'app-touroptimizing',
  templateUrl: './touroptimizing.component.html',
  styleUrls: ['./touroptimizing.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TouroptimizingComponent extends AbstractTranslateComponent implements OnInit {

  touroptimizingForm: FormGroup;
  tour: Tour[];
  shipments: Shipment[];

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               public router: Router ) {
    super( translate, cd, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();

    this.touroptimizingForm = this.fb.group( {
      carCount: [ null ],
      selectloadlist: [ null ],
      scanfield: [ null ],
      loadlistnumber: [ { value: '', disabled: true } ],
      printlabel: [ null ],
      basedon: [ 'standard' ],
      basedonscan: [ '' ]
    } );
    this.tour = [
      { deliverylistNumber: 123456789,
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
      { deliverylistNumber: 678912345,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
      { deliverylistNumber: 123894567,
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
    this.shipments = [
      {
        deliveryAddress: {
          zipCode: '50825',
        },
        deliveryTime: '12:00',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 12.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50123',
        },
        deliveryTime: '08:00',
        optimized: true,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 1.90,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
      {
        deliveryAddress: {
          zipCode: '50444',
        },
        deliveryTime: '09:30',
        optimized: false,
        parcels: [ {
          parcelNo: 84259511468,
          realWeight: 8.50,
        } ]
      },
    ];
  }
}
