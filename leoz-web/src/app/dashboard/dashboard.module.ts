import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { PanelMenuModule } from 'primeng/panelmenu';
import { DropdownModule } from 'primeng/dropdown';

import { SharedModule } from '../shared/shared.module';
import { DashboardRoutingModule } from './dashboard-routing.module';

import { DashboardComponent } from './dashboard.component';
import { LeftMenuComponent } from './left-menu/left-menu.component';
import { StatusbarComponent } from './statusbar/statusbar.component';

@NgModule( {
  imports: [
    SharedModule,
    PanelMenuModule,
    DropdownModule,
    FormsModule,
    DashboardRoutingModule
  ],
  declarations: [
    DashboardComponent,
    LeftMenuComponent,
    StatusbarComponent
  ],
  exports: [ DashboardComponent ]
} )
export class DashboardModule {
}
