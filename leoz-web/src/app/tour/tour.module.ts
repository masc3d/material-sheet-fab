import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TourComponent} from './tour.component';
import {TourMapComponent} from './tour-map/tour-map.component';
import {TourDriverListComponent} from './tour-driver-list/tour-driver-list.component';
import {DriverService} from './driver.service';
import {TourService} from './tour.service';
import {YagaModule} from '@yaga/leaflet-ng2';
import { TranslateModule } from '../translate/translate.module';

@NgModule({
  imports: [
    CommonModule,
    YagaModule,
    TranslateModule
  ],
  declarations: [
    TourComponent,
    TourMapComponent,
    TourDriverListComponent,
  ],
  providers: [
    DriverService,
    TourService
  ],
  exports: [
    TourComponent
  ]
})
export class TourModule { }
