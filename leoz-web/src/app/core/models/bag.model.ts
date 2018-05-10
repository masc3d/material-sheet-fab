import { Exportorder } from './exportorder.model';

export interface Bag {
  status?: Bag.Status;
  loadinglistNo?: number;
  unitBackLabel?: string;
  sealYellowLabel?: string;
  ordersToexport?: Exportorder[];
}

export namespace Bag {
  export enum Status {
    CLOSED_FROM_STATION = <any> 'CLOSED_FROM_STATION',
    CLOSED_FROM_HUB = <any> 'CLOSED_FROM_HUB',
    OPENED = <any> 'OPENED'
  }
}
