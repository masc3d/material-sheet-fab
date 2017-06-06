export interface Driver {
  firstName: string;
  lastName: string;
  role: Driver.RoleEnum;
  email: string;
}

export namespace Driver {
  export enum RoleEnum {
    Admin = <any> 'Admin',
    PowerUser = <any> 'PowerUser',
    User = <any> 'User',
    Driver = <any> 'Driver',
    Customer = <any> 'Customer'
  }
}
