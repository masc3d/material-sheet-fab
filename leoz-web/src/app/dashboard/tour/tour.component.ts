import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';

import { Message } from 'primeng/components/common/api';

import { MsgService } from '../../shared/msg/msg.service';

@Component( {
  selector: 'app-tour',
  template: `
    <div class="text2button" style="margin-left: 9px;">
      {{'tour' | translate}}
    </div>
    <div class="mbDashboardContent">
      <app-msg-box [msgs]="msgs$ | async" [sticky]="false"></app-msg-box>
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
