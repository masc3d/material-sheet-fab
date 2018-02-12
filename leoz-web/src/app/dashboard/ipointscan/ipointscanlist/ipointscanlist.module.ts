import { NgModule } from '@angular/core';

import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { SharedModule } from '../../../shared/shared.module';
import { IpointscanlistRoutingModule } from './ipointscanlist-routing.module';
import { IpointscanlistComponent } from './ipointscanlist.component';
import { DataScrollerModule } from 'primeng/datascroller';

@NgModule({
  imports: [
    SharedModule,
    ButtonModule,
    IpointscanlistRoutingModule,
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
