import { ChangeDetectionStrategy, Component } from '@angular/core';
import { DispoComponent } from '../dispo/dispo.component';

@Component( {
  selector: 'app-driverdispo',
  templateUrl: './driverdispo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class DriverdispoComponent extends DispoComponent {

  withInitialGeneration = false;
}



