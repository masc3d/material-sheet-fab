import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe } from './translate.pipe';
import { TranslateService } from './translate.service';
import { Translation } from './translation';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    TranslatePipe
  ],
  exports: [TranslatePipe],
  providers: [
    Translation,
    TranslateService
  ]
})
export class TranslateModule { }
