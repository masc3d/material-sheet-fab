import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { TourComponent } from './tour.component';
import { TourMapComponent } from './tour-map/tour-map.component';
import { TourDriverListComponent } from './tour-driver-list/tour-driver-list.component';
import { TourService } from './tour.service';
import { YagaModule } from '@yaga/leaflet-ng2';
import { TourRoutingModule } from './tour-routing.module';
import { DriverService } from './driver.service';
import { DriverFilterPipe } from './driver.pipe';

@NgModule( {
  imports: [
    SharedModule,
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
    TourService
  ],
  exports: [
    TourComponent
  ]
} )
export class TourModule {
}
