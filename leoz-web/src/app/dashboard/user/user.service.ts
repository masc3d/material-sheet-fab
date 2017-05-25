import { Injectable } from '@angular/core';
import { Headers, Http, RequestMethod, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';

import { User } from './user.model';
import { environment } from '../../../environments/environment';

@Injectable()
export class UserService {

  private userListUrl = `${environment.apiUrl}/internal/v1/user`;

  private activeUserSubject = new BehaviorSubject<User>( new User() );
  public activeUser = this.activeUserSubject.asObservable().distinctUntilChanged();

  constructor( private http: Http ) {
  }

  getUsers() {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const headers = new Headers();
    headers.append( 'Content-Type', 'application/json' );
    headers.append( 'x-api-key', currUser.key );

    const queryParameters = new URLSearchParams();
    // queryParameters.set('email', currUser.email);
    // queryParameters.set('debitor-id', currUser.debitorNo);

    const options = new RequestOptions( {
      method: RequestMethod.Get,
      headers: headers,
      search: queryParameters
    } );

    return this.http.request( this.userListUrl, options )
      .map( ( response: Response ) => {
        const userArr: User[] = [];
        response.json().forEach( function ( json ) {
          const user = Object.assign( new User(), json );
          userArr.push( user );
        } );
        return userArr;
      } )
      .catch( ( error: Response ) => this.errorHandler( error ) );
  }

  errorHandler( error: Response ) {
    console.log( error );
    return Observable.of( [] );
  }

  changeActiveUser( selectedUser ) {
    this.activeUserSubject.next( selectedUser );
  }
}
