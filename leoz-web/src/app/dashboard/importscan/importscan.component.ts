import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component( {
  selector: 'app-importscan',
  template: `
    <div style="padding: 5px">
        <router-outlet></router-outlet>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class ImportscanComponent {}
