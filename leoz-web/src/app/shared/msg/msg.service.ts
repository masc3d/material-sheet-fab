import { Injectable } from '@angular/core';
import { Msg } from './msg.model';
import { Response } from '@angular/http';

@Injectable()
export class MsgService {

  clear(): Msg {
    return <Msg> {text: '', alertStyle: ''};
  }

  success( text: string ): Msg {
    return <Msg> {text: text, alertStyle: 'alert-success'};
  }

  handleResponse( resp: Response ): Msg {
    console.log( resp );
    return <Msg> {text: resp.json().title, alertStyle: 'alert-danger'};
  }
}
