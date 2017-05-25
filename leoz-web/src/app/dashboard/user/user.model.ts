export class User {

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

  toString(): string {
    return `firstname: ${this.firstName} / surname: ${this.lastName}`;
  }
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

