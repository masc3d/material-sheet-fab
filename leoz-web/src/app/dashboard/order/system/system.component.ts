import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';

@Component({
  selector: 'app-system',
  templateUrl: './system.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SystemComponent extends AbstractTranslateComponent {

  @Input() isPickup: boolean

  constructor(public translate: TranslateService ,
              protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }
}
