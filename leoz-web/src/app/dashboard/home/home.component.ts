import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  template: `
    <div class="content-header">
      <h2>{{'home' | translate}}</h2>
    </div>

    <div id="content" class="content-box">
      <div class="section"><span>
    <img src="assets/images/deku-logo-background.png" width="100%" height="100%" alt="logo"></span>
      </div>
    </div>
  `
})
export class HomeComponent {
}
