import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { ExportRoutingModule } from './export-routing.module';

import { ExportComponent } from './export.component';
import { TabMenuModule } from 'primeng/primeng';

@NgModule( {
  imports: [
    SharedModule,
    TabMenuModule,
    ExportRoutingModule
  ],
  declarations: [
    ExportComponent
  ],
  exports: [ ExportComponent ],
} )

export class ExportModule {
}
