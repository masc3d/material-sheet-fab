import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { TopBarComponent } from '../top-bar/top-bar.component';
import { TranslateModule } from '../core/translate/translate.module';
import { MsgBoxComponent } from './msg-box/msg-box.component';

@NgModule( {
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule
  ],
  declarations: [
    TopBarComponent,
    MsgBoxComponent],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    TopBarComponent,
    MsgBoxComponent ]
} )
export class SharedModule {
}
