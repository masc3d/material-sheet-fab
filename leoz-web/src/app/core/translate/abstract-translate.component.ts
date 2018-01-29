import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';

import { TranslateService } from './translate.service';
import { MsgService } from '../../shared/msg/msg.service';
import { Observable } from 'rxjs/Observable';
import { Message } from 'primeng/primeng';

export class AbstractTranslateComponent implements OnInit, OnDestroy {

  protected ngUnsubscribe: Subject<void> = new Subject<void>();

  dateFormat: string;
  dateFormatLong: string;
  dateFormatEvenLonger: string;
  dateFormatPrimeng: string;
  locale: any;
  msgs$: Observable<Message[]>;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               protected doOnSubscribe?: Function ) {
  }

  ngOnInit() {
    this.msgService.clear();
    this.msgs$ = this.msgService.msgs$;

    this.translate.onLangChanged
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( lang: string ) => {
        this.dateFormat = this.translate.setDateformat( 'internal' );
        this.dateFormatLong = this.translate.setDateformat( 'internalLong' );
        this.dateFormatEvenLonger = this.translate.setDateformat( 'internalLonger' );
        this.dateFormatPrimeng = this.translate.setDateformat( 'primeng' );
        this.locale = this.translate.setCalendarLocale();
        if (this.doOnSubscribe) {
          this.doOnSubscribe( lang );
        }
        this.cd.markForCheck();
      } );
    this.dateFormat = this.translate.setDateformat( 'internal' );
    this.dateFormatLong = this.translate.setDateformat( 'internalLong' );
    this.dateFormatEvenLonger = this.translate.setDateformat( 'internalLonger' );
    this.dateFormatPrimeng = this.translate.setDateformat( 'primeng' );
    this.locale = this.translate.setCalendarLocale();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
