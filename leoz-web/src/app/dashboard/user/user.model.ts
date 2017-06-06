export interface User {

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
  role: User.RoleEnum;

}

export namespace User {
  export enum RoleEnum {
    Admin = <any> 'Admin',
    PowerUser = <any> 'PowerUser',
    User = <any> 'User',
    Driver = <any> 'Driver',
    Customer = <any> 'Customer'
  }
}

