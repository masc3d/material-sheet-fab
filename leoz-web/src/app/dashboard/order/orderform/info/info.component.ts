import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { TranslateService } from '../../../../core/translate/translate.service';
import { AbstractTranslateComponent } from '../../../../core/translate/abstract-translate.component';
import { MsgService } from '../../../../shared/msg/msg.service';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styles: [ `
    input[pinputtext] {
      height: 25px;
      margin-bottom: 2px;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InfoComponent extends AbstractTranslateComponent {

  @Input() isPickup: boolean

  constructor(public translate: TranslateService ,
              protected cd: ChangeDetectorRef,
              protected msgService: MsgService ) {
    super( translate, cd, msgService );
  }
}
