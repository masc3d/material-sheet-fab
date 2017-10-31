import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ImportscanComponent } from './importscan.component';


const routes: Routes = [
  {
    path: '', component: ImportscanComponent,
    children: [
      {
        path: 'importscanquick', data: [ 'importscanquick' ],
        loadChildren: 'app/dashboard/importscan/importscanquick/importscanquick.module#ImportscanquickModule'
      },
      {
        path: 'importscanlist', data: [ 'importscanlist' ],
        loadChildren: 'app/dashboard/importscan/importscanlist/importscanlist.module#ImportscanlistModule'
      },
      {
        path: 'importscancheck', data: [ 'importscancheck' ],
        loadChildren: 'app/dashboard/importscan/importscancheck/importscancheck.module#ImportscancheckModule'
      }
    ]
  }
];

@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class ImportscanRoutingModule {
}
