import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { AbstractTranslateComponent } from '../../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../../core/translate/translate.service';
import { MsgService } from '../../../../shared/msg/msg.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styles: [ `
    input[pinputtext] {
      height: 25px;
      margin-bottom: 2px;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactComponent  extends AbstractTranslateComponent {

  @Input() isPickup: boolean

  constructor(public translate: TranslateService ,
              protected cd: ChangeDetectorRef,
              protected msgService: MsgService ) {
    super( translate, cd, msgService );
  }
}
