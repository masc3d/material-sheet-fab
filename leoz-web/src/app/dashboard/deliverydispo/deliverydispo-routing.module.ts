import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DeliverydispoComponent } from './deliverydispo.component';


const routes: Routes = [
  {
    path: '', component: DeliverydispoComponent,
    children: [
      {
        path: 'deliverydispo', data: [ 'deliverydispo' ],
        loadChildren: 'app/dashboard/deliverydispo/deliverydispo.module#DeliverydispoModule'
      }
    ]
  }
];

@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class DeliverydispoRoutingModule {
}
