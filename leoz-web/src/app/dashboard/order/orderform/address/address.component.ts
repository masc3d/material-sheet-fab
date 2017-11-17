import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { AbstractTranslateComponent } from '../../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../../core/translate/translate.service';

@Component( {
  selector: 'app-address',
  templateUrl: './address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class AddressComponent extends AbstractTranslateComponent {

  @Input() addresses;
  @Input() placeholderLine1: string;
  @Input() placeholderLine2: string;
  @Input() placeholderLine3: string;

  countryCodes = [];

  constructor(public translate: TranslateService,
               protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }

}
