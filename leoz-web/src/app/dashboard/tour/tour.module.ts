import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

import { ButtonModule, DataTableModule, DropdownModule } from 'primeng/primeng';
import { YagaModule } from '@yaga/leaflet-ng2';

import { SharedModule } from '../../shared/shared.module';
import { TourComponent } from './tour.component';
import { TourMapComponent } from './tour-map/tour-map.component';
import { TourDriverListComponent } from './tour-driver-list/tour-driver-list.component';
import { TourService } from './tour.service';
import { TourRoutingModule } from './tour-routing.module';
import { DriverService } from './driver.service';
import { DriverFilterPipe } from './driver.pipe';
import { UserService } from '../user/user.service';

@NgModule( {
  imports: [
    SharedModule,
    FormsModule,
    DataTableModule,
    DropdownModule,
    ButtonModule,
    YagaModule,
    TourRoutingModule
  ],
  declarations: [
    TourComponent,
    TourMapComponent,
    TourDriverListComponent,
    DriverFilterPipe
  ],
  providers: [
    DriverService,
    UserService,
    TourService,
    DatePipe
  ],
  exports: [
    TourComponent
  ]
} )
export class TourModule {
}
