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
        path: 'home',
        data: { preload: true },
        canActivate: [ AuthenticationGuard ],
        loadChildren: 'app/dashboard/home/home.module#HomeModule'
      },
      {
        path: 'user',
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/user/user.module#UserModule'
      },
      {
        path: 'favourites',
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/favourites/favourites.module#FavouritesModule'
      },
      {
        path: 'tour',
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/tour/tour.module#TourModule'
      },
      {
        path: 'export',
        data: { preload: true },
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/export/export.module#ExportModule'
      },
      {
        path: 'importscan',
        data: { preload: true },
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/importscan/importscan.module#ImportscanModule'
      },
      {
        path: 'touroptimizing',
        data: { preload: true },
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/touroptimizing/touroptimizing.module#TouroptimizingModule'
      },
      {
        path: 'order',
        data: { preload: true },
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/order/order.module#OrderModule'
      },
      {
        path: 'printers',
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/settings/printer/printer.module#PrinterModule'
      },
      {
        path: 'changepassword',
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/settings/changepassword/changepassword.module#ChangepasswordModule'
      },
      {
        path: 'stateofshipments',
        canActivate: [ AuthenticationGuard,
          RoleGuard ],
        loadChildren: 'app/dashboard/stateofshipments/stateofshipments.module#StateofshipmentsModule'
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
