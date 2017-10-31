import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IpointscanlistComponent } from './ipointscanlist.component';

const routes: Routes = [
  { path: '', component: IpointscanlistComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IpointscanlistRoutingModule {}
