import { Component, OnInit } from '@angular/core';
import { Msg } from '../../shared/msg/msg.model';
import { MsgService } from '../../shared/msg/msg.service';
import { Observable } from 'rxjs/Observable';

@Component( {
  selector: 'app-tour',
  template: `
    <h2>{{'tour' | translate}}</h2>
    <app-msg-box [msg]="msg | async"></app-msg-box>
    <app-tour-driver-list></app-tour-driver-list>
    <app-tour-map></app-tour-map>
  `
} )
export class TourComponent implements OnInit {
  msg: Observable<Msg>;

  constructor( private msgService: MsgService ) {
  }

  ngOnInit(): void {
    this.msg = this.msgService.msg;
  }
}
