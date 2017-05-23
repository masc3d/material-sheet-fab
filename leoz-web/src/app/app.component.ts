import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from './auth/authentication.service';
import { TranslateService } from './translate/translate.service';
import { environment } from '../environments/environment';

@Component({
  moduleId: module.id,
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  isLoggedIn: boolean;

  constructor(private authService: AuthenticationService, private _translate: TranslateService){}

  ngOnInit() {
    this.authService.isLoggedIn.subscribe((isLoggedIn: boolean) => this.isLoggedIn = isLoggedIn);
    // set current language
    this._translate.use(`${environment.defLang}`);
  }
}
