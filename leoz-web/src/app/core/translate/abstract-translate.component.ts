import { OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { TranslateService } from './translate.service';

export class AbstractTranslateComponent implements OnInit, OnDestroy {

  private subscriptionI18n: Subscription;
  dateFormat: string;
  dateFormatPrimeng: string;
  locale: any;

  constructor( protected translate: TranslateService,
               protected doOnSubscribe?: Function ) {
  }

  ngOnInit() {
    this.subscriptionI18n = this.translate.onLangChanged.subscribe( ( lang: string ) => {
      this.dateFormat = this.translate.setDateformat('internal');
      this.dateFormatPrimeng = this.translate.setDateformat('primeng');
      this.locale = this.translate.setCalendarLocale();
      if (this.doOnSubscribe) {
        this.doOnSubscribe(lang);
      }
    } );
    this.dateFormat = this.translate.setDateformat('internal');
    this.dateFormatPrimeng = this.translate.setDateformat('primeng');
    this.locale = this.translate.setCalendarLocale();
  }

  ngOnDestroy() {
    if (this.subscriptionI18n) {
      this.subscriptionI18n.unsubscribe();
    }
  }
}
