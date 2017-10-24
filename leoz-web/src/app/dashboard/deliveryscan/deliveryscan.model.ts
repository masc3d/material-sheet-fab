import { Package } from '../../core/models/package.model';

export interface Deliverylist {
  DeliverylistNo: number,
  packages: Package[]
}
