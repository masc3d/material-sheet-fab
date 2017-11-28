import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BagscanComponent } from './bagscan.component';

const routes: Routes = [
  { path: '', component: BagscanComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BagscanRoutingModule {}
