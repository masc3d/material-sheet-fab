import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StateofshipmentsComponent } from './stateofshipments.component';

const routes: Routes = [
  { path: '', component: StateofshipmentsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StateofshipmentsRoutingModule {}
