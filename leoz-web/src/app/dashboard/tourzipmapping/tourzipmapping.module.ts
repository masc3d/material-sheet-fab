import { NgModule } from '@angular/core';

import {
  ButtonModule,
  CheckboxModule,
  DataScrollerModule,
  DataTableModule,
  InputTextModule
} from 'primeng/primeng';

import { SharedModule } from '../../shared/shared.module';
import { DatePipe } from '@angular/common';
import { TourzipmappingRoutingModule } from './tourzipmapping-routing.module';
import { TourzipmappingService } from './tourzipmapping.service';
import { TourzipmappingComponent } from './tourzipmapping.component';

@NgModule( {
  imports: [
    SharedModule,
    DataTableModule,
    InputTextModule,
    ButtonModule,
    CheckboxModule,
    TourzipmappingRoutingModule ,
    DataScrollerModule,
  ],
  declarations: [ TourzipmappingComponent ],
  providers: [
    TourzipmappingService,
    DatePipe,
    // use fake backend
    // fakeBackendProvider,
    // MockBackend,
    // BaseRequestOptions
  ]
} )
export class TourzipmappingModule {
}
