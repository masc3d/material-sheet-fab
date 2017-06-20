import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  template: `
    <app-left-menu></app-left-menu>
    <div id="main">
      <router-outlet></router-outlet>
    </div>`
})
export class DashboardComponent {
}
