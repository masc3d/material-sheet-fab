import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { MsgService } from '../../shared/msg/msg.service';
import { Observable } from 'rxjs/Observable';
import { Message } from 'primeng/primeng';

@Component( {
  selector: 'app-tour',
  template: `
    <h2>{{'tour' | translate}}</h2>
    <div class="mbDashboardContent">
      <app-msg-box [msgs]="msgs$ | async"></app-msg-box>
      <app-tour-driver-list></app-tour-driver-list>
      <app-tour-map></app-tour-map>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class TourComponent implements OnInit {
  msgs$: Observable<Message[]>;

  constructor( private msgService: MsgService ) {
  }

  ngOnInit(): void {
    this.msgs$ = this.msgService.msgs$;
  }
}
