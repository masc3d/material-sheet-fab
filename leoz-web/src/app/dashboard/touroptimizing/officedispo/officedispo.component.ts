import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component( {
  selector: 'app-officedispo',
  template: `<app-dispo [withInitialGeneration]="true"></app-dispo>`,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OfficedispoComponent {
}



