import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ImportscanlistComponent } from './importscanlist.component';

const routes: Routes = [
  { path: '', component: ImportscanlistComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ImportscanlistRoutingModule {}
