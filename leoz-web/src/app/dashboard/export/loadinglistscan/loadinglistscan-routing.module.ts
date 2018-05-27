import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoadinglistscanComponent } from './loadinglistscan.component';

const routes: Routes = [
  { path: '', component: LoadinglistscanComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LoadinglistscanRoutingModule {}
