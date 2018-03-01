import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { OfficedispoRoutingModule } from './officedispo-routing.module';
import { OfficedispoComponent } from './officedispo.component';
import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';
import { FieldsetModule } from 'primeng/fieldset';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { DataScrollerModule } from 'primeng/datascroller';
import { TabMenuModule } from 'primeng/tabmenu';
import { TabViewModule } from 'primeng/tabview';
import { ToggleButtonModule } from 'primeng/primeng';
import { TourlistitemComponent } from '../tourlistitem.component';
import { TouroptimizingService } from '../touroptimizing.service';

@NgModule( {
  imports: [
    SharedModule,
    FormsModule,
    FieldsetModule,
    InputTextModule,
    ButtonModule,
    ToggleButtonModule,
    CheckboxModule,
    DataScrollerModule,
    TabViewModule,
    TabMenuModule,
    OfficedispoRoutingModule,
  ],
  declarations: [ OfficedispoComponent,
                  TourlistitemComponent],
  exports: [ OfficedispoComponent ],
  providers: [ TouroptimizingService
  ]
} )
export class OfficedispoModule {
}
