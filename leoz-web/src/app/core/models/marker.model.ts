import { Position } from './position.model';
import { User } from './user.model';

export interface MarkerModel {
  position: Position;
  driver: User;
}

