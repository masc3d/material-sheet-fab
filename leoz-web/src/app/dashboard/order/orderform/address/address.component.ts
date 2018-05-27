import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractTranslateComponent } from '../../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../../core/translate/translate.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MsgService } from '../../../../shared/msg/msg.service';

@Component( {
  selector: 'app-address',
  templateUrl: './address.component.html',
  styles: [ `
    input[pinputtext] {
      height: 25px;
      margin-bottom: 2px;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class AddressComponent extends AbstractTranslateComponent implements OnInit, OnDestroy {

  adressForm: FormGroup;
  @Input() addresses;
  @Input() placeholderLine1: string;
  @Input() placeholderLine2: string;
  @Input() placeholderLine3: string;

  countryCodes = [];

  constructor( private fb: FormBuilder,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    this.adressForm = this.fb.group( {
      placeholderLine1: [ null, [ Validators.required, Validators.maxLength( 50 ) ] ],
      placeholderLine2: [ null ],
      placeholderLine3: [ null ],
      CC: [ null, [ Validators.required, Validators.maxLength( 2 ) ] ],
      zip: [ null, [ Validators.required, Validators.maxLength( 10 ) ] ],
      city: [ null, [ Validators.required, Validators.maxLength( 50 ) ] ],
      street: [ null, [ Validators.required, Validators.maxLength( 50 ) ] ],
      No: [ null ],
    } );
  }
}
