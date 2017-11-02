export interface Package {
  parcelNo: number;
  typeOfPackaging: number;
  realWeight: number;

  orderId?: number;
  parcelPosition?: number;
  loadinglistNo?: number;
  dateOfStationOut?: Date;
  creference?: string;
  zip?: string;
  city?: string;
  devliveryStation?: number;
  deliverydate?: string;
  deliverytime?: string;
  volWeight?: number;
  length?: number;
  width?: number;
  height?: number
}
