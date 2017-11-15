import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrderformComponent } from './orderform.component';

const routes: Routes = [
  { path: '', component: OrderformComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrderformRoutingModule {}
