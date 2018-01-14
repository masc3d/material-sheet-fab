import { DeliverylistItem } from './deliverylist.item.model';

interface Info {
  id?: number;
  date?: string,
  debitorId?: number;
}

export interface Deliverylist {
  id?: number;
  date?: string,
  debitorId?: number;
  info?: Info;
  optimized?: boolean;
  time?: number;
  distance?: number;
  orders?: DeliverylistItem[];
  totalShipments?: number;
  totalPackages?: number;
  totalWeight?: number;
}
