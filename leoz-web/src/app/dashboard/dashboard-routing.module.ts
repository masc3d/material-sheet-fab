import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { DashboardComponent } from 'app/dashboard/dashboard.component';
import { AuthenticationGuard } from '../core/auth/authentication.guard';
import { RoleGuard } from '../core/auth/role.guard';

export const routes = [
  {
    path: '', component: DashboardComponent, canActivate: [ AuthenticationGuard ],
    children: [
      {
        path: 'home', data: [ 'Home' ],
        canActivate: [ AuthenticationGuard ],
        loadChildren: 'app/dashboard/home/home.module#HomeModule'
      },
      {
        path: 'user', data: [ 'Users' ],
        canActivate: [ AuthenticationGuard,
                       RoleGuard ],
        loadChildren: 'app/dashboard/user/user.module#UserModule'
      },
      {
        path: 'tour', data: [ 'Tour' ],
        canActivate: [ AuthenticationGuard,
                       RoleGuard ],
        loadChildren: 'app/dashboard/tour/tour.module#TourModule'
      },
      {
        path: 'stationloading', data: [ 'Stationloading' ],
        canActivate: [ AuthenticationGuard,
                       RoleGuard ],
        loadChildren: 'app/dashboard/stationloading/stationloading.module#StationloadingModule'
      },
      {
        path: 'importscan', data: [ 'importscan' ],
        canActivate: [ AuthenticationGuard,
                       RoleGuard ],
        loadChildren: 'app/dashboard/importscan/importscan.module#ImportscanModule'
      },
      {
        path: 'importscancheck', data: [ 'importscancheck' ],
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/importscan/importscan.module#ImportscanModule'
      },
      {
        path: 'deliveryscan', data: [ 'deliveryscan' ],
        canActivate: [ AuthenticationGuard,
                       RoleGuard ],
        loadChildren: 'app/dashboard/deliveryscan/deliveryscan.module#DeliveryscanModule'
      },
      {
        path: 'ipointscan', data: [ 'ipointscan' ],
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/ipointscan/ipointscan.module#IpointscanModule'
      }
    ]
  }
];
@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class DashboardRoutingModule {
}
