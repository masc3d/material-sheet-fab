import { LoginComponent } from './login/login.component';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';

export const routes = [
  { path: '', redirectTo: 'dashboard/home', pathMatch: 'full' },
  { path: 'dashboard', loadChildren: 'app/dashboard/dashboard.module#DashboardModule'  },
  { path: 'login', name: [ 'Login' ], component: LoginComponent },
  { path: 'logout', data: [ 'Logout' ], component: LoginComponent },
  { path: '**', redirectTo: '' }
];
@NgModule( {
  imports: [ RouterModule.forRoot( routes, {useHash: true}) ],
  exports: [ RouterModule ]
} )
export class AppRoutingModule {
}
