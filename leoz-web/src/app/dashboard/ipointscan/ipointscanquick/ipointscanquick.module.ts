import { NgModule } from '@angular/core';

import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';

import { SharedModule } from '../../../shared/shared.module';
import { IpointscanquickRoutingModule } from './ipointscanquick-routing.module';
import { IpointscanquickComponent } from './ipointscanquick.component';

@NgModule({
  imports: [
    SharedModule,
    IpointscanquickRoutingModule,
    ButtonModule,
    CheckboxModule
  ],
  declarations: [IpointscanquickComponent],
  exports: [IpointscanquickComponent],
  providers: [
  ]
})
export class IpointscanquickModule { }
