import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ImportscanquickRoutingModule } from './importscanquick-routing.module';
import { ImportscanquickComponent } from './importscanquick.component';
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
    ImportscanquickRoutingModule,
    DataTableModule,
    TabViewModule,
    DropdownModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule
  ],
  declarations: [ImportscanquickComponent],
  exports: [ImportscanquickComponent],
  providers: [
  ]
})
export class ImportscanquickModule { }
