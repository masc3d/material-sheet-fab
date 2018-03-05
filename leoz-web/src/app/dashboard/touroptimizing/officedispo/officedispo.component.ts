import { ChangeDetectionStrategy, Component } from '@angular/core';
import { DispoComponent } from '../dispo/dispo.component';

@Component( {
  selector: 'app-officedispo',
  templateUrl: './officedispo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OfficedispoComponent extends DispoComponent {
}



