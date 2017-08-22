import { Injectable } from '@angular/core';
import { Http, RequestOptions, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { User } from './user.model';
import { environment } from '../../../environments/environment';
import { ApiKeyHeaderFactory } from '../../core/api-key-header.factory';
import { MsgService } from '../../shared/msg/msg.service';

@Injectable()
export class UserService {

  private userListUrl = `${environment.apiUrl}/internal/v1/user`;

  private usersSubject = new BehaviorSubject<User[]>( [] );
  public users = this.usersSubject.asObservable().distinctUntilChanged();

  private activeUserSubject = new BehaviorSubject<User>( <User> {} );
  public activeUser = this.activeUserSubject.asObservable().distinctUntilChanged();

  constructor( private http: Http,
               private msgService: MsgService ) {
  }

  insert( userData: any ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    return this.http.post( this.userListUrl,
      JSON.stringify( userData ),
      new RequestOptions( { headers: ApiKeyHeaderFactory.headers( currUser.key ) } ) );
  }

  update( userData: any, originEmail: string ): Observable<Response> {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    const queryParameters = new URLSearchParams();
    queryParameters.set( 'email', originEmail );

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      params: queryParameters
    } );
    return this.http.put( this.userListUrl,
      JSON.stringify( userData ), options );
  }

  getUsers(): void {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );

    const options = new RequestOptions( {
      headers: ApiKeyHeaderFactory.headers( currUser.key ),
      search: new URLSearchParams()
    } );

    this.http.get( this.userListUrl, options )
      .subscribe( ( response: Response ) => this.usersSubject.next( <User[]> response.json() ),
        ( error: Response ) => {
          this.msgService.handleResponse( error );
          this.usersSubject.next( <User[]> [] );
        } );
  }

  changeActiveUser( selectedUser ): void {
    this.activeUserSubject.next( selectedUser );
  }
}
