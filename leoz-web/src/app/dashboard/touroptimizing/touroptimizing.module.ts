import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  ButtonModule,
  CheckboxModule,
  DataScrollerModule,
  DataTableModule,
  FieldsetModule,
  InputTextModule, ToggleButtonModule
} from 'primeng/primeng';

import { SharedModule } from '../../shared/shared.module';
import { TouroptimizingRoutingModule } from './touroptimizing-routing.module';
import { TouroptimizingComponent } from './touroptimizing.component';
import { TourlistitemComponent } from './tourlistitem.component';
import { TouroptimizingService } from './touroptimizing.service';

@NgModule( {
  imports: [
    SharedModule,
    FormsModule,
    DataTableModule,
    FieldsetModule,
    InputTextModule,
    ButtonModule,
    ToggleButtonModule,
    CheckboxModule,
    TouroptimizingRoutingModule,
    DataScrollerModule,
  ],
  declarations: [
    TourlistitemComponent,
    TouroptimizingComponent
  ],
  providers: [
    TouroptimizingService
    // use fake backend
    // fakeBackendProvider,
    // MockBackend,
    // BaseRequestOptions
  ]
} )
export class TouroptimizingModule {
}
