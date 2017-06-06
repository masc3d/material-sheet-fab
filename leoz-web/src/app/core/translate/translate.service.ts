import { EventEmitter, Injectable } from '@angular/core';
import { Translation } from './translation';

@Injectable()
export class TranslateService {

  public onLangChanged: EventEmitter<string> = new EventEmitter<string>();
  private curLang: string;

  constructor(private transl: Translation) {
    console.log('-----translation');
  }

  public use(lang: string): void {
    // set current language
    this.curLang = lang;
    this.onLangChanged.emit(lang);
  }

  private translate(key: string): string {
    const translation = key;

    if (this.transl.translations[this.curLang] && this.transl.translations[this.curLang][key]) {
      return this.transl.translations[this.curLang][key];
    }

    return translation;
  }

  public instant(key: string) {
    // call translation
    return this.translate(key);
  }
}
