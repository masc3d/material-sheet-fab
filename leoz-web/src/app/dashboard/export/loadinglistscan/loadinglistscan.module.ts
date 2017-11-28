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
import { LoadinglistscanRoutingModule } from './loadinglistscan-routing.module';
import { LoadinglistscanComponent } from './loadinglistscan.component';
import { DatePipe } from '@angular/common';
import { LoadinglistscanService } from './loadinglistscan.service';

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
    LoadinglistscanRoutingModule
  ],
  declarations: [ LoadinglistscanComponent ],
  providers: [
    DatePipe,
    LoadinglistscanService
    // use fake backend
    // fakeBackendProvider,
    // MockBackend,
    // BaseRequestOptions
  ]
} )
export class LoadinglistscanModule {
}
