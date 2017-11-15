import { NgModule } from '@angular/core';

import {
  ButtonModule,
  CalendarModule,
  CheckboxModule,
  DataTableModule,
  DropdownModule,
  FieldsetModule,
  InputTextareaModule,
  InputTextModule,
  ProgressBarModule,
  RadioButtonModule,
  TabViewModule
} from 'primeng/primeng';

import { SharedModule } from '../../../shared/shared.module';
import { OrderprofileComponent } from './orderprofile.component';
import { OrderprofileRoutingModule } from './orderprofile-routing.module';

@NgModule( {
  imports: [
    SharedModule,
    DataTableModule,
    FieldsetModule,
    InputTextModule,
    DropdownModule,
    ProgressBarModule,
    ButtonModule,
    CalendarModule,
    CheckboxModule,
    RadioButtonModule,
    InputTextareaModule,
    TabViewModule,
    OrderprofileRoutingModule
  ],
  declarations: [ OrderprofileComponent ],
  providers: [
  ]
} )
export class OrderprofileModule {
}
