import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ImportscancheckComponent } from './importscancheck.component';

const routes: Routes = [
  { path: '', component: ImportscancheckComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ImportscancheckRoutingModule {}
