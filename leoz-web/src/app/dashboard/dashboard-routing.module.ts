import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { DashboardComponent } from 'app/dashboard/dashboard.component';
import { AuthenticationGuard } from '../core/auth/authentication.guard';

export const routes = [
  {
    path: '', component: DashboardComponent, canActivate: [ AuthenticationGuard ],
    children: [
      { path: 'home', data: [ 'Home' ], canActivate: [ AuthenticationGuard ],
        loadChildren: 'app/dashboard/home/home.module#HomeModule'},
      { path: 'user', data: [ 'Users' ], canActivate: [ AuthenticationGuard ],
        loadChildren: 'app/dashboard/user/user.module#UserModule'},
      { path: 'tour', data: [ 'Tour' ],  canActivate: [ AuthenticationGuard ],
        loadChildren: 'app/dashboard/tour/tour.module#TourModule'}
    ]
  }
];
@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class DashboardRoutingModule {
}
