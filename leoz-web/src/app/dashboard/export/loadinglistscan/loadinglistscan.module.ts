import { NgModule } from '@angular/core';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { FieldsetModule } from 'primeng/fieldset';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { ProgressBarModule } from 'primeng/progressbar';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TabViewModule } from 'primeng/tabview';

import { SharedModule } from '../../../shared/shared.module';
import { LoadinglistscanRoutingModule } from './loadinglistscan-routing.module';
import { LoadinglistscanComponent } from './loadinglistscan.component';
import { DatePipe } from '@angular/common';
import { LoadinglistscanService } from './loadinglistscan.service';

@NgModule( {
  imports: [
    SharedModule,
    TableModule,
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
