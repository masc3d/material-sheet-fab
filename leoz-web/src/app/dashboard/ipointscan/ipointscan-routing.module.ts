import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IpointscanComponent } from './ipointscan.component';


const routes: Routes = [
  {
    path: '', component: IpointscanComponent,
    children: [
      {
        path: 'ipointscanquick', data: [ 'ipointscanquick' ],
        loadChildren: 'app/dashboard/ipointscan/ipointscanquick/ipointscanquick.module#IpointscanquickModule'
      },
      {
        path: 'ipointscanlist', data: [ 'ipointscanlist' ],
        loadChildren: 'app/dashboard/ipointscan/ipointscanlist/ipointscanlist.module#IpointscanlistModule'
      }
    ]
  }
];

@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class IpointscanRoutingModule {
}
