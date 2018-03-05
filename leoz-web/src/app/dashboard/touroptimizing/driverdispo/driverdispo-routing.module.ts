import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DriverdispoComponent } from './driverdispo.component';

const routes: Routes = [
  { path: '', component: DriverdispoComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DriverdispoRoutingModule {}
