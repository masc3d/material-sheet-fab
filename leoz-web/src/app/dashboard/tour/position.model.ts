export interface Position {
  phone: string;
  name: string;
  mobile: string;
  latitude: number;
  longitude: number;
  speed: number;
  time: string;
  vehicleType: Position.VehicleType;
}

export namespace Position {
  export enum VehicleType {
    BIKE = <any> 'BIKE',
    CAR = <any> 'CAR',
    VAN = <any> 'VAN',
    TRUCK = <any> 'TRUCK'
  }
}
