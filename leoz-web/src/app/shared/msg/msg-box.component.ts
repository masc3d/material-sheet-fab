import { Component, Input } from '@angular/core';
import { Message } from 'primeng/primeng';


@Component( {
  selector: 'app-msg-box',
  template: `
    <p-growl [(value)]="msgs"></p-growl>
  `
} )
export class MsgBoxComponent {

  @Input() msgs: Message[];
}
