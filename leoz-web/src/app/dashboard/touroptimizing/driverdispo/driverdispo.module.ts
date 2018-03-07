import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';
import { FieldsetModule } from 'primeng/fieldset';
import { ButtonModule } from 'primeng/button';
import { DataScrollerModule } from 'primeng/datascroller';
import { TabMenuModule } from 'primeng/tabmenu';
import { TabViewModule } from 'primeng/tabview';
import { ToggleButtonModule } from 'primeng/primeng';
import { DialogModule } from 'primeng/dialog';

import { SharedModule } from '../../../shared/shared.module';
import { DispoModule } from '../dispo/dispo.module';
import { TouroptimizingService } from '../touroptimizing.service';
import { DriverdispoComponent } from './driverdispo.component';
import { DriverdispoRoutingModule } from './driverdispo-routing.module';

@NgModule( {
  imports: [
    SharedModule,
    FormsModule,
    FieldsetModule,
    InputTextModule,
    ButtonModule,
    ToggleButtonModule,
    CheckboxModule,
    DataScrollerModule,
    TabViewModule,
    TabMenuModule,
    DialogModule,
    DispoModule,
    DriverdispoRoutingModule
  ],
  declarations: [
    DriverdispoComponent
  ],
  exports: [
    DriverdispoComponent
  ],
  providers: [
    TouroptimizingService
  ]
} )

export class DriverdispoModule {
}
