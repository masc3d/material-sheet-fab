import { Component, Inject, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';
import { TranslateService } from '../../translate/translate.service';

@Component({
  selector: 'app-top-menu',
  templateUrl: './top-menu.component.html'
})
export class TopMenuComponent {

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
