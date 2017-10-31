import { Address } from './address.model';
import { Package } from './package.model';

export interface Shipment {
  orderId: number;
  deliveryAddress: Address;
  deliveryStation: number;
  shipmentDate: Date;
  parcels: Package[];
}
