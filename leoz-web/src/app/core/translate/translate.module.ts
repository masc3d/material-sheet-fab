import {NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe } from './translate.pipe';
import { DateMomentjsPipe } from './date-momentjs.pipe';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    TranslatePipe,
    DateMomentjsPipe
  ],
  exports: [
    TranslatePipe,
    DateMomentjsPipe
  ]
})
export class TranslateModule {
}
