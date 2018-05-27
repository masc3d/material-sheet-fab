import { NgModule } from '@angular/core';

import { ChangepasswordRoutingModule } from './changepassword-routing.module';
import { ChangepasswordComponent } from './changepassword.component';
import { SharedModule } from '../../../shared/shared.module';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ChangepasswordService } from './changepassword.service';

@NgModule( {
  imports: [
    SharedModule,
    InputTextModule,
    ButtonModule,
    ChangepasswordRoutingModule
  ],
  declarations: [
    ChangepasswordComponent
  ]
} )
export class ChangepasswordModule {
}
