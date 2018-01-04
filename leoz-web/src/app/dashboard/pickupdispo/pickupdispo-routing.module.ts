import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PickupdispoComponent } from './pickupdispo.component';


const routes: Routes = [
  {
    path: '', component: PickupdispoComponent }
];

@NgModule( {
  imports: [ RouterModule.forChild( routes ) ],
  exports: [ RouterModule ]
} )
export class PickupdispoRoutingModule {
}
