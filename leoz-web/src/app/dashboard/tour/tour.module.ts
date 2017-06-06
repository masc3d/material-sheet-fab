import { NgModule } from '@angular/core';
import { DataTableModule } from 'primeng/primeng';

import { SharedModule } from '../../shared/shared.module';
import { TourComponent } from './tour.component';
import { TourMapComponent } from './tour-map/tour-map.component';
import { TourDriverListComponent } from './tour-driver-list/tour-driver-list.component';
import { TourService } from './tour.service';
import { YagaModule } from '@yaga/leaflet-ng2';
import { TourRoutingModule } from './tour-routing.module';
import { DriverService } from './driver.service';

@NgModule( {
  imports: [
    SharedModule,
    DataTableModule,
    YagaModule,
    TourRoutingModule
  ],
  declarations: [
    TourComponent,
    TourMapComponent,
    TourDriverListComponent
  ],
  providers: [
    DriverService,
    TourService
  ],
  exports: [
    TourComponent
  ]
} )
export class TourModule {
}
