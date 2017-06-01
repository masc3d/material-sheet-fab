import { Component, Input } from '@angular/core';
import { Msg } from './msg.model';

@Component({
  selector: 'app-msg-box',
  template: `
    <div *ngIf="msg.text.length > 0" class="alert" [ngClass]="msg.alertStyle">{{msg.text | translate}}</div>
  `
})
export class MsgBoxComponent {

  @Input() msg: Msg;
}
