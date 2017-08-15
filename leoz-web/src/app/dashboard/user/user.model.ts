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
  mobile: string;
  role: User.RoleEnum;

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

