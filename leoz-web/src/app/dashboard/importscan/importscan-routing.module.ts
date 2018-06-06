import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ImportscanComponent } from './importscan.component';


const routes: Routes = [
  {
    path: '', component: ImportscanComponent,
    children: [
      {
        path: 'importscanquick',
        loadChildren: 'app/dashboard/importscan/importscanquick/importscanquick.module#ImportscanquickModule'
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
