import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { Importscancheck } from '../../../core/models/importscancheck.model';

@Component( {
  selector: 'app-importscancheck',
  templateUrl: './importscancheck.component.html'
} )
export class ImportscancheckComponent extends AbstractTranslateComponent implements OnInit {

  importscancheckForm: FormGroup;

  shipments: Importscancheck[];

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               public router: Router ) {
    super( translate, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.importscancheckForm = this.fb.group( {
      scanfield: [ null ],
      msgfield: [ null ],
      printlabel: [ null ]
    } );
    this.shipments = [ {
      senderline1: 'General Logistics Sys',
      deliveryline1: 'alte Freiheit Werbung',
      senderstation: 348,
      deliverystation: 50,
      deliveryzip: '50825',
      deliverycity: 'Köln',
      parcelno: '84259511468(0)'
    },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)'
      },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)'
      },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)'
      },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)'
      },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)'
      }];
  }
}
