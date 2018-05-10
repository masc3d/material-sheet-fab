import { Injectable } from '@angular/core';
import { LANG_EN_TRANS } from './lang-en';
import { LANG_DE_TRANS } from './lang-de';

@Injectable({
  providedIn: 'root',
})
export class Translation {

  public translations = {
    ['en']: LANG_EN_TRANS,
    ['de']: LANG_DE_TRANS
  };
}
