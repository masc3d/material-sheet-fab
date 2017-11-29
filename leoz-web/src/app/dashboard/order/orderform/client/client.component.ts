import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { TranslateService } from '../../../../core/translate/translate.service';
import { AbstractTranslateComponent } from '../../../../core/translate/abstract-translate.component';

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClientComponent extends AbstractTranslateComponent {

  @Input() isPickup: boolean

  constructor(public translate: TranslateService ,
              protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }
}
