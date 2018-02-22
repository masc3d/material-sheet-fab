import { NgModule } from '@angular/core';

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
import { DataScrollerModule } from 'primeng/datascroller';

import { SharedModule } from '../../shared/shared.module';
import { DeliveryscanRoutingModule } from './deliveryscan-routing.module';
import { DeliveryscanComponent } from './deliveryscan.component';
import { DeliveryscanService } from './deliveryscan.service';
import { DatePipe } from '@angular/common';

@NgModule( {
  imports: [
    SharedModule,
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
    DeliveryscanRoutingModule,
    DataScrollerModule,
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
