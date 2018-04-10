import { Moment } from 'moment';

export interface LoadinglistReportHeader {
  loadlistNo: number;
  dateFrom: Moment;
  dateTo: Moment;
  loadingAddress: string;
  hubAddress: string;
  shipmentCount: number;
  packageCount: number;
  totalWeight: number;
}
