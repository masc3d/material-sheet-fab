import { NgModule } from '@angular/core';

import {
  ButtonModule,
  CheckboxModule,
  DataScrollerModule,
  DataTableModule, FieldsetModule,
  InputTextModule
} from 'primeng/primeng';

import { SharedModule } from '../../shared/shared.module';
import { DatePipe } from '@angular/common';
import { TouroptimizingRoutingModule } from './touroptimizing-routing.module';
import { TouroptimizingComponent } from './touroptimizing.component';
import { TouroptimizingService } from './touroptimizing.service';

@NgModule( {
  imports: [
    SharedModule,
    DataTableModule,
    FieldsetModule,
    InputTextModule,
    ButtonModule,
    CheckboxModule,
    TouroptimizingRoutingModule,
    DataScrollerModule,
  ],
  declarations: [ TouroptimizingComponent ],
  providers: [
    TouroptimizingService,
    DatePipe,
    // use fake backend
    // fakeBackendProvider,
    // MockBackend,
    // BaseRequestOptions
  ]
} )
export class TouroptimizingModule {
}
