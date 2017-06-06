import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { DashboardRoutingModule } from './dashboard-routing.module';

import { DashboardComponent } from './dashboard.component';
import { LeftMenuComponent } from './left-menu/left-menu.component';
import { PanelMenuModule } from 'primeng/primeng';

@NgModule({
  imports: [
    SharedModule,
    PanelMenuModule,
    DashboardRoutingModule
  ],
  declarations: [DashboardComponent, LeftMenuComponent],
  exports: [DashboardComponent]
})
export class DashboardModule { }
