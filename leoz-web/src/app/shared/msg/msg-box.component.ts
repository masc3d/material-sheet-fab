import { Component, Input } from '@angular/core';
import { Message } from 'primeng/primeng';


@Component( {
  selector: 'app-msg-box',
  template: `
    <p-growl [(value)]="msgs" [sticky]='sticky'></p-growl>
  `
} )
export class MsgBoxComponent {

  @Input() sticky: boolean;
  @Input() msgs: Message[];
}
