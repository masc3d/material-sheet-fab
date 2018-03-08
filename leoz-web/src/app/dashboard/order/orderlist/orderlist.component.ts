import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';


@Component( {
  selector: 'app-orderlist',
  templateUrl: './orderlist.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OrderlistComponent extends AbstractTranslateComponent implements OnInit {
  orderlistForm: FormGroup;

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();
    this.orderlistForm = this.fb.group( {} );
  }
}
