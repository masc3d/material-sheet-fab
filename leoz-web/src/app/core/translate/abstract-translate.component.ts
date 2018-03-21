import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { takeUntil } from 'rxjs/operators';

import { Message } from 'primeng/components/common/api';

import { TranslateService } from './translate.service';
import { MsgService } from '../../shared/msg/msg.service';

export class AbstractTranslateComponent implements OnInit, OnDestroy {

  protected ngUnsubscribe: Subject<void> = new Subject<void>();

  dateFormat: string;
  dateFormatLong: string;
  dateFormatEvenLonger: string;
  dateFormatPrimeng: string;
  locale: any;
  msgs$: Observable<Message[]>;
  sticky$: Observable<boolean>;

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               protected doOnSubscribe?: Function ) {
  }

  ngOnInit() {
    this.msgService.clear();
    this.sticky$ = this.msgService.sticky$;
    this.msgs$ = this.msgService.msgs$;

    this.translate.onLangChanged
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
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
