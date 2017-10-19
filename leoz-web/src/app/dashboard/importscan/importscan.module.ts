import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

import { TabMenuModule } from 'primeng/primeng';
import { ImportscanRoutingModule } from './importscan-routing.module';
import { ImportscanComponent } from './importscan.component';


@NgModule( {
  imports: [
    SharedModule,
    TabMenuModule,
    ImportscanRoutingModule
  ],
  declarations: [
    ImportscanComponent
  ],
  exports: [ ImportscanComponent ],
} )

export class ImportscanModule {
}
