import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { UserModule } from '../user/user.module';
import { TourModule } from '../tour/tour.module';
import { HomeModule } from '../home/home.module';
import { AppFooterComponent } from '../app-footer/app-footer.component';
import { AccordionModule } from 'ngx-bootstrap';
import { LeftMenuComponent } from './left-menu/left-menu.component';
import { TranslateModule } from '../translate/translate.module';

@NgModule({
  imports: [
    CommonModule,
    DashboardRoutingModule,
    UserModule,
    TourModule,
    HomeModule,
    TranslateModule,
    AccordionModule.forRoot()
  ],
  declarations: [DashboardComponent, LeftMenuComponent, AppFooterComponent],
  exports: [DashboardComponent]
})
export class DashboardModule { }
