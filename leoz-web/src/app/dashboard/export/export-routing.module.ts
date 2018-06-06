import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ExportComponent } from './export.component';
import { AuthenticationGuard } from '../../core/auth/authentication.guard';
import { BagscanGuard } from '../../core/auth/bagscan.guard';


const routes: Routes = [
  {
    path: '', component: ExportComponent,
    children: [
      {
        path: 'loadinglistscan',
        data: { preload: true },
        loadChildren: 'app/dashboard/export/loadinglistscan/loadinglistscan.module#LoadinglistscanModule'
      },
      {
        path: 'bagscan',
        data: { preload: true },
        canActivate: [
          AuthenticationGuard,
          BagscanGuard
        ],
        loadChildren: 'app/dashboard/export/bagscan/bagscan.module#BagscanModule'
      }
    ]
  }
];

@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class ExportRoutingModule {
}
