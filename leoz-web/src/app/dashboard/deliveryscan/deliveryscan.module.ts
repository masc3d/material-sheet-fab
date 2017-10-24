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

import { SharedModule } from '../../shared/shared.module';
import { DeliveryscanRoutingModule } from './deliveryscan-routing.module';
import { DeliveryscanComponent } from './deliveryscan.component';
import { DeliveryscanService } from './deliveryscan.service';
import { DatePipe } from '@angular/common';

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
    DeliveryscanRoutingModule
  ],
  declarations: [ DeliveryscanComponent ],
  providers: [
    DeliveryscanService,
    DatePipe,
    // use fake backend
    // fakeBackendProvider,
    // MockBackend,
    // BaseRequestOptions
  ]
} )
export class DeliveryscanModule {
}
