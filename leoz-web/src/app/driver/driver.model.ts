export class Driver {
  id: number;
  firstname: string;
  surname: string;
  drivernumber: number;
  tournumber: number;
  position: Position;

  toString(): string {
    return `firstname: ${this.firstname} / surname: ${this.surname}`;
  }
}

export class Position {
  lat: number;
  lng: number;

  toString(): string {
    return `lat: ${this.lat}; lng: ${this.lng}`;
  }
}
