import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { AuthenticationGuard } from '../auth/authentication.guard';
import { UserComponent } from '../user/user.component';
import { TourComponent } from '../tour/tour.component';
import { HomeComponent } from 'app/home/home.component';
import { DashboardComponent } from 'app/dashboard/dashboard.component';

export const routes = [
  {
    path: '', component: DashboardComponent, canActivate: [ AuthenticationGuard ],
    children: [
      { path: 'home', data: [ 'Home' ], component: HomeComponent, canActivate: [ AuthenticationGuard ] },
      { path: 'user', data: [ 'Users' ], component: UserComponent, canActivate: [ AuthenticationGuard ] },
      { path: 'tour', data: [ 'Tour' ], component: TourComponent, canActivate: [ AuthenticationGuard ] }
    ]
  }
];
@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class DashboardRoutingModule {
}
