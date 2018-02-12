import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { FieldsetModule } from 'primeng/fieldset';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TabViewModule } from 'primeng/tabview';
import { DataScrollerModule } from 'primeng/datascroller';

import { SharedModule } from '../../shared/shared.module';
import { PickupdispoRoutingModule } from './pickupdispo-routing.module';
import { PickupdispoComponent } from './pickupdispo.component';

@NgModule({
  imports: [
    SharedModule,
    FormsModule,
    PickupdispoRoutingModule,
    TableModule,
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
