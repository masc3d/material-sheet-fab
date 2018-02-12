import { NgModule } from '@angular/core';

import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TabViewModule } from 'primeng/tabview';
import { SharedModule } from '../../../shared/shared.module';
import { ImportscanquickRoutingModule } from './importscanquick-routing.module';
import { ImportscanquickComponent } from './importscanquick.component';

@NgModule({
  imports: [
    SharedModule,
    ImportscanquickRoutingModule,
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
