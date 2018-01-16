import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable()
export class AuthenticationGuard implements CanActivate {

  private versionUrl = `assets/version.json`;

  constructor( private router: Router, private http: HttpClient ) {
  }

  canActivate( route: ActivatedRouteSnapshot, state: RouterStateSnapshot ) {
    this.http.get<{ version }>( this.versionUrl, {params: new HttpParams().set( 'blanco', Date.now().toString() ) } )
      .subscribe( ( json ) => {
          const locallyStoredVersion = localStorage.getItem( 'version' );
          if(locallyStoredVersion && json.version !== locallyStoredVersion) {
            this.router.navigate( [ 'login' ] );
            window.location.reload();
          }
        },
        ( error: Response ) => console.log( error ) );
    if (localStorage.getItem( 'currentUser' )) {
      // logged in so return true
      return true;
    }
    this.router.navigate( [ 'login' ] );
    return false;
  }
}
