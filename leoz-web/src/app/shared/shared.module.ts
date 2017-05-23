import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { TopBarComponent } from '../top-bar/top-bar.component';
import { TranslateModule } from '../core/translate/translate.module';

@NgModule( {
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule
  ],
  declarations: [
    TopBarComponent],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    TopBarComponent ]
} )
export class SharedModule {
}
