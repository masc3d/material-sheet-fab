import { NgModule } from '@angular/core';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { DataScrollerModule } from 'primeng/datascroller';

import { SharedModule } from '../../shared/shared.module';
import { DatePipe } from '@angular/common';
import { TourzipmappingRoutingModule } from './tourzipmapping-routing.module';
import { TourzipmappingService } from './tourzipmapping.service';
import { TourzipmappingComponent } from './tourzipmapping.component';

@NgModule( {
  imports: [
    SharedModule,
    TableModule,
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
