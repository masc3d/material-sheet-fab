import { NgModule } from '@angular/core';

import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { FieldsetModule } from 'primeng/fieldset';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TabViewModule } from 'primeng/tabview';

import { SharedModule } from '../../shared/shared.module';
import { StateofshipmentsComponent } from './stateofshipments.component';
import { StateofshipmentsRoutingModule } from './stateofshipments-routing.module';
import { InfoComponent } from './info/info.component';

@NgModule( {
  imports: [
    SharedModule,
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
