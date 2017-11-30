import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-user',
  template: `
    <h2>{{'co-worker' | translate}}</h2>
    <div class="mbDashboardContent">
      <app-user-form></app-user-form>
      <app-user-list></app-user-list>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserComponent {}
