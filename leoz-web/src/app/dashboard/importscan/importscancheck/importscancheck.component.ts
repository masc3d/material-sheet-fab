import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { Shipment } from '../../../core/models/shipment.model';
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-importscancheck',
  templateUrl: './importscancheck.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class ImportscancheckComponent extends AbstractTranslateComponent implements OnInit {

  importscancheckForm: FormGroup;

  shipments: Shipment[];

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               public router: Router ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();
    this.importscancheckForm = this.fb.group( {
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
        senderStation: 348,
        deliveryStation: 50,
        parcels: [ {
          parcelNo: 84259511468
        } ]
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
        senderStation: 348,
        deliveryStation: 50,
        parcels: [ {
          parcelNo: 84259511468
        } ]
      } ];
  }
}
