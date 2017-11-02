import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { IpointscanquickRoutingModule } from './ipointscanquick-routing.module';
import { IpointscanquickComponent } from './ipointscanquick.component';
import {
ButtonModule,
CheckboxModule,
DataTableModule,
} from 'primeng/primeng';

@NgModule({
  imports: [
    SharedModule,
    IpointscanquickRoutingModule,
    DataTableModule,
    ButtonModule,
    CheckboxModule
  ],
  declarations: [IpointscanquickComponent],
  exports: [IpointscanquickComponent],
  providers: [
  ]
})
export class IpointscanquickModule { }
