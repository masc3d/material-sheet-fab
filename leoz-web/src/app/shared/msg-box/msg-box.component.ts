import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-msg-box',
  templateUrl: './msg-box.component.html'
})
export class MsgBoxComponent {

  @Input() msg: string;
  @Input() alertStyle: string;
}
