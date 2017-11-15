import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { OrderformRoutingModule } from './orderform-routing.module';
import { OrderformComponent } from './orderform.component';
import {
ButtonModule,
CheckboxModule,
DataTableModule,
DropdownModule,
RadioButtonModule,
TabViewModule
} from 'primeng/primeng';

@NgModule({
  imports: [
    SharedModule,
    OrderformRoutingModule,
    DataTableModule,
    TabViewModule,
    DropdownModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule
  ],
  declarations: [OrderformComponent],
  exports: [OrderformComponent],
  providers: [
  ]
})
export class OrderformModule { }
