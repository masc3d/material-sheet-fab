import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { OrderRoutingModule } from './order-routing.module';

import { OrderComponent } from './order.component';
import { TabMenuModule } from 'primeng/primeng';


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
