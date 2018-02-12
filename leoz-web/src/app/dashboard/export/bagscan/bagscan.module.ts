import { NgModule } from '@angular/core';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TabViewModule } from 'primeng/tabview';
import { SharedModule } from '../../../shared/shared.module';
import { BagscanRoutingModule } from './bagscan-routing.module';
import { BagscanComponent } from './bagscan.component';
import { BagscanService } from './bagscan.service';

@NgModule( {
  imports: [
    SharedModule,
    BagscanRoutingModule,
    TableModule,
    TabViewModule,
    DropdownModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule
  ],
  declarations: [ BagscanComponent ],
  exports: [ BagscanComponent ],
  providers: [ BagscanService ]
} )
export class BagscanModule {
}
