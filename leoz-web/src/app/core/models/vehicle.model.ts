export interface Vehicle {
  capacity?: number
}

export namespace Vehicle {
  export const SPRINTER = <Vehicle>{ capacity: 1200 };
  export const CADDY = <Vehicle>{ capacity: 500 };
  export const STATION_WAGON = <Vehicle>{ capacity: 350 };
  export const BIKE = <Vehicle>{ capacity: 30 };
}
