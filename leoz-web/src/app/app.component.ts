import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from './auth/authentication.service';
import { TranslateService } from './translate/translate.service';

@Component({
  moduleId: module.id,
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  isLoggedIn: boolean;
  public translatedText: string;
  public supportedLanguages: any[];

  constructor(private authService: AuthenticationService, private _translate: TranslateService){}

  ngOnInit() {
    this.authService.isLoggedIn.subscribe((isLoggedIn: boolean) => this.isLoggedIn = isLoggedIn);
    this.supportedLanguages = [
      { display: 'English', value: 'en' },
      { display: 'Deutsch', value: 'de' },
    ];

    // set current language
    this.selectLang('de');
  }

  selectLang(lang: string) {
    // set current lang;
    this._translate.use(lang);
    this.refreshText();
  }

  refreshText() {
    // refresh translation when language change
    this.translatedText = this._translate.instant('hello world');
  }
}
