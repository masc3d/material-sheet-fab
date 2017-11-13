import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrderprofileComponent } from './orderprofile.component';

const routes: Routes = [
  { path: '', component: OrderprofileComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrderprofileRoutingModule {}
