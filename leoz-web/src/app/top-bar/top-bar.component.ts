import { ChangeDetectionStrategy, Component, Inject, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { TranslateService } from '../core/translate/translate.service';

@Component({
  selector: 'app-top-bar',
  template: `
    <div id="top-menu">
      <div>
        <img src="assets/images/logo.png" alt="">
      </div>
      <div style="margin-top: 10px; margin-left: 80px;">
        <img (click)="changeLang('de')" src="assets/images/de.png" width="33px" height="20px" alt="de">
        <br>
        <img (click)="changeLang('en')" src="assets/images/gb.png" width="33px" height="19px" alt="gb">
      </div>
      <div id="mobile-main-menu" (click)="toggle()">
        <i class="fas fa-bars" style="color: #ffffff; font-size: 32px;"></i>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopBarComponent {

  public constructor(private renderer: Renderer2,
                     @Inject(DOCUMENT) private document: any,
                     private translateService: TranslateService ) {
  }

  public changeLang(lang: string) {
    this.translateService.use(lang);
  }

  public toggle(): void {
    if (this.document && this.document.body) {
      if (this.document.body.classList.contains('isOpenMenu')) {
        this.renderer.removeClass(this.document.body, 'isOpenMenu');
        this.renderer.setProperty(this.document.body, 'scrollTop', 0);
      } else {
        this.renderer.addClass(this.document.body, 'isOpenMenu');
      }
    }
  }
}
