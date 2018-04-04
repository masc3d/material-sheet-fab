import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-user',
  template: `
    <div style="padding-left: 8px;">
    <div class="text2button">
      {{'co-worker' | translate}}
    </div>
    </div>
    <div class="mbDashboardContent">
      <app-user-form></app-user-form>
      <app-user-list></app-user-list>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserComponent {}
