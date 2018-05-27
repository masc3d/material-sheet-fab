export interface User {
  id?: number; // only user for drivers in tracing
  active: boolean;
  alias: string;
  debitorId: number;
  email: string;
  expiresOn: string;
  externalUser: boolean;
  firstName: string;
  lastName: string;
  password: string;
  phone: string;
  phoneMobile: string;
  role: User.RoleEnum;
  allowedStations?: number[];
}

export namespace User {
  export enum RoleEnum {
    ADMIN = <any> 'ADMIN',
    POWERUSER = <any> 'POWERUSER',
    USER = <any> 'USER',
    DRIVER = <any> 'DRIVER',
    CUSTOMER = <any> 'CUSTOMER'
  }
}

