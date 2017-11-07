import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { environment } from '../../../environments/environment';

@Injectable()
export class AuthenticationGuard implements CanActivate {

  constructor( private router: Router ) {
  }

  canActivate( route: ActivatedRouteSnapshot, state: RouterStateSnapshot ) {
    // check version
    const locallyStoredVersion = localStorage.getItem( 'version' );
    if (locallyStoredVersion
      && locallyStoredVersion === environment.version
      && localStorage.getItem( 'currentUser' )) {
      // logged in so return true
      return true;
    }
    this.router.navigate( [ 'login' ] );
    return false;
  }
}
