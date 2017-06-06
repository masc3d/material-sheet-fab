import { Component } from '@angular/core';

@Component({
  selector: 'app-user',
  template: `
    <h2>{{'users' | translate}}</h2>
    <app-user-form></app-user-form>
    <app-user-list></app-user-list>
  `
})
export class UserComponent {}
