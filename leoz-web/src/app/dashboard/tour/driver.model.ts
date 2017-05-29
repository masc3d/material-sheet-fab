export class Driver {

  firstName: string;
  lastName: string;
  role: Driver.RoleEnum;

  toString(): string {
    return `firstname: ${this.firstName}; lastname: ${this.lastName}`;
  }
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
