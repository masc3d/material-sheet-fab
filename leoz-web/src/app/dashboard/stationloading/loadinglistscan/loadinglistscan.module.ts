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
import { LoadinglistService } from './loadinglist.service';
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
    LoadinglistscanRoutingModule
  ],
  declarations: [ LoadinglistscanComponent ],
  providers: [
    LoadinglistService,
    DatePipe,
    // use fake backend
    // fakeBackendProvider,
    // MockBackend,
    // BaseRequestOptions
  ]
} )
export class LoadinglistscanModule {
}
