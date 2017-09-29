import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { StationloadingRoutingModule } from './stationloading-routing.module';

import { StationloadingComponent } from './stationloading.component';
import { TabMenuModule } from 'primeng/primeng';


@NgModule({
  imports: [
    SharedModule,
    TabMenuModule,
    StationloadingRoutingModule
  ],
  declarations: [StationloadingComponent],
  exports: [StationloadingComponent],
})

export class StationloadingModule { }
