import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Http, Response } from '@angular/http';
import { RequestOptions, Headers } from '@angular/http';
import { environment } from '../../../environments/environment';

@Injectable()
export class AuthenticationService {

  private authUrl = `${environment.apiUrl}/internal/v1/authorize/web`;

  constructor( private router: Router, private http: Http ) {
  }

  logout() {
    localStorage.removeItem( 'currentUser' );
    this.router.navigate( [ 'login' ] );
  }

  login( username: string, password: string ): Observable<any> {

    const headers = new Headers();
    headers.append( 'Content-Type', 'application/json' );

    const options = new RequestOptions( { headers: headers } );

    return this.http.patch( this.authUrl,
      JSON.stringify( {
        'email': `${username}`,
        'password': `${password}`
      } ), options )
      .map( ( response: Response ) => {
        if (response.status === 200) {
          localStorage.setItem( 'currentUser', JSON.stringify( response.json() ) );
        }
        return response;
      } );
  }
}
