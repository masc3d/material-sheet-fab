import { Position } from '../position.model';
import { Driver } from '../driver.model';

export interface Marker {
  position: Position;
  driver: Driver;
}

