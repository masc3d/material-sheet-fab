interface Dimension {
    length: number;
    height: number;
    width: number;
    weight: number;
}

export interface Parcel extends Dimension {
  id: number;
  dimension: Dimension;
  lastDeliveryListId?: number;
}
