import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import 'rxjs/add/operator/filter';


import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

@Component( {
  selector: 'app-orderprofile',
  templateUrl: './orderprofile.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class OrderprofileComponent extends AbstractTranslateComponent implements OnInit {


  orderprofileForm: FormGroup;

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.orderprofileForm = this.fb.group( {
    });
    }
  }
