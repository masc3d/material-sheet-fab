import { Parcel } from './parcel.model';
import { Address } from './address.model';

export interface DeliverylistItem {
  id: number;
  deliveryAddress?: Address;
  parcels: Parcel[];
}
