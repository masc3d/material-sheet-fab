import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { FavouritesRoutingModule } from './favourites-routing.module';
import { FavouritesComponent } from './favourites.component';
import { DashboardboxComponent } from './dashboardbox.component';

@NgModule( {
  imports: [
    SharedModule,
    FavouritesRoutingModule
  ],
  declarations: [
    FavouritesComponent,
    DashboardboxComponent ]
} )
export class FavouritesModule {
}
