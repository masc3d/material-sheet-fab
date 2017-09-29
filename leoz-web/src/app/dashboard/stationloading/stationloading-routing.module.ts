import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StationloadingComponent } from './stationloading.component';


const routes: Routes = [
  { path: '', component: StationloadingComponent,
    children: [
      {
        path: 'loadinglistscan', data: [ 'loadinglistscan' ],
        loadChildren: 'app/dashboard/stationloading/loadinglistscan/loadinglistscan.module#LoadinglistscanModule'
      },
      {
        path: 'bagscan', data: [ 'bagscan' ],
        loadChildren: 'app/dashboard/stationloading/bagscan/bagscan.module#BagscanModule'
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StationloadingRoutingModule {}
