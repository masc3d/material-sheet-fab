import { Package } from '../../core/models/package.model';
export interface Exportlist {
  label?: string;
  loadlistNo: number;
  packages: Package[];
}
