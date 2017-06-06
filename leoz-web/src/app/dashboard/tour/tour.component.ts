import { Component } from '@angular/core';
@Component({
  selector: 'app-tour',
  template: `
    <h2>{{'tour' | translate}}</h2>
    <app-tour-driver-list></app-tour-driver-list>
    <app-tour-map></app-tour-map>
  `
})
export class TourComponent {}
