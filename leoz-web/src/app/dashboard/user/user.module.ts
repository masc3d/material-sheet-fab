import { NgModule } from '@angular/core';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { FieldsetModule } from 'primeng/fieldset';
import { InputTextModule } from 'primeng/inputtext';

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
    TableModule,
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
