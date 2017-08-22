import { Component, Input } from '@angular/core';
import { Message } from 'primeng/primeng';


@Component( {
  selector: 'app-msg-box',
  template: `
    <p-growl [(value)]="msgs"></p-growl>
   <!-- <div *ngIf="msg.text.length > 0" class="ui-messages ui-widget ui-corner-all"
         style="display:block" [ngClass]="msg.alertStyle">
      {{msg.text | translate}}
    </div>-->
  `
} )
export class MsgBoxComponent {

  @Input() msgs: Message[];
}
