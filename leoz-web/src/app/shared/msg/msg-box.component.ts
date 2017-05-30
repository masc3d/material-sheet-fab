import { Component, Input } from '@angular/core';
import { Msg } from './msg.model';

@Component({
  selector: 'app-msg-box',
  templateUrl: './msg-box.component.html'
})
export class MsgBoxComponent {

  @Input() msg: Msg;
}
