import { Address } from './address.model';
import { Package } from './package.model';

export interface Shipment {
  deliveryAddress: Address;
  parcels: Package[];

  orderId?: number;
  senderAddress?: Address;
  senderStation?: number;
  shipmentDate?: Date;
  deliveryStation?: number;
  deliveryDate?: string;
  deliveryTime?: string;
  deliveryPos?: number;
  deliveryStatus?: number;
  deliveryCode?: number;
  optimized?: boolean
}
