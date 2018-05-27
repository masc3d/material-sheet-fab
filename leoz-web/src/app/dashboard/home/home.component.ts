import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-home',
  template: `
    <div class="content-header">
      <h2><!--{{'home' | translate}}--> &nbsp;</h2>
    </div>

    <div id="content" class="content-box">
      <div class="section"><span>
    <img src="assets/images/deku-logo-background.png" width="100%" height="100%" alt="logo"></span>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeComponent {
}
