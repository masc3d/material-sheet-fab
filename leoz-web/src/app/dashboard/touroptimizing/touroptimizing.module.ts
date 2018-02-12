import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ToggleButtonModule } from 'primeng/togglebutton';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { FieldsetModule } from 'primeng/fieldset';
import { InputTextModule } from 'primeng/inputtext';
import { DataScrollerModule } from 'primeng/datascroller';

import { SharedModule } from '../../shared/shared.module';
import { TouroptimizingRoutingModule } from './touroptimizing-routing.module';
import { TouroptimizingComponent } from './touroptimizing.component';
import { TourlistitemComponent } from './tourlistitem.component';
import { TouroptimizingService } from './touroptimizing.service';

@NgModule( {
  imports: [
    SharedModule,
    FormsModule,
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
