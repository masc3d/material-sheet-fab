
interface TourRouteMeta {
  distance?: number;
  drivingTime?: number;
  quality?: number;
}

export interface TourListItem {
  id?: number;
  uid?: string;
  parentId?: number;
  customId?: string;
  totalShipments?: number;
  totalPackages?: number;
  totalWeight?: number;
  optimized?: string;
  created?: string;
  time?: string;
  route?: TourRouteMeta;
  selected?: boolean;
  outdated?: boolean;
  state?: string;
  distance?: number;
  drivingTime?: number;
  isOptimizing?: boolean;
  optimizationFailed?: boolean;
  children?: TourListItem[];
}
