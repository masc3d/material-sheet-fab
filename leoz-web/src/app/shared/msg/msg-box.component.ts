import { Component, Input } from '@angular/core';
import { Msg } from './msg.model';

@Component( {
  selector: 'app-msg-box',
  template: `
    <div *ngIf="msg.text.length > 0" class="ui-messages ui-widget ui-corner-all"
         style="display:block" [ngClass]="msg.alertStyle">
      {{msg.text | translate}}
    </div>
  `
} )
export class MsgBoxComponent {

  @Input() msg: Msg;
}
