import {Component, Inject, Renderer2, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {DOCUMENT} from '@angular/platform-browser';
import { TranslateService } from '../../translate/translate.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-left-menu',
  templateUrl: './left-menu.component.html'
})

export class LeftMenuComponent implements OnInit{

  constructor(private renderer: Renderer2,
              @Inject(DOCUMENT) private document: any,
              private router: Router,
              private translateService: TranslateService) {
  }
  ngOnInit(): void {
    this.translateService.use(`${environment.defLang}`);
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
