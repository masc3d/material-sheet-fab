import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/distinctUntilChanged';

import { Message } from 'primeng/primeng';

import { TranslateService } from '../../core/translate/translate.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

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
    this.msgsSubject.next( <Message[]>  [ {
      severity: 'success',
      summary: '',
      detail: this.translate.instant( text )
    } ] );
  }

  error( text: string ): void {
    this.msgsSubject.next( <Message[]>  [ { severity: 'warn', summary: '', detail: this.translate.instant( text ) } ] );
  }

  handleResponse( resp: HttpResponse<any> | HttpErrorResponse ): void {
    let msg = '';
    try {
      let json;
      if (resp instanceof HttpResponse) {
        json = JSON.parse(resp.body);
      } else {
        json = JSON.parse(resp.error);
      }
      if (json.title) {
        msg = json.title
      } else {
        msg = 'webservice not available';
        if (json.message) {
          this.handleBackendError( resp.url, json.message );
        }
      }
    } catch (e) {
      msg = 'webservice not available';
      this.handleBackendError( resp.url, msg );
    }
    this.msgsSubject.next( <Message[]>  [ {
      severity: 'warn',
      summary: '',
      detail: this.translate.instant( msg )
    } ] );
  }

  private handleBackendError( url: string, errMsg: string ) {
    console.error( 'Backend Error', url, errMsg );
  }
}
