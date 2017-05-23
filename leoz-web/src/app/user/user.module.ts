import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserComponent } from './user.component';
import { UserFormComponent } from './user-form/user-form.component';
import { UserListComponent } from './user-list/user-list.component';
import { UserService } from './user.service';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '../translate/translate.module';

@NgModule( {
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule
  ],
  declarations: [ UserComponent,
    UserFormComponent,
    UserListComponent ],
  exports: [ UserComponent ],
  providers: [ UserService ]
} )
export class UserModule {
}
