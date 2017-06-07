export interface Driver {
  firstName: string;
  lastName: string;
  role: Driver.RoleEnum;
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
