import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { SelectItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { Shipment } from '../../../core/models/shipment.model';

@Component( {
  selector: 'app-importscanlist',
  templateUrl: './importscanlist.component.html'
} )
export class ImportscanlistComponent extends AbstractTranslateComponent implements OnInit {

  importscanlistForm: FormGroup;
  scanOptions: SelectItem[];

  shipments: Shipment[];

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
    this.importscanlistForm = this.fb.group( {
      scanfield: [ null ],
      msgfield: [ null ],
      printlabel: [ null ]
    } );
    this.shipments = [
      {
        senderAddress: {
          line1: 'General Logistics Sys'
        },
        deliveryAddress: {
          line1: 'alte Freiheit Werbung',
          zipCode: '50825',
          city: 'Köln'
        },
        shipmentDate: new Date(),
        senderStation: 348,
        deliveryStation: 50,
        deliveryDate: '24.10.2017',
        deliveryTime: '12:00',
        parcels: [
          {
            parcelNo: 84259511468,
            typeOfPackaging: 94,
            realWeight: 39.40,
            volWeight: 1,
            length: 2,
            width: 3,
            height: 4
          }
        ]
      },
      {
        senderAddress: {
          line1: 'General Logistics Sys'
        },
        deliveryAddress: {
          line1: 'alte Freiheit Werbung',
          zipCode: '50825',
          city: 'Köln'
        },
        shipmentDate: new Date(),
        senderStation: 348,
        deliveryStation: 50,
        deliveryDate: '24.10.2017',
        deliveryTime: '12:00',
        parcels: [
          {
            parcelNo: 84259511468,
            typeOfPackaging: 94,
            realWeight: 39.40,
            volWeight: 0,
            length: 0,
            width: 0,
            height: 0
          }
        ]
      }
    ];
  }

  private createScanOptions(): SelectItem[] {
    const scanOptions = [];
    scanOptions.push( { label: this.translate.instant( 'standard' ), value: 1 } );
    scanOptions.push( { label: this.translate.instant( 'zip' ), value: 0 } );
    return scanOptions;
  }
}
