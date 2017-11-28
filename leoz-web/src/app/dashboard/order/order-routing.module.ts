import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthenticationGuard } from '../../core/auth/authentication.guard';
import { OrderComponent } from './order.component';

const routes: Routes = [
  {
    path: '', component: OrderComponent,
    children: [
      {
        path: 'orderform',
        data: { preload: true },
        canActivate: [
          AuthenticationGuard ],
        loadChildren: 'app/dashboard/order/orderform/orderform.module#OrderformModule'
      },
      {
        path: 'orderlist',
        data: { preload: true },
        canActivate: [
          AuthenticationGuard ],
        loadChildren: 'app/dashboard/order/orderlist/orderlist.module#OrderlistModule'
      },
      {
        path: 'orderprofile',
        data: { preload: true },
        canActivate: [
          AuthenticationGuard ],
        loadChildren: 'app/dashboard/order/orderprofile/orderprofile.module#OrderprofileModule'
      }
    ]
  }
];

@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class OrderRoutingModule {
}
