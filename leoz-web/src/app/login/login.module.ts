import { NgModule } from '@angular/core';

import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';

import { LoginComponent } from './login.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [
    SharedModule,
    InputTextModule,
    ButtonModule
  ],
  declarations: [LoginComponent]
})
export class LoginModule { }
