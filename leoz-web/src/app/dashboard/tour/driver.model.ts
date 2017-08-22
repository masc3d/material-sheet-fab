export interface Driver {
  id: number;
  firstName: string;
  lastName: string;
  role: Driver.RoleEnum;
  phone: string;
  phoneMobile: string;
  email: string;
  active: boolean;
}

export namespace Driver {
  export enum RoleEnum {
    ADMIN = <any> 'ADMIN',
    POWERUSER = <any> 'POWERUSER',
    USER = <any> 'USER',
    DRIVER = <any> 'DRIVER',
    CUSTOMER = <any> 'CUSTOMER'
  }
}
