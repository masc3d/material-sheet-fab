import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ImportscancheckRoutingModule } from './importscancheck-routing.module';
import { ImportscancheckComponent } from './importscancheck.component';
import {
  ButtonModule,
  DataScrollerModule,
  DataTableModule
} from 'primeng/primeng';


@NgModule({
  imports: [
    SharedModule,
    ImportscancheckRoutingModule,
    DataTableModule,
    ButtonModule,
    DataScrollerModule,
  ],
  declarations: [ImportscancheckComponent],
  exports: [ImportscancheckComponent],
  providers: [
  ]
})
export class ImportscancheckModule { }
