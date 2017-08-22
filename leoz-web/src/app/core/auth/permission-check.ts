import { User } from '../../dashboard/user/user.model';

export class PermissionCheck {

  private static roleSorting = [
    User.RoleEnum.ADMIN,
    User.RoleEnum.POWERUSER,
    User.RoleEnum.USER,
    User.RoleEnum.DRIVER,
    User.RoleEnum.CUSTOMER ];

  public static isAllowedRole( ownRole: User.RoleEnum, givenRole: User.RoleEnum ): boolean {
    return this.roleSorting.indexOf( ownRole) <= this.roleSorting.indexOf( givenRole );
  }

  public static hasLessPermissions( ownRole: User.RoleEnum, givenRole: User.RoleEnum ): boolean {
    return this.roleSorting.indexOf( ownRole) < this.roleSorting.indexOf( givenRole );
  }

  public static myself( user: User ): boolean {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    return currUser && currUser.user.email === user.email;
  }
}
