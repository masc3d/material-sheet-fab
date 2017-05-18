export class User {

  id: number;
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
  role: string; // TODO enum
  salt: string;

  toString(): string {
    return `firstname: ${this.firstName} / surname: ${this.lastName}`;
  }
}
