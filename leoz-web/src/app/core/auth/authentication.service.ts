import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Http, Response } from '@angular/http';
import { RequestOptions } from '@angular/http';
import { environment } from '../../../environments/environment';
import { RoleGuard } from './role.guard';
import { ApiKeyHeaderFactory } from '../api-key-header.factory';

@Injectable()
export class AuthenticationService {

  private authUrl = `${environment.apiUrl}/internal/v1/authorize/web`;

  constructor( private router: Router,
               private http: Http,
               private roleGuard: RoleGuard ) {
  }

  logout() {
    this.roleGuard.userRole = null;
    localStorage.removeItem( 'currentUser' );
    this.router.navigate( [ 'login' ] );
  }

  login( username: string, password: string ): Observable<any> {

    const options = new RequestOptions( { headers: ApiKeyHeaderFactory.headers() } );

    const body = JSON.stringify( {
      'email': `${username}`,
      'password': `${password}`
    } );

    return this.http.patch( this.authUrl, body, options )
      .map( ( response: Response ) => {
        if (response.status === 200) {
          const userJson = response.json();
          this.roleGuard.userRole = userJson.userRole;
          localStorage.setItem( 'currentUser', JSON.stringify( userJson ) );
        }
        return response;
      } );
  }
}
