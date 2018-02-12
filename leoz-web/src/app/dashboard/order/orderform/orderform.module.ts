import { NgModule } from '@angular/core';

import { TabMenuModule } from 'primeng/tabmenu';

import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { FieldsetModule } from 'primeng/fieldset';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TabViewModule } from 'primeng/tabview';

import { SharedModule } from '../../../shared/shared.module';
import { OrderformRoutingModule } from './orderform-routing.module';
import { OrderformComponent } from './orderform.component';
import { AddressComponent } from './address/address.component';
import { TimewindowsComponent } from './timewindows/timewindows.component';
import { ServicesComponent } from './services/services.component';
import { ContactComponent } from './contact/contact.component';
import { ClientComponent } from './client/client.component';
import { InfoComponent } from './info/info.component';
import { ProductspecComponent } from './productspec/productspec.component';
import { PackagesComponent } from './packages/packages.component';
import { SystemComponent } from './system/system.component';
import { PackagesRowComponent } from './packages/packages.row.component';

@NgModule( {
  imports: [
    SharedModule,
    OrderformRoutingModule,
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
    ContactComponent,
    InfoComponent,
    ClientComponent,
    ProductspecComponent,
    PackagesComponent,
    PackagesRowComponent,
    SystemComponent
  ],
  exports: [ OrderformComponent ],
  providers: []
} )
export class OrderformModule {
}
