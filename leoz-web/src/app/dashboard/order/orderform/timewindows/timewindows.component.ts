import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { AbstractTranslateComponent } from '../../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../../core/translate/translate.service';

@Component({
  selector: 'app-timewindows',
  templateUrl: './timewindows.component.html',
  styles: [ `
    input[pinputtext] {
      height: 25px;
      margin-bottom: 2px;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimewindowsComponent extends AbstractTranslateComponent {

  @Input() isPickup: boolean

  constructor(public translate: TranslateService ,
              protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }
}
