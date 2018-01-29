import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InfoComponent extends AbstractTranslateComponent {

  constructor(public translate: TranslateService ,
              protected cd: ChangeDetectorRef,
              protected msgService: MsgService ) {
    super( translate, cd, msgService, () => {
    } );
  }
}
