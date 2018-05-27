import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { TableModule } from 'primeng/table';
import { YagaModule } from '@yaga/leaflet-ng2';

import { SharedModule } from '../../shared/shared.module';
import { TourComponent } from './tour.component';
import { TourMapComponent } from './tour-map/tour-map.component';
import { TourDriverListComponent } from './tour-driver-list/tour-driver-list.component';
import { TourService } from './tour.service';
import { TourRoutingModule } from './tour-routing.module';
import { DriverService } from './driver.service';
import { UserService } from '../user/user.service';
import { DateMomentjsPipe } from '../../core/translate/date-momentjs.pipe';

@NgModule( {
  imports: [
    SharedModule,
    FormsModule,
    TableModule,
    DropdownModule,
    CalendarModule,
    ButtonModule,
    YagaModule,
    TourRoutingModule
  ],
  declarations: [
    TourComponent,
    TourMapComponent,
    TourDriverListComponent
  ],
  exports: [
    TourComponent
  ]
} )
export class TourModule {
}
