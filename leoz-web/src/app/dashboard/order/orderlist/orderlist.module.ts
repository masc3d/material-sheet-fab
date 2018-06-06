import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { OrderlistRoutingModule } from './orderlist-routing.module';
import { OrderlistComponent } from './orderlist.component';

@NgModule( {
  imports: [
    SharedModule,
    OrderlistRoutingModule
  ],
  declarations: [ OrderlistComponent ]
} )
export class OrderlistModule {
}
