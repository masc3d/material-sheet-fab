import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { SelectItem } from 'primeng/api';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { MsgService } from '../../shared/msg/msg.service';



@Component( {
  selector: 'app-stateofshipments',
  templateUrl: './stateofshipments.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class StateofshipmentsComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  stateofshipmentForm: FormGroup;
  actionOptions: SelectItem[];

  constructor( private fb: FormBuilder,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService ) {
    super( translate, cd, msgService, () => {
      this.actionOptions = this.createActionOptions();
    } );
  }

  createActionOptions() {
    return [
      { label: this.translate.instant( 'action' ), value: 0 },
      { label: this.translate.instant( 'statusreport' ), value: 0 },
      { label: this.translate.instant( 'reprint_label_for_pack' ), value: 0 },
      { label: this.translate.instant( 'reprint_label_for_shipment' ), value: 0 },
      { label: this.translate.instant( 'preadvice' ), value: 0 },
      { label: this.translate.instant( 'open_Calendar' ), value: 0 } ];
  }

  ngOnInit() {
    super.ngOnInit();
    this.actionOptions = this.createActionOptions();
    this.stateofshipmentForm = this.fb.group( {
      consignorClientNos: [ null ],
      consignorClientReNos: [ null ],
      customer: [ null ],
      reference: [ null ],
    } );
  }
}
