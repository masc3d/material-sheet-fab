import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { OrderformRoutingModule } from './orderform-routing.module';
import { OrderformComponent } from './orderform.component';
import {
  ButtonModule,
  CheckboxModule,
  DataTableModule,
  DropdownModule,
  FieldsetModule,
  RadioButtonModule,
  TabViewModule,
  TabMenuModule
} from 'primeng/primeng';
import { AddressComponent } from './address/address.component';
import { TimewindowsComponent } from './timewindows/timewindows.component';
import { ServicesComponent } from './services/services.component';
import { ContactComponent } from './contact/contact.component';

@NgModule( {
  imports: [
    SharedModule,
    OrderformRoutingModule,
    DataTableModule,
    TabMenuModule,
    TabViewModule,
    DropdownModule,
    FieldsetModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule
  ],
  declarations: [
    OrderformComponent,
    AddressComponent,
    TimewindowsComponent,
    ServicesComponent,
    ContactComponent
  ],
  exports: [ OrderformComponent ],
  providers: []
} )
export class OrderformModule {
}
