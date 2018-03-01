import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { DriverdispoComponent } from './driverdispo.component';
import { DriverdispoRoutingModule } from './driverdispo-routing.module';
import { TouroptimizingService } from '../touroptimizing.service';

@NgModule( {
  imports: [
    SharedModule,
    DriverdispoRoutingModule,
  ],
  declarations: [ DriverdispoComponent ],
  exports: [ DriverdispoComponent ],
  providers: [ TouroptimizingService ]
} )
export class DriverdispoModule {
}
