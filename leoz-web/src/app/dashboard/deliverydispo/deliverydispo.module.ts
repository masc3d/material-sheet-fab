import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { DeliverydispoRoutingModule } from './deliverydispo-routing.module';
import { DeliverydispoComponent } from './deliverydispo.component';
import {
  ButtonModule,
  CheckboxModule,
  DataScrollerModule,
  DataTableModule,
  DropdownModule, FieldsetModule,
  RadioButtonModule,
  TabViewModule
} from 'primeng/primeng';

@NgModule({
  imports: [
    SharedModule,
    DeliverydispoRoutingModule,
    DataTableModule,
    FieldsetModule,
    TabViewModule,
    DropdownModule,
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
