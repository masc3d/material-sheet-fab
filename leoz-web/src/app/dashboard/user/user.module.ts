import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { UserRoutingModule } from './user-routing.module';

import { UserComponent } from './user.component';
import { UserFormComponent } from './user-form/user-form.component';
import { UserListComponent } from './user-list/user-list.component';
import { UserService } from './user.service';

@NgModule( {
  imports: [
    SharedModule,
    UserRoutingModule
  ],
  declarations: [ UserComponent,
    UserFormComponent,
    UserListComponent ],
  exports: [ UserComponent ],
  providers: [ UserService ]
} )
export class UserModule {
}
