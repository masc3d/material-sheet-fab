import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ImportscanlistRoutingModule } from './importscanlist-routing.module';
import { ImportscanlistComponent } from './importscanlist.component';
import {
  ButtonModule,
  CheckboxModule,
  DataScrollerModule,
  DataTableModule,
  DropdownModule,
  RadioButtonModule,
  TabViewModule
} from 'primeng/primeng';

@NgModule({
  imports: [
    SharedModule,
    ImportscanlistRoutingModule,
    DataTableModule,
    TabViewModule,
    DropdownModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule,
    DataScrollerModule,
  ],
  declarations: [ImportscanlistComponent],
  exports: [ImportscanlistComponent],
  providers: [
  ]
})
export class ImportscanlistModule { }
