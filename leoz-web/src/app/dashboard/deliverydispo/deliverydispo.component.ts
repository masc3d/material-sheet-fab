import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { Shipment } from '../../core/models/shipment.model';
import { Shipmentsums } from '../../core/models/shipmentsums.model';

@Component( {
  selector: 'app-deliverydispo',
  templateUrl: './deliverydispo.component.html'
} )
export class DeliverydispoComponent extends AbstractTranslateComponent implements OnInit {

  deliverydispoForm: FormGroup;

  shipments: Shipment[];
  shipmentSums: Shipmentsums[];

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               public router: Router ) {
    super( translate, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.deliverydispoForm = this.fb.group( {
      scanfield: [ null ],
      msgfield: [ null ],
      printlabel: [ null ]
    } );
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
