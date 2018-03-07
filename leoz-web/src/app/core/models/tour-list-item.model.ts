
interface TourRouteMeta {
  distance?: number;
  drivingTime?: number;
}

export interface TourListItem {
  id?: number;
  deliverylistId?: number;
  parentId?: number;
  customId?: string;
  totalShipments?: number;
  totalPackages?: number;
  totalWeight?: number;
  optimized?: string;
  created?: string;
  time?: string;
  route?: TourRouteMeta[];
  selected?: boolean;
  outdated?: boolean;
  state?: string;
}
