import { NgModule } from '@angular/core';

import { ButtonModule, DropdownModule } from 'primeng/primeng';

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
