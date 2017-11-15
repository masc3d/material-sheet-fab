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
import { OrderlistRoutingModule } from './orderlist-routing.module';
import { OrderlistComponent } from './orderlist.component';

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
    OrderlistRoutingModule
  ],
  declarations: [ OrderlistComponent ],
  providers: [
  ]
} )
export class OrderlistModule {
}
