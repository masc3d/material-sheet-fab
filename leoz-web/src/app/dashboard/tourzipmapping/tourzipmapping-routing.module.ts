import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TourzipmappingComponent } from './tourzipmapping.component';

const routes: Routes = [
  { path: '', component: TourzipmappingComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TourzipmappingRoutingModule {}
