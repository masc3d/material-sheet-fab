import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ImportscanlistRoutingModule } from './importscanlist-routing.module';
import { ImportscanlistComponent } from './importscanlist.component';
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
    ImportscanlistRoutingModule,
    DataTableModule,
    TabViewModule,
    DropdownModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule
  ],
  declarations: [ImportscanlistComponent],
  exports: [ImportscanlistComponent],
  providers: [
  ]
})
export class ImportscanlistModule { }
