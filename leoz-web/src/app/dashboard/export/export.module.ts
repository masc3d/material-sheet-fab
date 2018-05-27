import { NgModule } from '@angular/core';

import { TabMenuModule } from 'primeng/tabmenu';

import { SharedModule } from '../../shared/shared.module';
import { ExportRoutingModule } from './export-routing.module';

import { ExportComponent } from './export.component';

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
