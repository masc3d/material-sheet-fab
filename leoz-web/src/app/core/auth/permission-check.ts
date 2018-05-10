import { User } from '../models/user.model';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class PermissionCheck {

  private roleSorting = [
    User.RoleEnum.ADMIN,
    User.RoleEnum.POWERUSER,
    User.RoleEnum.USER,
    User.RoleEnum.DRIVER,
    User.RoleEnum.CUSTOMER ];

  public isAllowedRole( ownRole: User.RoleEnum, givenRole: User.RoleEnum ): boolean {
    return this.roleSorting.indexOf( ownRole) <= this.roleSorting.indexOf( givenRole );
  }

  public hasLessPermissions( ownRole: User.RoleEnum, givenRole: User.RoleEnum ): boolean {
    return this.roleSorting.indexOf( ownRole) < this.roleSorting.indexOf( givenRole );
  }

  public myself( user: User ): boolean {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    return currUser && currUser.user.email === user.email;
  }
}
