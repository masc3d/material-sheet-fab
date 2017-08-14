import { OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';

import { TranslateService } from './translate.service';

export class AbstractTranslateComponent implements OnInit, OnDestroy {

  protected ngUnsubscribe: Subject<void> = new Subject<void>();

  dateFormat: string;
  dateFormatLong: string;
  dateFormatPrimeng: string;
  locale: any;

  constructor( protected translate: TranslateService,
               protected doOnSubscribe?: Function ) {
  }

  ngOnInit() {
    this.translate.onLangChanged
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( lang: string ) => {
        this.dateFormat = this.translate.setDateformat( 'internal' );
        this.dateFormatLong = this.translate.setDateformat( 'internalLong' );
        this.dateFormatPrimeng = this.translate.setDateformat( 'primeng' );
        this.locale = this.translate.setCalendarLocale();
        if (this.doOnSubscribe) {
          this.doOnSubscribe( lang );
        }
      } );
    this.dateFormat = this.translate.setDateformat( 'internal' );
    this.dateFormatLong = this.translate.setDateformat( 'internalLong' );
    this.dateFormatPrimeng = this.translate.setDateformat( 'primeng' );
    this.locale = this.translate.setCalendarLocale();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
