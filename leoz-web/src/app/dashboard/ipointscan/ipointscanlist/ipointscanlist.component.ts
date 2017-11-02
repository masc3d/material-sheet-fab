import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { SelectItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { Package } from '../../../core/models/package.model';
import { sumAndRound } from '../../../core/math/sumAndRound';

@Component( {
  selector: 'app-ipointscanlist',
  templateUrl: './ipointscanlist.component.html'
} )
export class IpointscanlistComponent extends AbstractTranslateComponent implements OnInit {

  ipointscanlistForm: FormGroup;
  scanOptions: SelectItem[];

  parcels: Package[];

  realWeightTotal: number;
  volWeightTotal: number;

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               public router: Router ) {
    super( translate, () => {
      this.scanOptions = this.createScanOptions();
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.scanOptions = this.createScanOptions();
    this.ipointscanlistForm = this.fb.group( {
      scanfield: [ null ],
      msgfield: [ null ],
      printlabel: [ null ],
      novalidation: [ null ],
      basedon: [ null ]
    } );
    this.parcels = [];
    this.realWeightTotal = 0;
    this.volWeightTotal = 0;
    this.callInitialShipmentService();
  }

  private createScanOptions(): SelectItem[] {
    const scanOptions = [];
    scanOptions.push( { label: this.translate.instant( '84259511468(0)' ), value: 1 } );
    scanOptions.push( { label: this.translate.instant( '84259511469(7)' ), value: 2 } );
    scanOptions.push( { label: this.translate.instant( '84259511470(4)' ), value: 3 } );
    return scanOptions;
  }

  private callInitialShipmentService() {
    this.parcels = [ {
      parcelNo: 84259511468,
      deliverydate: '24.10.2017',
      deliverytime: '12:00',
      typeOfPackaging: 94,
      realWeight: 39.40,
      volWeight: 0,
      length: 0,
      width: 0,
      height: 0
    },
      {
        parcelNo: 84259511469,
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        typeOfPackaging: 94,
        realWeight: 39.40,
        volWeight: 0,
        length: 0,
        width: 0,
        height: 0
      },
      {
        parcelNo: 84259511470,
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        typeOfPackaging: 94,
        realWeight: 39.40,
        volWeight: 0,
        length: 0,
        width: 0,
        height: 0
      }
    ];
    this.calcTotals();
  }

  private calcTotals() {
    this.realWeightTotal = sumAndRound(this.parcels
      .map( ( parcel: Package ) => parcel.realWeight ));
    this.volWeightTotal = sumAndRound(this.parcels
      .map( ( parcel: Package ) => parcel.volWeight ));
  }
}
