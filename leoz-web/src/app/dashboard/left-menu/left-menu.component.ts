import {Component, Inject, Renderer2} from '@angular/core';
import {Router} from '@angular/router';
import {DOCUMENT} from '@angular/platform-browser';

@Component({
  selector: 'app-left-menu',
  templateUrl: './left-menu.component.html'
})
export class LeftMenuComponent {

  constructor(private renderer: Renderer2,
              @Inject(DOCUMENT) private document: any,
              private router: Router) {
  }

  navigate(path: string) {
    this.router.navigate([path]);
    this.closeMenu();
  }

  closeMenu() {
    if (this.document && this.document.body) {
      this.renderer.removeClass(this.document.body, 'isOpenMenu');
      this.renderer.setProperty(this.document.body, 'scrollTop', 0);
    }
  }
}
