import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { TopBarComponent } from '../top-bar/top-bar.component';
import { LoginComponent } from '../login/login.component';
import { TranslateModule } from '../translate/translate.module';

@NgModule( {
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule
  ],
  declarations: [
    TopBarComponent,
    LoginComponent ],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    TopBarComponent,
    LoginComponent ]
} )
export class SharedModule {
}
