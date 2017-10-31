import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IpointscanquickComponent } from './ipointscanquick.component';

const routes: Routes = [
  { path: '', component: IpointscanquickComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IpointscanquickRoutingModule {}
