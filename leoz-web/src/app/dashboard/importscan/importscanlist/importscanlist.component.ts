import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { SelectItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { Package } from '../../../core/models/package.model';

@Component( {
  selector: 'app-importscanlist',
  templateUrl: './importscanlist.component.html'
} )
export class ImportscanlistComponent extends AbstractTranslateComponent implements OnInit {

  importscanlistForm: FormGroup;
  scanOptions: SelectItem[];

  packages: Package[];

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected router: Router ) {
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
    this.packages = [ {
      parcelNo: 123,
      orderId: 456,
      parcelPosition: 1,
      loadinglistNo: 789,
      typeOfPackaging: 1,
      realWeight: 2.2,
      dateOfStationOut: new Date(),
      creference: 'dsdf',

      zip: '03189',
      city: 'Orihuela Costa',
      devliveryStation: 321
    } ];
  }

  private createScanOptions(): SelectItem[] {
    const scanOptions = [];
    scanOptions.push( { label: this.translate.instant( 'standard' ), value: 1 } );
    scanOptions.push( { label: this.translate.instant( 'zip' ), value: 0 } );
    return scanOptions;
  }
}
