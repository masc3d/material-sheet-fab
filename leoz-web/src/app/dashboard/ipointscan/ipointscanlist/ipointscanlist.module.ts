import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { IpointscanlistRoutingModule } from './ipointscanlist-routing.module';
import { IpointscanlistComponent } from './ipointscanlist.component';
import {
  ButtonModule,
  CheckboxModule,
  DataScrollerModule,
  DataTableModule,
  DropdownModule,
} from 'primeng/primeng';


@NgModule({
  imports: [
    SharedModule,
    ButtonModule,
    IpointscanlistRoutingModule,
    DataTableModule,
    DropdownModule,
    CheckboxModule,
    DataScrollerModule,
  ],
  declarations: [IpointscanlistComponent],
  exports: [IpointscanlistComponent],
  providers: [
  ]
})
export class IpointscanlistModule { }
