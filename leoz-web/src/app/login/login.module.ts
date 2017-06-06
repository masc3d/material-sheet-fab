import { NgModule } from '@angular/core';

import { LoginComponent } from './login.component';
import { SharedModule } from '../shared/shared.module';
import { ButtonModule, InputTextModule } from 'primeng/primeng';

@NgModule({
  imports: [
    SharedModule,
    InputTextModule,
    ButtonModule
  ],
  declarations: [LoginComponent]
})
export class LoginModule { }
