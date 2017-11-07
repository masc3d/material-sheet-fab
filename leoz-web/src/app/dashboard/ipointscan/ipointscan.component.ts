import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component( {
  selector: 'app-ipointscan',
  template: `
    <div style="padding: 5px">
        <router-outlet></router-outlet>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class IpointscanComponent {}
