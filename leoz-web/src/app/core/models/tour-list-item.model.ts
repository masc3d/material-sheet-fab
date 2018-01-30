export interface TourListItem {
  id?: number;
  totalShipments?: number;
  totalPackages?: number;
  totalWeight?: number;
  optimized?: string;
  created?: string;
  time?: string;
  distance?: number;
  selected?: boolean;
}
