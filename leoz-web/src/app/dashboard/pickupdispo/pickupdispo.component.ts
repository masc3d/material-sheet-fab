import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { MsgService } from '../../shared/msg/msg.service';

@Component( {
  selector: 'app-pickupdispo',
  templateUrl: './pickupdispo.component.html',
  styles: [`
  .ui-tabview-panel {
    height: 100% !important;
  }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class PickupdispoComponent extends AbstractTranslateComponent implements OnInit {

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               public router: Router ) {
    super( translate, cd, msgService, () => {
    } );
  }
}
