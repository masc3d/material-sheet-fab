import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { GrowlModule } from 'primeng/growl';

import { TopBarComponent } from '../top-bar/top-bar.component';
import { TranslateModule } from '../core/translate/translate.module';
import { MsgBoxComponent } from './msg/msg-box.component';
import { MsgService } from './msg/msg.service';

@NgModule( {
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    GrowlModule
  ],
  providers: [ MsgService ],
  declarations: [
    TopBarComponent,
    MsgBoxComponent ],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    TopBarComponent,
    MsgBoxComponent ]
} )
export class SharedModule {
}
