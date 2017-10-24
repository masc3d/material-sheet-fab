export interface Package {
  // packageId: number; // "CollieBelegNr": 1060532266,
  // // orderId?: number; // "orderId": 1060532266,
  // zip: string; // 17419,
  // city: string; // "Ahlbeck",
  // devliveryStation: number; // 20,
  // loadlistNo?: number,
  // weight: number; // "GewichtReal": "8",
  // wrapperType: number; // 4,
  // loadingDate: string;

  parcelNo: number;
  orderId: number;
  parcelPosition: number;
  ldeliverylistNo: number;
  typeOfPackaging: number;
  realWeight: number;
  dateOfStationOut: Date;
  creference: string;

  zip?: string;
  city?: string;
  devliveryStation?: number;
}
