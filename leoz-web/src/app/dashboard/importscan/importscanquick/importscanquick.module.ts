import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { KeyFilterModule } from 'primeng/keyfilter';
import { FieldsetModule } from 'primeng/fieldset';
import { ToggleButtonModule } from 'primeng/primeng';

import { SharedModule } from '../../../shared/shared.module';
import { ImportscanquickRoutingModule } from './importscanquick-routing.module';
import { ImportscanquickComponent } from './importscanquick.component';
import { ImportscanquickService } from './importscanquick.service';

@NgModule( {
  imports: [
    SharedModule,
    ImportscanquickRoutingModule,
    FormsModule,
    FieldsetModule,
    TabViewModule,
    TableModule,
    DialogModule,
    ButtonModule,
    ToggleButtonModule,
    OverlayPanelModule,
    KeyFilterModule
  ],
  declarations: [
    ImportscanquickComponent
  ]
} )
export class ImportscanquickModule {
}
