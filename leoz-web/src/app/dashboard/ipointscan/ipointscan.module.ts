import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

import { IpointscanComponent } from './ipointscan.component';
import { IpointscanRoutingModule } from './ipointscan-routing.module';


@NgModule( {
  imports: [
    SharedModule,
    IpointscanRoutingModule
  ],
  declarations: [
    IpointscanComponent
  ],
  exports: [ IpointscanComponent ],
} )

export class IpointscanModule {
}
