import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { AccordionModule } from 'ngx-bootstrap';

import { DashboardComponent } from './dashboard.component';
import { LeftMenuComponent } from './left-menu/left-menu.component';

@NgModule({
  imports: [
    SharedModule,
    AccordionModule,
    DashboardRoutingModule
  ],
  declarations: [DashboardComponent, LeftMenuComponent],
  exports: [DashboardComponent]
})
export class DashboardModule { }
