import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import 'rxjs/add/operator/filter';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';


@Component( {
  selector: 'app-orderlist',
  templateUrl: './orderlist.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OrderlistComponent extends AbstractTranslateComponent implements OnInit {
  orderlistForm: FormGroup;

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.orderlistForm = this.fb.group( {} );
  }
}
