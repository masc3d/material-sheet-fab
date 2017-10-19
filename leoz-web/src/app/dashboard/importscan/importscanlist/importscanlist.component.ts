import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

@Component( {
  selector: 'app-importscanlist',
  templateUrl: './importscanlist.component.html'
} )
export class ImportscanlistComponent extends AbstractTranslateComponent implements OnInit {

  importscanlistForm: FormGroup;

  constructor( private fb: FormBuilder,
               protected translate: TranslateService ) {
    super( translate, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();

    this.importscanlistForm = this.fb.group( {
     /* payload: [ null ],
      selectloadlist: [ null ],
      scanfield: [ null ],
      loadlistnumber: [ { value: '', disabled: true } ],
      printlabel: [ null ],
      basedon: [ 'actuallist' ]*/
    } );

  }
}
