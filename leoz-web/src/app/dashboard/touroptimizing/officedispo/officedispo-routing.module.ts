import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OfficedispoComponent } from './officedispo.component';

const routes: Routes = [
  { path: '', component: OfficedispoComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OfficedispoRoutingModule {}
