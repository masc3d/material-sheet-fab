import { Injectable } from '@angular/core';
import { Msg } from './msg.model';
import { Response } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/distinctUntilChanged';

@Injectable()
export class MsgService {

  private msgSubject = new BehaviorSubject<Msg>( <Msg> { text: '', alertStyle: '' } );
  public msg = this.msgSubject.asObservable().distinctUntilChanged();

  clear(): void {
    this.msgSubject.next( <Msg> { text: '', alertStyle: '' } );
  }

  success( text: string ): void {
    this.msgSubject.next( <Msg> { text: text, alertStyle: 'ui-messages-success' } );
  }

  error( text: string ): void {
    this.msgSubject.next( <Msg> { text: text, alertStyle: 'ui-messages-error' } );
  }

  handleResponse( resp: Response ): void {
    this.msgSubject.next( <Msg> {
      text: resp.json().title || 'webservice not available',
      alertStyle: 'ui-messages-error'
    } );
  }
}
