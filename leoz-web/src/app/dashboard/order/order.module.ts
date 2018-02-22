import { NgModule } from '@angular/core';

import { TabMenuModule } from 'primeng/tabmenu';

import { SharedModule } from '../../shared/shared.module';

import { OrderRoutingModule } from './order-routing.module';
import { OrderComponent } from './order.component';

@NgModule( {
  imports: [
    SharedModule,
    TabMenuModule,
    OrderRoutingModule
  ],
  declarations: [
    OrderComponent
  ],
  exports: [ OrderComponent ],
} )

export class OrderModule {
}
