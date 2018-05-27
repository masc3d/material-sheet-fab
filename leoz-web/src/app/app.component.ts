import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { TranslateService } from './core/translate/translate.service';
import { environment } from '../environments/environment';
import { ElectronService } from './core/electron/electron.service';

@Component({
  // moduleId: module.id,
  selector: 'app-root',
  template: `
    <app-top-bar></app-top-bar>
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent implements OnInit {

  constructor(private translate: TranslateService,
              private electronService: ElectronService){}

  ngOnInit() {
    // set current language
    this.translate.use(`${environment.defLang}`);
  }
}
