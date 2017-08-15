import { Injectable } from '@angular/core';
import { Response } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/distinctUntilChanged';

import { Message } from 'primeng/primeng';

import { TranslateService } from '../../core/translate/translate.service';

@Injectable()
export class MsgService {

  private msgsSubject = new BehaviorSubject<Message[]>( <Message[]> [] );
  public msgs = this.msgsSubject.asObservable().distinctUntilChanged();

  constructor( private translate: TranslateService ) {
  }

  clear(): void {
    this.msgsSubject.next( <Message[]> [] );
  }

  success( text: string ): void {
    this.msgsSubject.next( <Message[]>  [ { severity: 'success', summary: '', detail: this.translate.instant(text) } ] );
  }

  error( text: string ): void {
    this.msgsSubject.next( <Message[]>  [ { severity: 'warn', summary: '', detail: this.translate.instant(text) } ] );
  }

  handleResponse( resp: Response ): void {
    this.msgsSubject.next( <Message[]>  [ {
      severity: 'warn',
      summary: '',
      detail: this.translate.instant(resp.json().title || 'webservice not available')
    } ] );
  }
}
