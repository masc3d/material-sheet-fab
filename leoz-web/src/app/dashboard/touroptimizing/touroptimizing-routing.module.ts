import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TouroptimizingComponent } from './touroptimizing.component';
import { AuthenticationGuard } from '../../core/auth/authentication.guard';

const routes: Routes = [
  {
    path: '', component: TouroptimizingComponent,
    children: [
      {
        path: 'officedispo',
        data: { preload: true },
        canActivate: [
          AuthenticationGuard ],
        loadChildren: 'app/dashboard/touroptimizing/officedispo/officedispo.module#OfficedispoModule'
      },
      {
        path: 'driverdispo',
        data: { preload: true },
        canActivate: [
          AuthenticationGuard ],
        loadChildren: 'app/dashboard/touroptimizing/driverdispo/driverdispo.module#DriverdispoModule'
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TouroptimizingRoutingModule {}
