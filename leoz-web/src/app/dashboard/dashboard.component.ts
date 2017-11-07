import { ChangeDetectionStrategy, Component, HostListener } from '@angular/core';
import { KeyUpEventService } from '../core/key-up-event.service';

@Component( {
  selector: 'app-dashboard',
  template: `
    <app-left-menu></app-left-menu>
    <div id="main">
      <router-outlet></router-outlet>
    </div>`,
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class DashboardComponent {

  private preventDefaultKeys = ['F2', 'F3', 'F5', 'F7', 'F10', 'F12'];

  constructor(private keyUpService: KeyUpEventService) {
  }

  @HostListener( 'window:keydown', [ '$event' ] )
  onKeyDown( ev: KeyboardEvent ): void {
    if (this.preventDefaultKeys.includes(ev.key)) {
      ev.preventDefault();
    }
  }

  @HostListener( 'window:keyup', [ '$event' ] )
  onKeyUp( ev: KeyboardEvent ): void {
    this.keyUpService.nextEvent(ev);
  }
}
