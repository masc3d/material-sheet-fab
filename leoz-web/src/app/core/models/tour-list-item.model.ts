export interface TourListItem {
  id?: number;
  totalShipments?: number;
  totalPackages?: number;
  totalWeight?: number;
  optimized?: boolean;
  time?: string;
  distance?: number;
  selected?: boolean;
}
