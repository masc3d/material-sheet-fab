import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

import { ImportscanRoutingModule } from './importscan-routing.module';
import { ImportscanComponent } from './importscan.component';


@NgModule( {
  imports: [
    SharedModule,
    ImportscanRoutingModule
  ],
  declarations: [
    ImportscanComponent
  ],
  exports: [ ImportscanComponent ],
} )

export class ImportscanModule {
}
