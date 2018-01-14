import { Parcel } from './parcel.model';

export interface DeliverylistItem {
  id: number;
  parcels: Parcel[];
}
