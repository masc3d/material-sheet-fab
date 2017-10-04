import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { BagscanRoutingModule } from './bagscan-routing.module';
import { BagscanComponent } from './bagscan.component';
import {
  ButtonModule,
  CheckboxModule,
  DataTableModule,
  DropdownModule,
  RadioButtonModule,
  TabViewModule
} from 'primeng/primeng';
import { BagscanService } from './bagscan.service';
import { LoadinglistService } from '../loadinglistscan/loadinglist.service';

@NgModule({
  imports: [
    SharedModule,
    BagscanRoutingModule,
    DataTableModule,
    TabViewModule,
    DropdownModule,
    ButtonModule,
    CheckboxModule,
    RadioButtonModule
  ],
  declarations: [BagscanComponent],
  exports: [BagscanComponent],
  providers: [
    LoadinglistService,
    BagscanService,
    // use fake backend
    // fakeBackendProvider,
    // MockBackend,
    // BaseRequestOptions
  ]
})
export class BagscanModule { }
