import { TourListItem } from './tour-list-item.model';
import { DeliverylistItem } from './deliverylist.item.model';

export interface Tour extends TourListItem {
  orders?: DeliverylistItem[];
}
