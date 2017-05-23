import { Component, Inject, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';
import { TranslateService } from '../core/translate/translate.service';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html'
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
