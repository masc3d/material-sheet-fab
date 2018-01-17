import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { TranslateService } from '../../../../core/translate/translate.service';
import { AbstractTranslateComponent } from '../../../../core/translate/abstract-translate.component';

@Component({
  selector: 'app-productspec',
  templateUrl: './productspec.component.html',
  styles: [ `
    input[pinputtext] {
      height: 25px;
      margin-bottom: 2px;
    }
  ` ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductspecComponent extends AbstractTranslateComponent {

  @Input() isPickup: boolean

  constructor(public translate: TranslateService ,
              protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => {
    } );
  }
}
