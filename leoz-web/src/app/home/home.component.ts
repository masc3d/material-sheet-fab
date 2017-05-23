import { Component, OnInit } from '@angular/core';
import { TranslateService } from '../translate/translate.service';
import { environment } from '../../environments/environment';

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
