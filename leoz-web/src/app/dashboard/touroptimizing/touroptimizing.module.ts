import { NgModule } from '@angular/core';

import { TabMenuModule } from 'primeng/tabmenu';

import { SharedModule } from '../../shared/shared.module';
import { TouroptimizingRoutingModule } from './touroptimizing-routing.module';
import { TouroptimizingComponent } from './touroptimizing.component';
import { TouroptimizingService } from './touroptimizing.service';

@NgModule( {
  imports: [
    SharedModule,
    TabMenuModule,
    TouroptimizingRoutingModule
  ],
  declarations: [
    TouroptimizingComponent
  ],
  exports: [ TouroptimizingComponent ],
  providers: [TouroptimizingService ]
} )

export class TouroptimizingModule {
}

