import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component( {
  selector: 'app-driverdispo',
  template: `<app-dispo [withInitialGeneration]="false"></app-dispo>`,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class DriverdispoComponent {
}



