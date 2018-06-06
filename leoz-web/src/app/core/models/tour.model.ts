import { TourListItem } from './tour-list-item.model';
import { DeliverylistItem } from './deliverylist.item.model';
import { Address } from './address.model';

interface Interval {
  from?: string;
  to?: string;
}

interface TourStopRouteMeta {
  eta?: Interval;
}

interface Task {
  orderId?: number;
}

export interface Stop {
  address?: Address;
  tasks?: Task[];
  route?: TourStopRouteMeta;
  weight?: number;
  appointmentStart?: string;
  appointmentEnd?: string;
  parcelNumbers?: string[];
}

export interface Tour extends TourListItem {
  stationNo?: number;
  date?: string;
  orders?: DeliverylistItem[];
  stops?: Stop[];
}
