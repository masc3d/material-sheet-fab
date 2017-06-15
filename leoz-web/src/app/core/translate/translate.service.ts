import { EventEmitter, Injectable } from '@angular/core';
import { DATEFORMATS } from './dateformats';
import { Translation } from './translation';
import { environment } from '../../../environments/environment';
import { CALENDAR_LOCALES } from './calendar-locales';

@Injectable()
export class TranslateService {

  public onLangChanged: EventEmitter<string> = new EventEmitter<string>();
  private curLang: string;

  constructor( private transl: Translation ) {
  }

  public use( lang: string ): void {
    // set current language
    this.curLang = lang;
    this.onLangChanged.emit( lang );
  }

  public setDateformat( flavor?: string ): string {
    flavor = flavor ? flavor : 'internal';
    return (this.curLang && DATEFORMATS[ flavor ][ this.curLang ])
      ? DATEFORMATS[ flavor ][ this.curLang ]
      : DATEFORMATS[ flavor ][ environment.defLang ];
  }

  public setCalendarLocale(): any {
    return (this.curLang && CALENDAR_LOCALES[ this.curLang ])
      ? CALENDAR_LOCALES[ this.curLang ]
      : CALENDAR_LOCALES[ environment.defLang ];
  }

  private translate( key: string ): string {
    const translation = key;

    if (this.transl.translations[ this.curLang ] && this.transl.translations[ this.curLang ][ key ]) {
      return this.transl.translations[ this.curLang ][ key ];
    }

    return translation;
  }

  public instant( key: string ) {
    // call translation
    return this.translate( key );
  }
}
