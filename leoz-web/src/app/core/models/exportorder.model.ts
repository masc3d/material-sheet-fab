import { Address } from './address.model';
import { Exportparcel } from './exportparcel.model';

export interface Exportorder {
  orderId?: number;
  deliveryAddress?: Address;
  deliveryStation?: number;
  shipmentDate?: string;
  parcels?: Exportparcel[];
}
