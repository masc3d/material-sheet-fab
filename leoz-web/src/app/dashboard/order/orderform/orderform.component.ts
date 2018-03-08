import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-orderform',
  templateUrl: './orderform.component.html',
  styles: [ `
    input[pinputtext] {
      height: 25px;
      margin-bottom: 2px;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OrderformComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  orderformForm: FormGroup;
  pickupAddresses = [];
  deliveryAddresses = [];

  constructor( private fb: FormBuilder,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    this.orderformForm = this.fb.group( {
      consignorClientNos: [ null ],
      consignorClientReNos: [ null ],
      customer: [ null ],
      reference: [ null ],
    } );
  }
}
