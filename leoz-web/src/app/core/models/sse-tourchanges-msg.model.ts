import { Tour } from './tour.model';

export interface SseTourchangesMsgModel {
  stationNo?: number;
  items?: Tour[];
  deleted?: number[];
}
