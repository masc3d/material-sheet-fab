import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TouroptimizingComponent } from './touroptimizing.component';

const routes: Routes = [
  { path: '', component: TouroptimizingComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TouroptimizingRoutingModule {}
