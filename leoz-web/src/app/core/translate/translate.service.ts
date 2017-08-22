import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

import { DATEFORMATS } from './dateformats';
import { Translation } from './translation';
import { environment } from '../../../environments/environment';
import { CALENDAR_LOCALES } from './calendar-locales';
import { User } from '../../dashboard/user/user.model';

@Injectable()
export class TranslateService {

  public onLangChanged: Subject<string> = new Subject<string>();
  private curLang: string;

  constructor( private transl: Translation ) {
  }

  public use( lang: string ): void {
    // set current language
    this.curLang = lang;
    this.onLangChanged.next( lang );
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

  public role( role: User.RoleEnum ): string {
    let key;
    switch (role) {
      case User.RoleEnum.ADMIN:
        key = 'Admin';
        break;
      case User.RoleEnum.POWERUSER:
        key = 'Poweruser';
        break;
      case User.RoleEnum.USER:
        key = 'User';
        break;
      case User.RoleEnum.DRIVER:
        key = 'Driver';
        break;
      case User.RoleEnum.CUSTOMER:
        key = 'Customer';
        break;
      default:
        key = '';
        break;
    }
    return this.translate( key );
  }
}
