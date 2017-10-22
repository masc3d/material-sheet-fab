import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { SelectItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

@Component( {
  selector: 'app-importscanlist',
  templateUrl: './importscanlist.component.html'
} )
export class ImportscanlistComponent extends AbstractTranslateComponent implements OnInit {

  importscanlistForm: FormGroup;
  scanOptions: SelectItem[];

  constructor( private fb: FormBuilder,
               protected translate: TranslateService ) {
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
  }

  private createScanOptions(): SelectItem[] {
    const scanOptions = [];
    scanOptions.push( { label: this.translate.instant( 'standard' ), value: 1 } );
    scanOptions.push( { label: this.translate.instant( 'zip' ), value: 0 } );
    return scanOptions;
  }
}
