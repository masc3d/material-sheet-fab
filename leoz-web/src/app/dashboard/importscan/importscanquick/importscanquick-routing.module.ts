import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ImportscanquickComponent } from './importscanquick.component';

const routes: Routes = [
  { path: '', component: ImportscanquickComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ImportscanquickRoutingModule {}
