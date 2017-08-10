import { NgModule } from '@angular/core';

import {
  DataTableModule, InputTextModule, DropdownModule, ButtonModule, CalendarModule, FieldsetModule,
  CheckboxModule
} from 'primeng/primeng';

import { SharedModule } from '../../shared/shared.module';
import { UserRoutingModule } from './user-routing.module';

import { UserComponent } from './user.component';
import { UserFormComponent } from './user-form/user-form.component';
import { UserListComponent } from './user-list/user-list.component';
import { UserService } from './user.service';
import { UserFilterPipe } from './user.pipe';

@NgModule( {
  imports: [
    SharedModule,
    DataTableModule,
    InputTextModule,
    DropdownModule,
    ButtonModule,
    FieldsetModule,
    CheckboxModule,
    UserRoutingModule,
    CalendarModule
  ],
  declarations: [
    UserComponent,
    UserFormComponent,
    UserListComponent,
    UserFilterPipe
  ],
  exports: [ UserComponent ],
  providers: [ UserService ]
} )
export class UserModule {
}
