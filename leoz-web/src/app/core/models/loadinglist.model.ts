import { Exportorder } from './exportorder.model';

export interface Loadinglist {
  loadinglistNo?: number;
  orders?: Exportorder[];
  label?: string;
  loadinglistType?: string;
}

export namespace Loadinglist {
  export enum Type {
    NORMAL = <any> 'NORMAL',
    BAG = <any> 'BAG'
  }
}
