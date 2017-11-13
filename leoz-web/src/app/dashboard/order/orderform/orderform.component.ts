import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import 'rxjs/add/operator/filter';


import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

@Component( {
  selector: 'app-orderform',
  templateUrl: './orderform.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OrderformComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  orderformForm: FormGroup;

  constructor( private fb: FormBuilder,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();

    this.orderformForm = this.fb.group( {} );
  }
}
