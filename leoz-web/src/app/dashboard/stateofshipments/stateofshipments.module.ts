import { NgModule } from '@angular/core';

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
import { SharedModule } from '../../shared/shared.module';
import { StateofshipmentsComponent } from './stateofshipments.component';
import { StateofshipmentsRoutingModule } from './stateofshipments-routing.module';
import { InfoComponent } from './info/info.component';

@NgModule( {
  imports: [
    SharedModule,
    DataTableModule,
    TabMenuModule,
    TabViewModule,
    DropdownModule,
    FieldsetModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule,
    StateofshipmentsRoutingModule
  ],
  declarations: [
    StateofshipmentsComponent,
    InfoComponent
  ],
  exports: [ StateofshipmentsComponent ],
  providers: []
} )
export class StateofshipmentsModule {
}
