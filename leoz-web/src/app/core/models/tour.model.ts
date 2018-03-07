import { TourListItem } from './tour-list-item.model';
import { DeliverylistItem } from './deliverylist.item.model';
import { Address } from './address.model';

interface Stop {
  address?: Address;
}

export interface Tour extends TourListItem {
  id?: number;
  stationNo?: number;
  deliverylistId?: number;
  parentId?: number;
  date?: string;
  orders?: DeliverylistItem[];
  stops?: Stop[]
}
