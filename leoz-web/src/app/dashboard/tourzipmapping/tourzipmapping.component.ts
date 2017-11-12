import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';

@Component( {
  selector: 'app-tourzipmapping',
  templateUrl: './tourzipmapping.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TourzipmappingComponent extends AbstractTranslateComponent implements OnInit {

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               public router: Router ) {
    super( translate, cd, () => {
    } );
  }

  ngOnInit() {
    super.ngOnInit();
  }
}
