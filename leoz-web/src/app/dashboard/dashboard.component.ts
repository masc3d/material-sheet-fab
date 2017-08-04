import { Component, HostListener } from '@angular/core';
import { KeyUpEventService } from '../core/key-up-event.service';

@Component( {
  selector: 'app-dashboard',
  template: `
    <app-left-menu></app-left-menu>
    <div id="main">
      <router-outlet></router-outlet>
    </div>`
} )
export class DashboardComponent {

  constructor(private keyUpService: KeyUpEventService) {
  }

  @HostListener( 'window:keydown', [ '$event' ] )
  onKeyDown( ev: KeyboardEvent ): void {
    if (ev.key === 'F5' || ev.key === 'F12') {
      ev.preventDefault();
    }
  }

  @HostListener( 'window:keyup', [ '$event' ] )
  onKeyUp( ev: KeyboardEvent ): void {
    this.keyUpService.nextEvent(ev);
  }
}
