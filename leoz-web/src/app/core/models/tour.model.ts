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

interface Stop {
  address?: Address;
  tasks?: Task[];
  route?: TourStopRouteMeta;
}

export interface Tour extends TourListItem {
  stationNo?: number;
  date?: string;
  orders?: DeliverylistItem[];
  stops?: Stop[];
}
