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
  id?: number;
  stationNo?: number;
  deliverylistId?: number;
  parentId?: number;
  date?: string;
  orders?: DeliverylistItem[];
  stops?: Stop[];
}
