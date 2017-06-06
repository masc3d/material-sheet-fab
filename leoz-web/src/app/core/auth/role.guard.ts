import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { User } from '../../dashboard/user/user.model';

@Injectable()
export class RoleGuard implements CanActivate {

  public userRole: User.RoleEnum;
  private allowedRoutes = {
    'Customer': [],
    'Admin': [],
    'Driver': [ 'tour' ],
    'User': [
      'user',
      'tour' ],
    'PowerUser': [
      'user',
      'tour' ],
  };

  constructor( private router: Router ) {
    if (localStorage.getItem( 'currentUser' )) {
      this.userRole = JSON.parse( localStorage.getItem( 'currentUser' ) ).userRole;
    }
  }

  canActivate( route: ActivatedRouteSnapshot, state: RouterStateSnapshot ) {
    if (!this.rolePermittedForRoute( route )) {
      this.router.navigate( [ 'login' ] );
      return false;
    }
    return true;
  }

  public rolePermittedForRoute( route: ActivatedRouteSnapshot ): boolean {
    const allowedRoute = this.allowedRoutes[ this.userRole ];
    let isPermitted = false;
    if(allowedRoute) {
      isPermitted = allowedRoute.includes( route.url.join('') );
    }
    return isPermitted;
  }

  public isDriver(): boolean {
    return this.userRole === User.RoleEnum.Driver;
  }

  public isUser(): boolean {
    return this.userRole === User.RoleEnum.User;
  }

  public isPoweruser(): boolean {
    return this.userRole === User.RoleEnum.PowerUser;
  }
}
