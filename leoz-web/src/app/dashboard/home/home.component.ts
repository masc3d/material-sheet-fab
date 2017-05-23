import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { TranslateService } from '../../core/translate/translate.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  constructor(private translateService: TranslateService){}

  ngOnInit(): void {
    this.translateService.use(`${environment.defLang}`);
  }
}
