import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { SharedModule } from '../../shared/shared.module';
import { DeliverydispoRoutingModule } from './deliverydispo-routing.module';
import { DeliverydispoComponent } from './deliverydispo.component';
import {
  ButtonModule,
  CalendarModule,
  CheckboxModule,
  DataScrollerModule,
  DataTableModule,
  DropdownModule,
  FieldsetModule,
  RadioButtonModule,
  TabViewModule
} from 'primeng/primeng';

@NgModule({
  imports: [
    SharedModule,
    FormsModule,
    DeliverydispoRoutingModule,
    DataTableModule,
    FieldsetModule,
    TabViewModule,
    DropdownModule,
    CalendarModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule,
    DataScrollerModule,
  ],
  declarations: [DeliverydispoComponent],
  exports: [DeliverydispoComponent],
  providers: [
  ]
})
export class DeliverydispoModule { }
