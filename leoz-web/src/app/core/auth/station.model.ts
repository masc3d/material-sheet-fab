import { Address } from '../models/address.model';

export interface Station {
  stationNo: number;
  exportValuablesAllowed: boolean;
  exportValuablesWithoutBagAllowed: boolean;
  address?: Address;
}
