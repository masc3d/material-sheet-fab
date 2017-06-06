import { Component, OnInit } from '@angular/core';
import { TranslateService } from './core/translate/translate.service';
import { environment } from '../environments/environment';

@Component({
  moduleId: module.id,
  selector: 'app-root',
  template: `
    <app-top-bar></app-top-bar>
    <router-outlet></router-outlet>
  `
})
export class AppComponent implements OnInit {

  constructor(private translate: TranslateService){}

  ngOnInit() {
    // set current language
    this.translate.use(`${environment.defLang}`);
  }
}
