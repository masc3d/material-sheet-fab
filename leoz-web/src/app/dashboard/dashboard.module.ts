import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { DashboardRoutingModule } from './dashboard-routing.module';

import { DashboardComponent } from './dashboard.component';
import { LeftMenuComponent } from './left-menu/left-menu.component';
import { DropdownModule, PanelMenuModule } from 'primeng/primeng';
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    SharedModule,
    PanelMenuModule,
    DropdownModule,
    FormsModule,
    DashboardRoutingModule
  ],
  declarations: [DashboardComponent, LeftMenuComponent],
  exports: [DashboardComponent]
})
export class DashboardModule { }
