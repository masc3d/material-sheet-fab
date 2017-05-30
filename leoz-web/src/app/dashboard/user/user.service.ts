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
import { ApiKeyHeaderFactory } from '../../core/api-key-header.factory';

@Injectable()
export class UserService {

  private userListUrl = `${environment.apiUrl}/internal/v1/user`;

  // private usersSubject = new BehaviorSubject<User[]>( [] );
  // public users = this.usersSubject.asObservable().distinctUntilChanged();
  private activeUserSubject = new BehaviorSubject<User>( <User> {} );
  public activeUser = this.activeUserSubject.asObservable().distinctUntilChanged();

  constructor( private http: Http ) {
  }

  insert( userData: any ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    return this.http.post( this.userListUrl,
      JSON.stringify( userData ),
      new RequestOptions( { headers: ApiKeyHeaderFactory.headers( currUser.key ) } ));
  }

  update( userData: any ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    const queryParameters = new URLSearchParams();
    queryParameters.set( 'email', userData.originEmail );

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      search: queryParameters
    } );
    return this.http.put( this.userListUrl,
      JSON.stringify( userData ), options );
  }

  getUsers() {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      search: new URLSearchParams()
    } );

    return this.http.get( this.userListUrl, options )
      .map( ( response: Response ) => <User[]> response.json() ) // this.usersSubject.next(userArr);
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
