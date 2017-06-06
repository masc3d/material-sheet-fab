import { OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { TranslateService } from './translate.service';

export class AbstractTranslateComponent implements OnInit, OnDestroy {

  private subscriptionI18n: Subscription;

  constructor( protected translate: TranslateService,
               protected doOnSubscribe: Function){
  }

  ngOnInit() {
    this.subscriptionI18n = this.translate.onLangChanged.subscribe(this.doOnSubscribe);
  }

  ngOnDestroy() {
    if (this.subscriptionI18n) {
      this.subscriptionI18n.unsubscribe();
    }
  }
}
