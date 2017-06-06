import { User } from '../../dashboard/user/user.model';

export class PermissionCheck {

  private static roleSorting = [
    User.RoleEnum.Admin,
    User.RoleEnum.PowerUser,
    User.RoleEnum.User,
    User.RoleEnum.Driver,
    User.RoleEnum.Customer ];

  public static isAllowedRole( ownRole: User.RoleEnum, givenRole: User.RoleEnum ): boolean {
    return this.roleSorting.indexOf( ownRole) <= this.roleSorting.indexOf( givenRole );
  }

}
