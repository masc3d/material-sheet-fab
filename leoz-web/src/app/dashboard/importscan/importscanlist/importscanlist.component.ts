import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { SelectItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { Importscan } from 'app/core/models/importscan.model';

@Component( {
  selector: 'app-importscanlist',
  templateUrl: './importscanlist.component.html'
} )
export class ImportscanlistComponent extends AbstractTranslateComponent implements OnInit {

  importscanlistForm: FormGroup;
  scanOptions: SelectItem[];

  shipments: Importscan[];

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
    this.shipments = [ {
      senderline1: 'General Logistics Sys',
      deliveryline1: 'alte Freiheit Werbung',
      senddate: '23.10.2017',
      senderstation: 348,
      deliverystation: 50,
      deliveryzip: '50825',
      deliverycity: 'Köln',
      parcelno: '84259511468(0)',
      deliverydate: '24.10.2017',
      deliverytime: '12:00',
      typeOfPackaging: 94,
      realWeight: 39.40,
      noOfPacks: 1,
      volWeight: 0,
      length: 0,
      width: 0,
      height: 0
    },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senddate: '23.10.2017',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        typeOfPackaging: 94,
        realWeight: 39.40,
        noOfPacks: 1,
        volWeight: 0,
        length: 0,
        width: 0,
        height: 0
      },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senddate: '23.10.2017',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        typeOfPackaging: 94,
        realWeight: 39.40,
        noOfPacks: 1,
        volWeight: 0,
        length: 0,
        width: 0,
        height: 0
      },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senddate: '23.10.2017',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        typeOfPackaging: 94,
        realWeight: 39.40,
        noOfPacks: 1,
        volWeight: 0,
        length: 0,
        width: 0,
        height: 0
      },
      {
        senderline1: 'General Logistics Sys',
        deliveryline1: 'alte Freiheit Werbung',
        senddate: '23.10.2017',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        typeOfPackaging: 94,
        realWeight: 39.40,
        noOfPacks: 1,
        volWeight: 0,
        length: 0,
        width: 0,
        height: 0
      },
      {
        senderline1: 'General Logistics Systems',
        deliveryline1: 'alte Freiheit Werbung',
        senddate: '23.10.2017',
        senderstation: 348,
        deliverystation: 50,
        deliveryzip: '50825',
        deliverycity: 'Köln',
        parcelno: '84259511468(0)',
        deliverydate: '24.10.2017',
        deliverytime: '12:00',
        typeOfPackaging: 94,
        realWeight: 39.40,
        noOfPacks: 1,
        volWeight: 0,
        length: 0,
        width: 0,
        height: 0
      }];
  }

  private createScanOptions(): SelectItem[] {
    const scanOptions = [];
    scanOptions.push( { label: this.translate.instant( 'standard' ), value: 1 } );
    scanOptions.push( { label: this.translate.instant( 'zip' ), value: 0 } );
    return scanOptions;
  }
}
