import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { Shipment } from '../../core/models/shipment.model';
import { Shipmentsums } from '../../core/models/shipmentsums.model';

@Component( {
  selector: 'app-deliverydispo',
  templateUrl: './deliverydispo.component.html',
  styles: [`
  .ui-tabview-panel {
    height: 100% !important;
  }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class DeliverydispoComponent extends AbstractTranslateComponent implements OnInit {

  shipments: Shipment[];
  shipmentSums: Shipmentsums[];

  dateFormatPrimeng: string;
  deliveryDate = null;
  locale: any;

  tourNo: string;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               public router: Router ) {
    super( translate, cd, () => {
        this.deliveryDate = this.deliveryDate ? new Date( this.deliveryDate ) : new Date();
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.deliveryDate = new Date();
    this.tourNo = '';
    this.shipmentSums = [
      {
        tour: 1,
        deadline: 'ND',
        deadlinecount: 26,
        weightsum: 350,
        packcount: 92,
        shipmentcount: 77,
      }
    ];
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
            parcelNo: 84259511469,
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
}
