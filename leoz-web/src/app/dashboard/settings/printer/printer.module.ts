import { NgModule } from '@angular/core';

import {DropdownModule} from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';

import { SharedModule } from '../../../shared/shared.module';

import { PrinterRoutingModule } from './printer-routing.module';
import { PrinterComponent } from './printer.component';

@NgModule( {
  imports: [
    SharedModule,
    DropdownModule,
    ButtonModule,
    PrinterRoutingModule
  ],
  declarations: [
    PrinterComponent
  ],
  exports: [ PrinterComponent ]
} )
export class PrinterModule {
}
