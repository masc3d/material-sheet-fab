import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { SharedModule } from '../../shared/shared.module';
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
import { PickupdispoRoutingModule } from './pickupdispo-routing.module';
import { PickupdispoComponent } from './pickupdispo.component';

@NgModule({
  imports: [
    SharedModule,
    FormsModule,
    PickupdispoRoutingModule,
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
  declarations: [PickupdispoComponent],
  exports: [PickupdispoComponent],
  providers: [
  ]
})
export class PickupdispoModule { }
